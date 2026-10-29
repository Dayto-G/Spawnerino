SimpleSpawn **1.1.0** for **Paper 26.3**, requiring **Java 25+**.

- `/setspawn` saves your current block position and full look direction (yaw and pitch).
- `/setspawn x y z` sets coordinates and keeps your current look direction.
- `/setspawn x y z yaw [pitch]` sets coordinates and explicit angles; omitted pitch keeps your current vertical look direction.
- Coordinates support relative values such as `~`, `~5`, and `~-5`. Decimal coordinates are floored to block positions.
- Invalid numbers, incomplete arguments, unsupported world heights, and pitch outside -90 to 90 are rejected before changing spawn.
- `/deletespawn` restores that world's original spawn.
- Both commands require `simplespawn.setspawn` (operators by default).
- Saved spawns persist across server restarts. No `/spawn` command is included.

Examples: `/setspawn 100 64 -200 180 0` faces north and level; `/setspawn ~ ~ ~ 90 -15` uses your current block while facing west and slightly up.

### Upgrading from 1.0.0

Stop the server, replace the old plugin JAR with this version, keep `plugins/SimpleSpawn/spawns.yml`, and restart. Existing entries without pitch load as level (0); re-run `/setspawn` to save your preferred vertical angle. Original-spawn backups remain intact. Do not keep both plugin versions installed.

### Installation

Download the `.jar` from this release's Assets, place it in your server's `plugins` folder, and restart. Run `/setspawn` in game as an operator at a safe location.

Vanilla spawn rules and valid personal beds/respawn anchors still apply.

### Validation

Built from the tagged source against Paper API `26.3.build.230-stable`, targeting Java 25. Nine automated parsing tests pass, covering coordinates, relative offsets, angles, invalid values, and world-height limits. The workflow checks the packaged plugin class, version, and API declaration and includes a SHA-256 checksum. Live server/player gameplay testing has not been performed.
