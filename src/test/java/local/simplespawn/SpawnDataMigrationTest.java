package local.simplespawn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

final class SpawnDataMigrationTest {
    @TempDir Path root;

    @Test void copiesAllSavedDataAndKeepsOriginal() throws IOException {
        Path legacy = root.resolve("SimpleSpawn/spawns.yml");
        Path destination = root.resolve("Spawnerino/spawns.yml");
        String data = "worlds:\n  abc:\n    original: {x: 1, y: 64, z: 2, yaw: 90, pitch: -10}\n"
                + "    spawn: {x: 100, y: 67, z: 99, yaw: 26, pitch: 2}\n";
        Files.createDirectories(legacy.getParent());
        Files.writeString(legacy, data);
        assertTrue(SpawnDataMigration.copyIfMissing(legacy, destination));
        assertEquals(data, Files.readString(destination));
        assertEquals(data, Files.readString(legacy));
    }

    @Test void neverOverwritesExistingSpawnerinoData() throws IOException {
        Path legacy = root.resolve("old.yml");
        Path destination = root.resolve("new.yml");
        Files.writeString(legacy, "old data");
        Files.writeString(destination, "new data");
        assertFalse(SpawnDataMigration.copyIfMissing(legacy, destination));
        assertEquals("new data", Files.readString(destination));
    }

    @Test void missingOldDataIsANormalFreshInstall() throws IOException {
        Path destination = root.resolve("Spawnerino/spawns.yml");
        assertFalse(SpawnDataMigration.copyIfMissing(root.resolve("missing.yml"), destination));
        assertFalse(Files.exists(destination));
    }

    @Test void restartDoesNotReimportOlderData() throws IOException {
        Path legacy = root.resolve("old.yml");
        Path destination = root.resolve("Spawnerino/spawns.yml");
        Files.writeString(legacy, "initial spawn");
        assertTrue(SpawnDataMigration.copyIfMissing(legacy, destination));
        Files.writeString(destination, "updated spawn");
        assertFalse(SpawnDataMigration.copyIfMissing(legacy, destination));
        assertEquals("updated spawn", Files.readString(destination));
    }

    @Test void copyFailureIsReportedInsteadOfSilentlyStartingFresh() throws IOException {
        Path legacy = root.resolve("old.yml");
        Path blockedParent = root.resolve("blocked");
        Files.writeString(legacy, "saved spawn");
        Files.writeString(blockedParent, "not a directory");
        assertThrows(IOException.class, () -> SpawnDataMigration.copyIfMissing(legacy, blockedParent.resolve("spawns.yml")));
        assertEquals("saved spawn", Files.readString(legacy));
    }
}
