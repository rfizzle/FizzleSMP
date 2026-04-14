# Companions

Small, single-purpose Fabric mods built in-repo to patch compatibility gaps
between mods in the FizzleSMP pack. Each subdirectory is a standalone Gradle
project — build with `./gradlew build` from inside the mod's folder.

Built jars belong in the server's `mods/` directory and, when the patched
behavior involves client-synced state, in the client pack as well. Declare
their `side` in the matching plugin file entry if/when they are added to
`plugins/`.

## Mods

_No companions currently. Add a subdirectory with its own Gradle project and
list it here._
