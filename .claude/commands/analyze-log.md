Diagnose a Minecraft log and map issues to mods in the FizzleSMP modpack. Input provided as: $ARGUMENTS

The input can be one of:
1. **A file path** — e.g., `/path/to/latest.log` or `crash-2024-01-15.txt`. Read the file directly.
2. **Pasted log content** — The user may paste raw log lines directly as the argument. If the argument contains log-like content (lines with timestamps like `[HH:MM:SS]`, `[Thread/LEVEL]` markers, stack traces, or Minecraft log formatting), treat the entire argument text as the log content to analyze. Write it to a temp file (`/tmp/fizzlesmp-log-analysis.txt`) for structured processing.
3. **No input** — Check for common log locations (`modpack/logs/latest.log`, `latest.log`) and ask the user to specify if none are found.

To distinguish a file path from pasted content: if the argument is a single token that looks like a file path (starts with `/`, `./`, `~`, or contains no spaces and ends with `.log`/`.txt`), treat it as a file path. Otherwise, treat it as pasted log content.

## 1. Read & Parse the Log

- Read the log (from file or pasted content).
- If the file is very large (>2000 lines), read it in chunks, focusing on:
  - Lines containing `ERROR`, `FATAL`, or `Exception`
  - Lines containing `WARN` (especially mixin, injection, and registry warnings)
  - Stack traces (lines starting with `at ` or `Caused by:`)
  - The initial mod loading section (first ~200 lines) for mod list and load order
  - The last ~200 lines for crash/shutdown info
- If the file is a crash report (`crash-*.txt`), also extract the "Affected Level" and "System Details" sections.

## 2. Categorize Issues

Group every issue found into one of these severity levels:

### CRITICAL — Crashes & Fatal Errors
- `java.lang.NoSuchMethodError`, `NoSuchFieldError`, `NoClassDefFoundError` — binary incompatibility
- `MixinApplyError`, `MixinTargetAlreadyExists` — mixin conflicts between mods
- `StackOverflowError`, `OutOfMemoryError` — resource exhaustion
- Uncaught exceptions that halt startup or crash the game

### ERROR — Functional Failures
- `Registry` errors — duplicate IDs, missing entries
- Failed resource/data pack loading
- Mod initialization failures (`ModInitException`, `EntrypointException`)
- Network/protocol errors (version mismatches)

### WARNING — Potential Problems
- Deprecated mixin targets (works now, may break on update)
- Missing optional dependencies
- Config parse failures (mod falls back to defaults)
- Duplicate keybind registrations
- Texture/model loading warnings

### INFO — Notable but Non-Blocking
- Mod version mismatches (mod expects different MC version but loads anyway)
- Disabled features or fallback behavior
- Performance notes (slow worldgen, long tick times)

## 3. Map Issues to Mods

For each issue found:

1. **Extract the mod identity** from the log line — look for:
   - Mod ID in brackets: `[modid]`, `(modid)`, `mod 'modid'`
   - Package names: `net.fabricmc`, `com.simibubi`, etc. — map to known mod packages
   - Mixin class names: `mixin.something.SomeMixin` — identify which mod owns the mixin
   - Stack trace class paths — trace back to the originating mod
2. **Cross-reference with the pack** — Read all `plugins/*.md` files and match the mod ID or name to a listed mod.
3. **Check compatibility matrix** — Read `docs/compatibility-matrix.md` to see if this is a known issue.
4. **Flag unknown mods** — If a log error references a mod not in `plugins/*.md`, note it as an unrecognized mod (could be a dependency not explicitly listed, or an accidentally included mod).

## 4. Diagnose Root Causes

For each mapped issue, determine the likely root cause:

- **Version mismatch** — Mod expects a different MC/Fabric/dependency version
- **Mixin conflict** — Two mods patching the same method (identify both mods)
- **Missing dependency** — Required library or API mod not installed
- **Load order issue** — Mod initializing before its dependency
- **Config conflict** — Two mods claiming the same registry ID, keybind, or resource path
- **Resource exhaustion** — Too many mods, memory limits, thread contention (C2ME/Noisium)
- **Mod bug** — Issue is internal to a single mod (not a cross-mod conflict)

## 5. Recommend Remediation

For each issue, provide actionable remediation steps:

- **Version mismatch** → Specify which version to pin or update. If a pin is needed, provide the CurseForge file ID format for the plugin file's `Pin CurseForge File ID` field.
- **Mixin conflict** → Identify both mods. Check if one has a config option to disable the conflicting mixin. If not, recommend which mod to remove or replace, with alternatives.
- **Missing dependency** → Name the dependency and suggest adding it via `/add-mods`.
- **Load order issue** → Suggest adding `depends` entries or reordering in `fabric.mod.json` overrides.
- **Config conflict** → Specify which config file to edit and what value to change.
- **Resource exhaustion** → Suggest JVM args, config tweaks (e.g., C2ME thread count), or mod removal.
- **Mod bug** → Link to the mod's issue tracker if available (check Modrinth API for `source_url`). Suggest reporting upstream or pinning to a known-good version.

For CRITICAL issues, clearly state whether the pack can run at all and what must be fixed first.

## 6. Update Testing Checklist

After diagnosis, review `docs/testing.md` and determine if new test entries are warranted:

- **Add a test** if:
  - A new failure mode was discovered that isn't covered by existing tests (e.g., a specific cross-mod interaction that crashes)
  - A config-dependent behavior needs verification after changes
  - A mixin conflict was resolved by config and should be regression-tested
  - A performance issue was found under specific conditions not currently stress-tested
- **Update an existing test** if:
  - The issue adds nuance to an existing check (e.g., "also verify X doesn't happen")
  - A workaround changes expected behavior

When adding test entries:
- Place them in the most relevant existing section
- Write concrete, actionable steps (not vague "check for errors")
- Reference the specific mods and conditions involved
- If the issue involves a cross-mod interaction, add it near related tests

Ask the user for confirmation before writing changes to `docs/testing.md`.

## 7. Update Compatibility Matrix

If the log reveals a conflict between two mods that is NOT already in `docs/compatibility-matrix.md`:

- **Hard conflict** (crash/fatal) → Add to Hard Conflicts section with details
- **Soft conflict** (warning/degraded functionality) → Add to Soft Conflicts section with workaround
- Follow the signal rule — only add entries with real informational value

Ask the user for confirmation before writing changes to `docs/compatibility-matrix.md`.

## 8. Summary Report

Print a structured report:

```
## Log Analysis: <filename>

### Overview
- Total issues found: X
- Critical: X | Error: X | Warning: X | Info: X

### Critical Issues (must fix)
For each:
- **Issue:** <description>
- **Mod(s):** <mod name(s)> (<category file>)
- **Root cause:** <diagnosis>
- **Fix:** <remediation steps>

### Errors (should fix)
(same format)

### Warnings (review recommended)
(same format)

### Informational
(brief list)

### Unrecognized Mods in Log
- <mod ID> — not found in plugins/*.md (may be a transitive dependency)

### Recommended Actions (priority order)
1. <most critical fix>
2. <next fix>
...

### Testing Additions
- <new tests to add to docs/testing.md, if any>

### Compatibility Matrix Updates
- <new entries to add to docs/compatibility-matrix.md, if any>
```

After presenting the report, ask the user which remediation steps they'd like to proceed with.
