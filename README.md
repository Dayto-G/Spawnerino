# Spawnerino for Paper 26.3

Previously named SimpleSpawn. A minimal persistent world-spawn plugin with coordinate, look-direction, console, and RCON support. No `/spawn` command is included.

[Download the latest release](https://github.com/Dayto-G/Spawnerino/releases/latest)

## Install or upgrade

1. Stop your Paper **26.3** server (Java **25+**).
2. Remove the old SimpleSpawn or Spawnerino JAR and install `Spawnerino-1.3.0.jar` in `plugins/`. Keep only one version installed.
3. Keep the existing `plugins/SimpleSpawn/spawns.yml` when upgrading from SimpleSpawn.
4. Start the server. On first startup, Spawnerino copies that file into `plugins/Spawnerino/spawns.yml`, including original-spawn backups, without modifying the old file.
5. Check `/version Spawnerino`.

An existing Spawnerino data file is never overwritten by migration. If the old SimpleSpawn plugin is still installed, Spawnerino disables itself and asks you to remove the old JAR and restart. Existing entries without pitch default to level (0).

## In-game commands

```text
/setspawn
/setspawn 100 67 99
/setspawn 100 67 99 26 2
/setspawn ~10 ~ ~-5
/deletespawn
```

Plain `/setspawn` uses your current block and look direction. Coordinates keep your current yaw and pitch unless specified. Syntax: `/setspawn [x y z [yaw [pitch]]]`. Relative coordinates (`~`, `~5`, `~-5`) are supported for players; local `^` coordinates are not.

Yaw: 0 south, 90 west, -90 east, 180 north. Pitch: 0 level, -90 straight up, 90 straight down. Yaw wraps; pitch must be between -90 and 90. Decimal coordinates are floored to blocks, including negatives.

## Console and RCON

```text
setspawn world 100 67 99 26 2
setspawn world 100 67 99
deletespawn world
```

Replace `world` with a loaded world's name. Console/RCON requires a world name and absolute coordinates; omitted angles keep that world's existing spawn direction. Unknown/unloaded worlds are rejected. Command blocks and other non-player senders are not supported.

## Permissions and saved data

Both commands require `spawnerino.setspawn`, granted to operators by default. Existing grants of `simplespawn.setspawn` remain supported as a legacy alias.

With LuckPerms: `/lp user PLAYER permission set spawnerino.setspawn true`.

Each world has an independent entry, identified by UUID, in `plugins/Spawnerino/spawns.yml`. Changes save immediately and are reapplied on startup/world load. Repeated `/setspawn` preserves the original backup. `/deletespawn` restores that original spawn and removes the world's override.

This changes vanilla world spawn behavior. Valid personal beds/respawn anchors still apply; vanilla rules may place players around the spawn block. It does not teleport existing players or force respawns. Choose safe terrain. X/Z block positions must be between -29999984 and 29999984; Y must fit the world's build limits. These checks do not verify terrain safety or custom world borders.

## Build and releases

Install JDK 25+ and Maven 3.9+, then run:

```text
mvn clean verify
```

Output: `target/Spawnerino-1.3.0.jar`. Paper API `26.3.build.230-stable` is provided by the server and not bundled. The compiler targets Java 25. The Java package remains unchanged for source compatibility.

The `Build and release` workflow builds and publishes an unpublished stable version (`X.Y.Z`) when `pom.xml`, `plugin.yml`, or the workflow changes on `main`. It also supports manual runs. Each release includes the JAR and SHA-256 checksum. Published versions are skipped and never overwritten.

For a new release, update the version in both `pom.xml` and `src/main/resources/plugin.yml`, update `RELEASE-NOTES.md`, and commit the changes together to `main`. A failed draft can be resumed by rerunning the same workflow run; a draft or tag for a different commit is rejected for review.

## Verification

19 automated tests cover coordinate/angle parsing and one-time migration (original retained, new data preserved, missing legacy data, repeat startup, and copy failure). See `BUILD-VERIFICATION.txt`.

Live server/player/RCON testing has not been performed. Check permissions, setting/restoring spawn, restart persistence, console commands, invalid inputs, and legacy-data migration on your server.

## Paper version references

- https://docs.papermc.io/paper/dev/project-setup/
- https://docs.papermc.io/paper/dev/plugin-yml/
- https://docs.papermc.io/paper/getting-started/
