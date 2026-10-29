package local.simplespawn;

/** Parses command input without changing the world or touching saved data. */
record SpawnPosition(int x, int y, int z, float yaw, float pitch) {
    static SpawnPosition parseConsole(String[] args, float yaw, float pitch, int minY, int maxY) {
        if (args.length < 3 || args.length > 5) {
            throw new IllegalArgumentException("Usage: setspawn <world> <x> <y> <z> [yaw [pitch]]");
        }
        for (int i = 0; i < 3; i++) {
            if (args[i].startsWith("~")) {
                throw new IllegalArgumentException("Console/RCON requires absolute coordinates; ~ needs a player position.");
            }
        }
        return parse(args, 0, 0, 0, yaw, pitch, minY, maxY);
    }

    static SpawnPosition parse(String[] args, double baseX, double baseY, double baseZ,
                               float baseYaw, float basePitch, int minY, int maxY) {
        if (args.length != 0 && args.length != 3 && args.length != 4 && args.length != 5) {
            throw new IllegalArgumentException("Usage: /setspawn [x y z [yaw [pitch]]]");
        }
        double x = args.length == 0 ? baseX : coordinate(args[0], baseX);
        double y = args.length == 0 ? baseY : coordinate(args[1], baseY);
        double z = args.length == 0 ? baseZ : coordinate(args[2], baseZ);
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Coordinates must be finite numbers.");
        }
        double blockX = Math.floor(x);
        double blockY = Math.floor(y);
        double blockZ = Math.floor(z);
        if (blockX < -29_999_984 || blockX > 29_999_984 || blockZ < -29_999_984 || blockZ > 29_999_984) {
            throw new IllegalArgumentException("X and Z must be between -29999984 and 29999984.");
        }
        if (blockY < minY || blockY >= maxY) {
            throw new IllegalArgumentException("Y must be between " + minY + " and " + (maxY - 1) + ".");
        }
        double yaw = args.length >= 4 ? number(args[3]) : baseYaw;
        double pitch = args.length >= 5 ? number(args[4]) : basePitch;
        if (!Double.isFinite(yaw) || !Double.isFinite(pitch) || pitch < -90 || pitch > 90) {
            throw new IllegalArgumentException("Use a finite yaw and a pitch between -90 and 90.");
        }
        yaw %= 360;
        if (yaw >= 180) yaw -= 360;
        if (yaw < -180) yaw += 360;
        return new SpawnPosition((int) blockX, (int) blockY, (int) blockZ, (float) yaw, (float) pitch);
    }

    private static double coordinate(String input, double base) {
        if (!input.startsWith("~")) return number(input);
        return base + (input.length() == 1 ? 0 : number(input.substring(1)));
    }

    private static double number(String input) {
        try {
            double value = Double.parseDouble(input);
            if (Double.isFinite(value)) return value;
        } catch (NumberFormatException ignored) {
            // Report a command error instead of throwing into the server command dispatcher.
        }
        throw new IllegalArgumentException("Use finite numbers; coordinates also accept ~, ~5, or ~-5.");
    }
}
