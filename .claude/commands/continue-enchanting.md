Continue implementing the Fizzle Enchanting companion mod. Follow these steps exactly:

1. Read the following files in full:
   - `companions/fizzle-enchanting/DESIGN.md`
   - `companions/fizzle-enchanting/TODO.md`
   - `.claude/commands/dev-companion.md`

2. **Find the next Story to work on.** In `TODO.md`, scan top-to-bottom for the first line matching `- [ ] Story complete` under a `## Story S-*` heading. Record that Story's ID (e.g. `S-2.3`) and its parent Epic ID.

3. **Find the next Task within that Story.** Under the selected Story, scan its `### Task T-*` subsections in order. Pick the first Task that has any unchecked `- [ ]` item under its `Acceptance`, `Subtasks`, or `Tests` blocks. If every Task's checkboxes are already ticked but the Story's `- [ ] Story complete` line is still unchecked, skip to step 9.

4. **Read the Task's Resume context.** Every referenced path (DESIGN.md sections, Zenith/Apotheosis source files, sibling companion files) — fetch each one. If a reference points to Zenith or Apotheosis (`/home/rfizzle/Projects/Zenith/…`, `/home/rfizzle/Projects/Apotheosis/…`) and the path does not exist locally, search GitHub/Modrinth for the upstream source and extract what you need before coding — do not fabricate.

5. **Read any file the Task is going to touch.** Acceptance bullets spell out file paths (`config/FizzleEnchantingConfig.java`, `src/main/resources/data/...`, etc.). Read each that already exists so your edits match the current state.

6. **Implement the Task fully.** Satisfy every Acceptance bullet, every Subtask bullet, and every Test bullet. Follow `/dev-companion` conventions for style, package layout, logging, mixin rules, and the 1.21.1 gotchas. Write unit tests for all new logic — `fabric-loader-junit` under `src/test/java/com/fizzlesmp/fizzle_enchanting/…`.

7. **Verify the project compiles and tests pass:**
   ```
   cd companions/fizzle-enchanting && ./gradlew build
   ```
   If the build or tests fail, fix the root cause and rebuild until green. Do not weaken tests to make them pass. Do not skip hooks or tests to force a green build.

8. **Tick the completed checkboxes in TODO.md.** Flip `- [ ]` → `- [x]` on every Acceptance/Subtasks/Tests item you satisfied in this Task. Do not touch checkboxes in other Tasks or Stories.

9. **Story-close check.** After ticking Task-level boxes, if **all** Tasks within the current Story now have every Acceptance/Subtasks/Tests box ticked, flip the Story's `- [ ] Story complete` line to `- [x] Story complete`. Otherwise leave the Story status alone.

10. **Epic-close check.** If flipping the Story's status left the Epic with every Story `- [x]`, flip the Epic's `- [ ] Epic complete` line to `- [x] Epic complete`.

11. **Commit.**
    - If you closed the Story in step 9, commit with the Epic's listed **Commit at epic close** message when the Story is the last one in the Epic, OR a Story-scoped message (`feat(enchanting): <story goal>`) when earlier Stories in the Epic remain open.
    - If you did not close a Story (partial Task progress within a Story), **do not commit yet**. Leave the changes staged/unstaged for the next invocation to continue.
    - Commit message must follow Conventional Commits and include the `Co-Authored-By` line per the project's git workflow.
    - Stage only the files you touched (no `git add -A`).

12. **Report:**
    - Which Task you implemented (ID + title).
    - Which Acceptance/Subtasks/Tests items flipped.
    - Whether the Story and/or Epic closed as a result.
    - If a commit was made, its hash and message.
    - The next Task (and Story) that `/continue-enchanting` would pick up on the next run.

## Rules

- Use `/dev-companion` guidance for all code style, patterns, and conventions.
- **Do NOT skip ahead** to future Tasks — only implement the single next incomplete Task.
- **Do NOT edit DESIGN.md** or any part of TODO.md beyond ticking checkboxes per steps 8–10.
- If a Task's Resume context says "Reference needed" for Zenith or Apotheosis and the referenced path doesn't exist locally, search upstream repos before coding; never fabricate implementation details.
- If you encounter a design ambiguity not covered by DESIGN.md, **pause work** and bring it up interactively — do not guess and press on.
- If the selected Story is in Epic 9 (Post-MVP Iteration Backlog), **stop and ask** before proceeding — iteration work only runs after the MVP has shipped and been playtested.
- If build or tests fail in step 7 and you cannot identify the root cause within a few iterations, stop and report the blocker rather than forcing a workaround.
