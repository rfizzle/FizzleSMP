# Enchantment Reimplementation — Interactive Prompt

Copy everything below the line into a new Claude Code session.

---

You are reimplementing the enchantment system for the **Meridian** Fabric mod
(Minecraft 1.21.1). Two files govern your work:

- **`companions/meridian/docs/ENCHANT_SPEC.md`** — Design spec. The ONLY source
  of truth for names, IDs, concepts, weights, treasure status, item
  applicability, exclusive sets, config behavior, and client UI requirements.
- **`companions/meridian/docs/ENCHANT_TODO.md`** — Implementation checklist
  organized into phases (0–5). Every task is a checkbox.

**Read both files now** before doing anything else.

## How to work

1. **One task at a time.** Pick the next unchecked item in the TODO, implement
   it, check it off, then **stop and ask me** before continuing to the next
   item. Do not batch multiple tasks or skip ahead.
2. **Phase order is strict.** Finish every task in Phase 0 before starting
   Phase 1, and so on.
3. **Show your work.** After completing each task, briefly state what you did
   and what the next unchecked task is.
4. **Build after each sub-group.** When a logical group of tasks is done (e.g.,
   all attribute-based enchantments), run `./gradlew build` and report the
   result before moving on.
5. **Work from the spec only.** Do not read or reference any third-party mod
   source files. Do not copy values from old enchantment JSONs. All data must
   be authored fresh from the spec's concepts.
6. **Two lang keys per enchantment.** Every enchantment needs
   `enchantment.meridian.<id>` (name) and `enchantment.meridian.<id>.desc`
   (inline tooltip). Descriptions must be original text.
7. **Use skills when relevant:** `/dev-companion` for Fabric mod guidance,
   `/gradle-builds` for build/test commands, `/fabric-testing` for test
   structure decisions.

## Build commands

```bash
./gradlew build              # Build
./gradlew test               # Unit tests
./gradlew runGametest        # In-world integration tests
./gradlew runClient          # Dev client for manual testing
```

## Start

Read the two files, then tell me which task you'll do first (it should be the
first unchecked item in Phase 0). Wait for my go-ahead before executing.
