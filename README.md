# Spawnerino

A lightweight Paper plugin to set the world spawn location. Includes /setspawn and /deletespawn, with no **/spawn **command or teleport features.

## Installation

1. [Download the latest JAR](https://github.com/Dayto-G/Spawnerino/releases/latest).
2. Stop your server, put the JAR in `plugins/`, and remove any older Spawnerino or SimpleSpawn JAR.
3. Start your server.

## Commands

| In game | Action |
| --- | --- |
| `/setspawn` | Set spawn to your position and look direction. |
| `/setspawn <x> <y> <z> [yaw] [pitch]` | Set spawn using coordinates and optional look angles. Supports `~` coordinates. |
| `/deletespawn` | Restore the world's original spawn. |

Example: `/setspawn 100 67 99 26 2`. Yaw controls horizontal direction; pitch controls looking up/down (-90 to 90).

**Console / RCON:** include the loaded world's name and use absolute coordinates:

```text
setspawn world 100 67 99 26 2
deletespawn world
```

Both commands require **`spawnerino.setspawn`** (operators have access by default).

This sets world spawn; beds and respawn anchors still apply. There is no `/spawn` teleport command.

## Build

With JDK 25+ and Maven 3.9+: `mvn clean verify`. The JAR is created in `target/`.
