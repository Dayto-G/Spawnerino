package local.simplespawn;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class SpawnPositionTest {
    @Test void consoleAcceptsExplicitCoordinatesAndBothLookAngles() {
        assertEquals(new SpawnPosition(100, 67, 99, 26, 2),
                SpawnPosition.parseConsole(new String[]{"100", "67", "99", "26", "2"}, 0, 0, -64, 320));
    }

    @Test void consoleKeepsWorldSpawnAnglesWhenOmitted() {
        assertEquals(new SpawnPosition(100, 67, 99, 45, -20),
                SpawnPosition.parseConsole(new String[]{"100", "67", "99"}, 45, -20, -64, 320));
        assertEquals(new SpawnPosition(100, 67, 99, 26, -20),
                SpawnPosition.parseConsole(new String[]{"100", "67", "99", "26"}, 45, -20, -64, 320));
    }

    @Test void consoleRejectsMissingCoordinatesAndExtraArguments() {
        assertThrows(IllegalArgumentException.class, () ->
                SpawnPosition.parseConsole(new String[]{}, 0, 0, -64, 320));
        assertThrows(IllegalArgumentException.class, () ->
                SpawnPosition.parseConsole(new String[]{"100", "67"}, 0, 0, -64, 320));
        assertThrows(IllegalArgumentException.class, () ->
                SpawnPosition.parseConsole(new String[]{"100", "67", "99", "26", "2", "extra"}, 0, 0, -64, 320));
    }

    @Test void consoleRejectsRelativeCoordinatesOnEveryAxis() {
        for (int axis = 0; axis < 3; axis++) {
            String[] args = {"100", "67", "99"};
            args[axis] = "~1";
            assertThrows(IllegalArgumentException.class, () -> SpawnPosition.parseConsole(args, 0, 0, -64, 320));
        }
    }

    @Test void consoleUsesTheSameHeightAndAngleValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                SpawnPosition.parseConsole(new String[]{"100", "320", "99"}, 0, 0, -64, 320));
        assertThrows(IllegalArgumentException.class, () ->
                SpawnPosition.parseConsole(new String[]{"100", "67", "99", "NaN"}, 0, 0, -64, 320));
        assertThrows(IllegalArgumentException.class, () ->
                SpawnPosition.parseConsole(new String[]{"100", "67", "99", "26", "91"}, 0, 0, -64, 320));
    }

    private SpawnPosition parse(String... args) {
        return SpawnPosition.parse(args, 10.75, 64.5, -10.25, 45, -20, -64, 320);
    }

    @Test void currentPositionIncludesBothLookAngles() {
        assertEquals(new SpawnPosition(10, 64, -11, 45, -20), parse());
    }

    @Test void coordinatesKeepCurrentLookAndFloorNegativeFractions() {
        assertEquals(new SpawnPosition(-2, 70, 3, 45, -20), parse("-1.2", "70.9", "3"));
    }

    @Test void relativeCoordinatesResolveBeforeFlooring() {
        assertEquals(new SpawnPosition(10, 66, -14, 45, -20), parse("~", "~2", "~-3"));
    }

    @Test void explicitYawWrapsAndOptionalPitchKeepsCurrentPitch() {
        assertEquals(new SpawnPosition(1, 64, 2, -90, -20), parse("1", "64", "2", "630"));
        assertEquals(90, parse("1", "64", "2", "-630").yaw());
    }

    @Test void explicitPitchIncludesVerticalLimits() {
        assertEquals(90, parse("1", "64", "2", "0", "90").pitch());
        assertEquals(-90, parse("1", "64", "2", "0", "-90").pitch());
    }

    @Test void rejectsIncompleteOrExtraArguments() {
        assertThrows(IllegalArgumentException.class, () -> parse("1"));
        assertThrows(IllegalArgumentException.class, () -> parse("1", "64"));
        assertThrows(IllegalArgumentException.class, () -> parse("1", "64", "2", "0", "0", "extra"));
    }

    @Test void rejectsInvalidOrNonFiniteNumbers() {
        for (String input : new String[]{"NaN", "Infinity", "-Infinity", "1e999", "abc", "^1"}) {
            assertThrows(IllegalArgumentException.class, () -> parse(input, "64", "2"));
            assertThrows(IllegalArgumentException.class, () -> parse("1", "64", "2", input));
            assertThrows(IllegalArgumentException.class, () -> parse("1", "64", "2", "0", input));
        }
        assertThrows(IllegalArgumentException.class, () -> parse("~NaN", "64", "2"));
    }

    @Test void enforcesWorldHeightBounds() {
        assertEquals(-64, parse("1", "-64", "2").y());
        assertEquals(319, parse("1", "319.99", "2").y());
        assertThrows(IllegalArgumentException.class, () -> parse("1", "-64.01", "2"));
        assertThrows(IllegalArgumentException.class, () -> parse("1", "320", "2"));
        assertThrows(IllegalArgumentException.class, () ->
                SpawnPosition.parse(new String[]{"1", "128", "2"}, 0, 0, 0, 0, 0, 0, 128));
    }

    @Test void rejectsInvalidPitchAndExtremeCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> parse("1", "64", "2", "0", "90.1"));
        assertThrows(IllegalArgumentException.class, () -> parse("1", "64", "2", "0", "-90.1"));
        assertThrows(IllegalArgumentException.class, () -> parse("30000000", "64", "2"));
        assertThrows(IllegalArgumentException.class, () -> parse("1", "64", "-30000000"));
        assertThrows(IllegalArgumentException.class, () -> parse("1e308", "64", "2"));
    }
}
