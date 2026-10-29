Minimal persistent world-spawn plugin for **Paper 26.3**, requiring **Java 25+**.

- `/setspawn` saves the current world's spawn block and facing direction.
- `/deletespawn` restores that world's original spawn.
- Both commands require `simplespawn.setspawn` (operators by default).
- Saved spawns persist across server restarts. No `/spawn` command is included.

### Installation

Download the `.jar` from this release's Assets, place it in your server's `plugins` folder, and restart. Run `/setspawn` in game as an operator at a safe location.

Vanilla spawn rules and valid personal beds/respawn anchors still apply.

### Validation

Built from the tagged source against Paper API `26.3.build.230-stable`, targeting Java 25. The workflow checks the packaged plugin class, version, and API declaration and includes a SHA-256 checksum. Live server/player gameplay testing has not been performed.
