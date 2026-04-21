#!/usr/bin/env bash
#
# Drive `/continue-enchanting` in a loop inside a Docker sandbox.
#
# Each iteration spins up a fresh container (fresh Claude Code context),
# runs `claude -p "/continue-enchanting"` once, then inspects the repo to
# decide whether to keep looping.
#
# Halt conditions:
#   - No `- [ ] Story complete` remaining outside Epic 9 (MVP done).
#   - Iteration produced no commit AND no checkbox movement (blocker:
#     build failure, design ambiguity, or Epic 9 gate — human should look).
#   - MAX_ITERATIONS exhausted.
#
# Usage:
#   scripts/continue-enchanting-loop.sh                  # run the loop
#   scripts/continue-enchanting-loop.sh --rebuild        # force image rebuild first
#   MAX_ITERATIONS=10 scripts/continue-enchanting-loop.sh
#
# Requirements on the host:
#   - docker
#   - ~/.claude/ and ~/.claude.json populated (run `claude` once interactively first)
#   - The three projects side-by-side at:
#         /home/rfizzle/Projects/FizzleSMP
#         /home/rfizzle/Projects/Zenith
#         /home/rfizzle/Projects/Apotheosis

set -euo pipefail

# ---- paths --------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROJECTS_ROOT="$(cd "${REPO_ROOT}/.." && pwd)"
MOD_DIR="${REPO_ROOT}/companions/fizzle-enchanting"
TODO_FILE="${MOD_DIR}/TODO.md"

IMAGE_TAG="${IMAGE_TAG:-fizzle-enchanting-loop:latest}"
GRADLE_VOLUME="${GRADLE_VOLUME:-fizzle-enchanting-gradle-cache}"
MAX_ITERATIONS="${MAX_ITERATIONS:-50}"
LOG_DIR="${LOG_DIR:-${MOD_DIR}/.continue-enchanting-logs}"

# ---- flags --------------------------------------------------------------

REBUILD=0
for arg in "$@"; do
    case "$arg" in
        --rebuild) REBUILD=1 ;;
        -h|--help)
            sed -n '2,30p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
            exit 0
            ;;
        *) echo "unknown flag: $arg" >&2; exit 2 ;;
    esac
done

# ---- sanity checks ------------------------------------------------------

command -v docker >/dev/null || { echo "docker not found on PATH" >&2; exit 1; }

for p in FizzleSMP Zenith Apotheosis; do
    if [[ ! -d "${PROJECTS_ROOT}/${p}" ]]; then
        echo "missing ${PROJECTS_ROOT}/${p} — mount layout assumes all three side-by-side" >&2
        exit 1
    fi
done

if [[ ! -f "${HOME}/.claude.json" ]] || [[ ! -d "${HOME}/.claude" ]]; then
    echo "~/.claude/ or ~/.claude.json missing — run \`claude\` once on the host to populate auth" >&2
    exit 1
fi

mkdir -p "${LOG_DIR}"

# ---- image build --------------------------------------------------------

if [[ "$REBUILD" == "1" ]] || ! docker image inspect "$IMAGE_TAG" >/dev/null 2>&1; then
    echo "==> Building image ${IMAGE_TAG}"
    docker build \
        --build-arg "USER_UID=$(id -u)" \
        --build-arg "USER_GID=$(id -g)" \
        -f "${SCRIPT_DIR}/continue-enchanting.Dockerfile" \
        -t "$IMAGE_TAG" \
        "${SCRIPT_DIR}"
fi

docker volume inspect "$GRADLE_VOLUME" >/dev/null 2>&1 || docker volume create "$GRADLE_VOLUME" >/dev/null

# ---- helpers ------------------------------------------------------------

# Count open stories in Epics 1–8 (skip Epic 9 by design).
remaining_stories() {
    awk '
        /^# Epic 9/   { skip=1; next }
        /^# Epic [0-9]+/ { skip=0; next }
        !skip && /^- \[ \] Story complete/ { count++ }
        END { print count+0 }
    ' "$TODO_FILE"
}

todo_hash() { sha1sum "$TODO_FILE" | awk '{print $1}'; }
repo_head() { git -C "$REPO_ROOT" rev-parse HEAD; }

run_one_iteration() {
    local log="$1"

    docker run --rm \
        --user "$(id -u):$(id -g)" \
        -v "${PROJECTS_ROOT}/FizzleSMP:/home/rfizzle/Projects/FizzleSMP" \
        -v "${PROJECTS_ROOT}/Zenith:/home/rfizzle/Projects/Zenith:ro" \
        -v "${PROJECTS_ROOT}/Apotheosis:/home/rfizzle/Projects/Apotheosis:ro" \
        -v "${HOME}/.claude:/home/rfizzle/.claude" \
        -v "${HOME}/.claude.json:/home/rfizzle/.claude.json" \
        -v "${HOME}/.gitconfig:/home/rfizzle/.gitconfig:ro" \
        -v "${GRADLE_VOLUME}:/home/rfizzle/.gradle" \
        -w "/home/rfizzle/Projects/FizzleSMP" \
        -e HOME=/home/rfizzle \
        "$IMAGE_TAG" \
        claude --dangerously-skip-permissions -p "/continue-enchanting" \
        2>&1 | tee "$log"
}

# ---- main loop ----------------------------------------------------------

iteration=0
while (( iteration < MAX_ITERATIONS )); do
    iteration=$((iteration + 1))

    remaining=$(remaining_stories)
    if [[ "$remaining" == "0" ]]; then
        echo "==> All MVP stories (Epics 1–8) complete. Stopping."
        exit 0
    fi

    timestamp="$(date +%Y%m%d-%H%M%S)"
    log="${LOG_DIR}/iter-${timestamp}.log"

    echo "==> Iteration ${iteration}/${MAX_ITERATIONS}  |  ${remaining} MVP stories remaining  |  log: ${log}"

    prev_head="$(repo_head)"
    prev_todo="$(todo_hash)"

    if ! run_one_iteration "$log"; then
        echo "==> claude CLI exited non-zero (see ${log}). Stopping."
        exit 1
    fi

    new_head="$(repo_head)"
    new_todo="$(todo_hash)"

    if [[ "$new_head" == "$prev_head" && "$new_todo" == "$prev_todo" ]]; then
        echo "==> No commit and no TODO checkbox movement — treating as blocker."
        echo "    Most likely: build failure, design ambiguity, or Epic 9 gate."
        echo "    Inspect ${log} and resume manually."
        exit 2
    fi

    if [[ "$new_head" != "$prev_head" ]]; then
        echo "==> Commit landed: $(git -C "$REPO_ROOT" log -1 --oneline)"
    else
        echo "==> Partial task progress (checkboxes moved, no commit yet)."
    fi
done

echo "==> Reached MAX_ITERATIONS=${MAX_ITERATIONS} without completing all MVP stories."
exit 1
