# Sandbox for running `/continue-enchanting` iterations headlessly.
#
# Projects are bind-mounted at their *host* absolute paths so the absolute
# references in TODO.md and DESIGN.md (`/home/rfizzle/Projects/Zenith/...`,
# `/home/rfizzle/Projects/Apotheosis/...`) resolve unchanged.

FROM eclipse-temurin:21-jdk-jammy

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
 && apt-get install -y --no-install-recommends \
        git curl ca-certificates make jq unzip xz-utils \
 && rm -rf /var/lib/apt/lists/*

# Node 20 (for the Claude Code CLI).
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
 && apt-get install -y --no-install-recommends nodejs \
 && rm -rf /var/lib/apt/lists/*

RUN npm install -g @anthropic-ai/claude-code

# Create a non-root user matching the host so bind mounts write cleanly and
# `claude --dangerously-skip-permissions` works (it refuses under root).
ARG USER_UID=1000
ARG USER_GID=1000
RUN groupadd --gid "${USER_GID}" rfizzle \
 && useradd --uid "${USER_UID}" --gid "${USER_GID}" --create-home --shell /bin/bash rfizzle

USER rfizzle
ENV HOME=/home/rfizzle
ENV GRADLE_USER_HOME=/home/rfizzle/.gradle

# Pre-create the gradle dir so a freshly-created named volume inherits
# rfizzle ownership on first mount.
RUN mkdir -p /home/rfizzle/.gradle

# Default working dir matches the host layout — the loop script also sets -w
# explicitly, this is just a safe fallback.
WORKDIR /home/rfizzle/Projects/FizzleSMP
