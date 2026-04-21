Continue the fizzle-enchanting test migration.

1. Read `companions/fizzle-enchanting/TESTING-TODO.md` and follow its Ordering
   section. Also read `companions/fizzle-enchanting/TODO.md` (for what each
   TEST-X.Y.Z is actually asserting) and `companions/fizzle-enchanting/DESIGN.md`
   (for expected values). The `fabric-testing` skill will load automatically
   when you edit a test file; `dev-companion` applies for all code style.

2. Pick the next target: first Phase in Ordering with any `[ ]` rows, then the
   first numbered item in that phase. Phase 0 (S-0) is blocking — do it first
   if any S-0 row is still `[ ]`.

3. Do the whole story in one iteration. Never half-migrate — if a story
   doesn't fit, pick a smaller one. Skip T1-pure / T1-pseudo rows (already
   correct).

4. Verify with `./gradlew :companions:fizzle-enchanting:test` (Tier 1/2) and
   `runGametest` (Tier 3). Don't weaken assertions to make things pass.

5. Tick the `[ ]` → `[x]` boxes in TESTING-TODO.md for everything you
   completed. Update `**File:**` paths if tests moved to `src/gametest/`.

6. Commit per story, staging only touched files:
   - Pure migration: `refactor(test): migrate S-X.Y to Tier N`
   - Tier 3 rewrite: `refactor(test): rewrite S-X.Y as gametest`
   - New coverage: `test(enchanting): add Tier N coverage for S-X.Y`
   - S-0 infra: `refactor(test): wire fabric-loader-junit and gametest source set`

   Don't bundle migration with new assertions or fixes — separate commits.

7. Report: story + rows completed, test counts, commit hash, next target.

Stop and ask if: a row needs re-classification (e.g. T2-migrate-unfreeze
turns out to need mod content → T3-rewrite), a story references Epic 6+ code
that isn't built yet, or the build fails and the root cause isn't obvious
within a few attempts.
