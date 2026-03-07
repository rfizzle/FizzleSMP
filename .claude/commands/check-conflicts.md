Perform a full conflict analysis across the FizzleSMP modpack:

1. **Gather Data**
   - Read all `plugins/*.md` files and extract every mod listed.
   - Read `docs/compatibility-matrix.md` for known hard/soft conflicts.

2. **Known Conflict Check**
   - Cross-reference all mods against the hard and soft conflict tables.
   - Report any mod pairs that appear in the hard conflict list — these are **blockers**.
   - Report any mod pairs that appear in the soft conflict list — these are **warnings** with their noted resolutions.

3. **Functional Overlap Detection**
   - Group mods by category.
   - Within each category, identify mods whose Summary or Why fields suggest they do the same thing (e.g., two minimap mods, two performance mods targeting the same subsystem).
   - Flag these as **suspected overlaps** for manual review.

4. **Cross-Category Overlap**
   - Look for mods in different categories that might conflict:
     - Multiple mods touching world generation.
     - Multiple mods modifying combat mechanics.
     - Multiple mods altering the same UI elements (HUD, inventory).
   - Flag these as **cross-category risks**.

5. **Missing Matrix Entries**
   - For every pair of mods that does NOT appear in the compatibility matrix at all, list them as **untested pairs** that should be verified.
   - Prioritize pairs that touch the same game systems.

6. **Report**
   - Print a structured report with sections: Blockers, Warnings, Suspected Overlaps, Cross-Category Risks, Untested Pairs.
   - For each issue, name both mods and explain the concern.
   - If there are zero mods listed, say so and remind the user to start adding mods.