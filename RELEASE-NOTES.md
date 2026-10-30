# Spawnerino 1.3.0

SimpleSpawn is now **Spawnerino**. Requires Paper **26.3** and Java **25+**.

- New plugin name, JAR filename, data folder, and permission: `spawnerino.setspawn`.
- Existing `simplespawn.setspawn` permission grants continue to work.
- First startup copies `plugins/SimpleSpawn/spawns.yml` into `plugins/Spawnerino/spawns.yml`, including original-spawn backups. The old file is retained; an existing Spawnerino file is never overwritten.
- Installing both old and new plugins causes Spawnerino to disable itself with an explanatory log message.
- All coordinate, yaw/pitch, relative-position, console, and RCON commands remain available.

## Upgrade

Stop the server, remove the old SimpleSpawn JAR, install this release's Spawnerino JAR, keep the old data folder, and start the server. Check `/version Spawnerino`. Do not keep both plugins installed.

## Examples

In game: `/setspawn`, `/setspawn 100 67 99 26 2`, `/deletespawn`.

RCON: `setspawn world 100 67 99 26 2` and `deletespawn world`.

## Validation

Built against Paper API `26.3.build.230-stable`, targeting Java 25. All 19 automated tests pass, including five data-migration tests. Live server/player/RCON testing has not been performed.
