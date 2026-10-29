# SimpleSpawn for Paper 26.3

Minimal world-spawn plugin. No `/spawn` command is included.

[Download the latest release](https://github.com/Dayto-G/SimpleSpawn/releases/latest)

## Install

1. Run Paper **26.3** with **Java 25 or newer**.
2. Put `SimpleSpawn-1.0.0.jar` in the server's `plugins` folder.
3. Restart the server.
4. As an operator, stand at a safe location and run `/setspawn`.

## Commands and permission

- `/setspawn`: save your current block position and yaw as this world's vanilla spawn.
- `/deletespawn`: restore the spawn that existed before the first `/setspawn` in this world. Repeating `/setspawn` preserves that original backup.
- Both require `simplespawn.setspawn`, granted to operators by default. With LuckPerms: `/lp user PLAYER permission set simplespawn.setspawn true`.
- Both commands must be run by a player and take no arguments.

Each world has a separate entry, identified by world UUID, in `plugins/SimpleSpawn/spawns.yml`. Changes save immediately and are reapplied on startup or world load. Deleting restores the previous spawn and removes that world's saved override. Keep this data file if you want to retain the original backup.

This changes vanilla world spawn behavior: new players and eligible respawning players use it. Existing valid beds/respawn anchors still apply. Vanilla spawn rules can place players around the spawn block; it is not an exact-position teleport. It does not teleport existing players, force respawns, or change personal spawn points. Choose a safe spawn site. Pitch and fractional coordinates are not part of vanilla world spawn storage.

## Build

Install JDK 25+ and Maven 3.9+, then run from this folder:

```
mvn clean package
```

Output: `target/SimpleSpawn-1.0.0.jar`. Paper API is provided by the server and is not bundled.

The project pins the verified published API `io.papermc.paper:paper-api:26.3.build.230-stable` and declares `api-version: '26.3'`. The compiler targets Java 25.

## Manual server checks

1. With no permission, confirm both commands are denied.
2. As an operator, note the original world spawn, run `/setspawn`, and check the saved world entry in `spawns.yml`.
3. Restart the server and confirm the world spawn remains at the saved block and yaw.
4. Run `/setspawn` at a second location, then `/deletespawn`; confirm the original spawn is restored, including after restarting.
5. Run `/deletespawn` again; expect the no-saved-spawn message.
6. Repeat in another world; confirm entries are independent.
7. Try the commands from the console; expect the in-game-only message.

## Version references (checked 2026-10-29)

- https://docs.papermc.io/paper/dev/project-setup/
- https://docs.papermc.io/paper/dev/plugin-yml/
- https://docs.papermc.io/paper/getting-started/
- https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/maven-metadata.xml

## Verification

See `BUILD-VERIFICATION.txt` for the build result. Live gameplay checks require a Paper server and player session.

## Automatic releases

The `Build and release` workflow runs on `main` when `pom.xml`, `plugin.yml`, or the workflow changes. It also supports manual runs from Actions. An unpublished stable version (`X.Y.Z`) is built with Java 25 and published as `vX.Y.Z`, with a JAR and SHA-256 checksum. Published versions are skipped and never overwritten.

For the next release, update the version in **both** `pom.xml` and `src/main/resources/plugin.yml`, update `RELEASE-NOTES.md` for the change (including testing status), and commit them together to `main`. The initial workflow installation publishes v1.0.0. The job uses GitHub's built-in token with repository contents write permission; no extra token secret is required.

If publishing fails after creating a draft, rerun the same workflow run to resume it. A draft or tag belonging to a different commit is rejected for review. Build details and failures are available under Actions. Gameplay testing remains a separate manual check.
