package local.simplespawn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** One-time import that preserves the old file and never replaces new spawn data. */
final class SpawnDataMigration {
    private SpawnDataMigration() {}

    static boolean copyIfMissing(Path legacy, Path destination) throws IOException {
        if (Files.exists(destination) || !Files.isRegularFile(legacy)) return false;
        Files.createDirectories(destination.getParent());
        Files.copy(legacy, destination);
        return true;
    }
}
