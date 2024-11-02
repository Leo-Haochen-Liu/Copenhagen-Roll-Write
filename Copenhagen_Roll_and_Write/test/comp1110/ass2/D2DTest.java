package comp1110.ass2;

import comp1110.ass2.Dice;
import comp1110.ass2.FacadeTile;
import comp1110.ass2.Game;
import comp1110.ass2.Player;
import comp1110.ass2.Grid;
import comp1110.ass2.gui.Placement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class D2DTest {

    @Test
    public void testGetPossibleTiles() {
        Game game = new Game();
        game.registerPlayer(2, new boolean[]{false, false});
        Dice dice = game.getDice();
        // Retrieve the first player directly, assuming access by ID works this way:

        // Example 1: Three red, one blue, one white dice
        dice.setResults(new Dice.Color[]{
                Dice.Color.RED, Dice.Color.RED, Dice.Color.RED, Dice.Color.BLUE, Dice.Color.WHITE
        });
        List<String> possibleTiles = game.getPossibleTiles();
        System.out.println(possibleTiles);
        assertTrue(possibleTiles.contains("R3"));
        assertTrue(possibleTiles.contains("R4"));
        assertFalse(possibleTiles.contains("B3"));

        // Example 2: All dice of the same color (Blue)
        dice.setResults(new Dice.Color[]{
                Dice.Color.BLUE, Dice.Color.BLUE, Dice.Color.BLUE, Dice.Color.BLUE, Dice.Color.BLUE
        });
        possibleTiles = game.getPossibleTiles();
        assertTrue(possibleTiles.contains("B5"));  // Largest tile available
        assertFalse(possibleTiles.contains("R1")); // Wrong color

        // Example 3: Mixed colors with multiple white dice
        dice.setResults(new Dice.Color[]{
                Dice.Color.RED, Dice.Color.WHITE, Dice.Color.WHITE, Dice.Color.BLUE, Dice.Color.RED
        });
        possibleTiles = game.getPossibleTiles();
        assertTrue(possibleTiles.contains("R3"));  // Two red + two white = R3 possible
        assertTrue(possibleTiles.contains("B2"));  // One blue + two white = B2 possible

        // Example 4: Insufficient dice for larger tiles without a bonus
        // Use AbilityTrack or appropriate system to simulate bonus if needed
        // Assuming game or player can manage bonuses through the existing API.
        // No setBonus(), so we skip that logic.

        dice.setResults(new Dice.Color[]{
                Dice.Color.RED, Dice.Color.RED, Dice.Color.WHITE, Dice.Color.GREEN, Dice.Color.BLUE
        });
        possibleTiles = game.getPossibleTiles();
        assertFalse(possibleTiles.contains("R4"));  // Requires 4, not achievable
        assertTrue(possibleTiles.contains("R3"));   // R3 achievable with two red + white
    }

    /**
     * test for FacadeTile.getRotatedShape()
     * input parameter: orientation - int, between 0 and 3 otherwise throw error
     * output: 2d char array represent the shape being rotated given the required orientation
     * <p>
     * test cases:
     * all the shapes on the facade sheet
     * <p>
     * expected results:
     * 1. output of the shape being rotated matches actual results hard-coded.
     * 2. getRotatedShape() don't accept any values larger than 3 or less than 0.
     * @author Xinrui Tan
     */
    @Test
    public void testRotateShape() {
        // tiles to test:
        // "R2", "R3", "R4", "R5", "B4L", "B4R", "B5", "P3", "P4", "P5", "G4L", "G4R", "G5", "Y4L", "Y4R", "Y5"

        // test size 2 shape I
        FacadeTile r2 = new FacadeTile.InfinityTile("R2");
        Assertions.assertArrayEquals(r2.getShape(), r2.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW}, {FacadeTile.WINDOW}}, r2.getRotatedShape(1));
        Assertions.assertArrayEquals(r2.getShape(), r2.getRotatedShape(2));
        Assertions.assertArrayEquals(r2.getRotatedShape(1), r2.getRotatedShape(3));

        // test size 3 shape L
        FacadeTile r3 = new FacadeTile.InfinityTile("R3");
        Assertions.assertArrayEquals(r3.getShape(), r3.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, FacadeTile.WINDOW}, {FacadeTile.WINDOW, 0}}, r3.getRotatedShape(1));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, FacadeTile.WINDOW}, {0, FacadeTile.WINDOW}}, r3.getRotatedShape(2));
        Assertions.assertArrayEquals(new char[][]{{0, FacadeTile.WINDOW}, {FacadeTile.WINDOW, FacadeTile.WINDOW}}, r3.getRotatedShape(3));

        // test size 3 shape I
        FacadeTile p3 = new FacadeTile.InfinityTile("P3");
        Assertions.assertArrayEquals(p3.getShape(), p3.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW}, {FacadeTile.WINDOW}, {FacadeTile.WINDOW}}, p3.getRotatedShape(1));
        Assertions.assertArrayEquals(p3.getShape(), p3.getRotatedShape(2));
        Assertions.assertArrayEquals(p3.getRotatedShape(1), p3.getRotatedShape(3));

        // test red size 4
        FacadeTile r4 = new FacadeTile.XTile("R4");
        Assertions.assertArrayEquals(r4.getShape(), r4.getRotatedShape(0));
        Assertions.assertArrayEquals(r4.getShape(), r4.getRotatedShape(1));
        Assertions.assertArrayEquals(r4.getShape(), r4.getRotatedShape(2));
        Assertions.assertArrayEquals(r4.getShape(), r4.getRotatedShape(3));

        // test red size 5
        FacadeTile r5 = new FacadeTile.XTile("R5");
        Assertions.assertArrayEquals(r5.getShape(), r5.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW}, {FacadeTile.WINDOW, FacadeTile.WINDOW, 0}}, r5.getRotatedShape(1));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, FacadeTile.WINDOW}, {FacadeTile.WINDOW, FacadeTile.WINDOW}, {0, FacadeTile.WINDOW}}, r5.getRotatedShape(2));
        Assertions.assertArrayEquals(new char[][]{{0, FacadeTile.WINDOW, FacadeTile.WINDOW}, {FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW}}, r5.getRotatedShape(3));

        // test blue size 4
        FacadeTile b4l = new FacadeTile.XTile("B4L");
        Assertions.assertArrayEquals(b4l.getShape(), b4l.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, FacadeTile.WINDOW}, {FacadeTile.WINDOW, 0}, {FacadeTile.WINDOW, 0}}, b4l.getRotatedShape(1));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW}, {0, 0, FacadeTile.WINDOW}}, b4l.getRotatedShape(2));
        Assertions.assertArrayEquals(new char[][]{{0, FacadeTile.WINDOW}, {0, FacadeTile.WINDOW}, {FacadeTile.WINDOW, FacadeTile.WINDOW}}, b4l.getRotatedShape(3));

        FacadeTile b4r = new FacadeTile.XTile("B4R");
        Assertions.assertArrayEquals(b4r.getShape(), b4r.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, FacadeTile.WINDOW}, {0, FacadeTile.WINDOW}, {0, FacadeTile.WINDOW}}, b4r.getRotatedShape(1));
        Assertions.assertArrayEquals(new char[][]{{0, 0, FacadeTile.WINDOW}, {FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW}}, b4r.getRotatedShape(2));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, 0}, {FacadeTile.WINDOW, 0}, {FacadeTile.WINDOW, FacadeTile.WINDOW}}, b4r.getRotatedShape(3));

        // test blue size 5
        FacadeTile b5 = new FacadeTile.XTile("B5");
        Assertions.assertArrayEquals(b5.getShape(), b5.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW}, {0, FacadeTile.WINDOW, 0}, {0, FacadeTile.WINDOW, 0}}, b5.getRotatedShape(1));
        Assertions.assertArrayEquals(new char[][]{{0, 0, FacadeTile.WINDOW}, {FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW}, {0, 0, FacadeTile.WINDOW}}, b5.getRotatedShape(2));
        Assertions.assertArrayEquals(new char[][]{{0, FacadeTile.WINDOW, 0}, {0, FacadeTile.WINDOW, 0}, {FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW}}, b5.getRotatedShape(3));

        // test purple size 4
        FacadeTile p4 = new FacadeTile.XTile("P4");
        Assertions.assertArrayEquals(p4.getShape(), p4.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW}, {FacadeTile.WINDOW}, {FacadeTile.WINDOW}, {FacadeTile.WINDOW}}, p4.getRotatedShape(1));
        Assertions.assertArrayEquals(p4.getShape(), p4.getRotatedShape(2));
        Assertions.assertArrayEquals(p4.getRotatedShape(1), p4.getRotatedShape(3));

        // test purple size 5
        FacadeTile p5 = new FacadeTile.XTile("P5");
        Assertions.assertArrayEquals(p5.getShape(), p5.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW}}, p5.getRotatedShape(1));
        Assertions.assertArrayEquals(p5.getShape(), p5.getRotatedShape(2));
        Assertions.assertArrayEquals(p5.getRotatedShape(1), p5.getRotatedShape(3));

        // test green size 4
        FacadeTile g4l = new FacadeTile.XTile("G4L");
        Assertions.assertArrayEquals(g4l.getShape(), g4l.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, 0}, {FacadeTile.WINDOW, FacadeTile.WINDOW}, {FacadeTile.WINDOW, 0}}, g4l.getRotatedShape(1));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW}, {0, FacadeTile.WINDOW, 0}}, g4l.getRotatedShape(2));
        Assertions.assertArrayEquals(new char[][]{{0, FacadeTile.WINDOW}, {FacadeTile.WINDOW, FacadeTile.WINDOW}, {0, FacadeTile.WINDOW}}, g4l.getRotatedShape(3));

        FacadeTile g4r = new FacadeTile.XTile("G4R");
        Assertions.assertArrayEquals(g4r.getShape(), g4r.getRotatedShape(0));
        Assertions.assertArrayEquals(g4l.getRotatedShape(3), g4r.getRotatedShape(1));
        Assertions.assertArrayEquals(g4l.getShape(), g4r.getRotatedShape(2));
        Assertions.assertArrayEquals(g4l.getRotatedShape(1), g4r.getRotatedShape(3));

        // test green size 5
        FacadeTile g5 = new FacadeTile.XTile("G5");
        Assertions.assertArrayEquals(g5.getShape(), g5.getRotatedShape(0));
        Assertions.assertArrayEquals(g5.getShape(), g5.getRotatedShape(1));
        Assertions.assertArrayEquals(g5.getShape(), g5.getRotatedShape(2));
        Assertions.assertArrayEquals(g5.getShape(), g5.getRotatedShape(3));

        // test yellow size 4
        FacadeTile y4l = new FacadeTile.XTile("Y4L");
        Assertions.assertArrayEquals(y4l.getShape(), y4l.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{0, FacadeTile.WINDOW}, {FacadeTile.WINDOW, FacadeTile.WINDOW}, {FacadeTile.WINDOW, 0}}, y4l.getRotatedShape(1));
        Assertions.assertArrayEquals(y4l.getShape(), y4l.getRotatedShape(2));
        Assertions.assertArrayEquals(y4l.getRotatedShape(1), y4l.getRotatedShape(3));

        FacadeTile y4r = new FacadeTile.XTile("Y4R");
        Assertions.assertArrayEquals(y4r.getShape(), y4r.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{FacadeTile.WINDOW, 0}, {FacadeTile.WINDOW, FacadeTile.WINDOW}, {0, FacadeTile.WINDOW}}, y4r.getRotatedShape(1));
        Assertions.assertArrayEquals(y4r.getShape(), y4r.getRotatedShape(2));
        Assertions.assertArrayEquals(y4r.getRotatedShape(1), y4r.getRotatedShape(3));

        // test yellow size 5
        FacadeTile y5 = new FacadeTile.XTile("Y5");
        Assertions.assertArrayEquals(y5.getShape(), y5.getRotatedShape(0));
        Assertions.assertArrayEquals(new char[][]{{0, 0, FacadeTile.WINDOW}, {FacadeTile.WINDOW, FacadeTile.WINDOW, FacadeTile.WINDOW}, {FacadeTile.WINDOW, 0, 0}}, y5.getRotatedShape(1));
        Assertions.assertArrayEquals(y5.getShape(), y5.getRotatedShape(2));
        Assertions.assertArrayEquals(y5.getRotatedShape(1), y5.getRotatedShape(3));

        char[][] shape = null;
        // test getRotatedShape() don't accept orientation larger than 3
        try {
            shape = y5.getRotatedShape(4);
        } catch (Exception e) {
            System.out.println(e);
            assertTrue(e instanceof IllegalArgumentException);
        } finally {
            Assertions.assertNull(shape);
        }

        // test getRotatedShape() don't accept orientation less than 0
        try {
            shape = y5.getRotatedShape(-1);
        } catch (Exception e) {
            System.out.println(e);
            assertTrue(e instanceof IllegalArgumentException);
        } finally {
            Assertions.assertNull(shape);
        }
    }

    /**
     * Test for Grid.getGridScore()
     * <p>
     * examples used: empty, full window row, full window column, full row with brick, full column with brick
     * @author Haochen Liu
     */
    @Test
    public void testGetGridScore() {
        // Scenario 1: Empty grid (should return 0 points)
        Grid grid = new Grid();
        int scoreEmptyGrid = grid.getGridScore();
        assertEquals(0, scoreEmptyGrid);

        // Scenario 2: Full row with all windows (should give 2 points)
        for (int col = 0; col < Grid.NUM_COLS; col++) {
            grid.getGrid()[col][0] = FacadeTile.WINDOW;  // Set the first row to all windows
        }
        grid.checkCompletion();  // Ensure the completion status is updated
        int scoreAllWindowsRow = grid.getGridScore();
        assertEquals(2, scoreAllWindowsRow);

        // Scenario 3: Full column with all windows (should give 4 points)
        grid = new Grid();  // Reset the grid
        for (int row = 0; row < Grid.NUM_ROWS; row++) {
            grid.getGrid()[0][row] = FacadeTile.WINDOW;  // Set the first column to all windows
        }
        grid.checkCompletion();  // Ensure the completion status is updated
        int scoreAllWindowsCol = grid.getGridScore();
        assertEquals(4, scoreAllWindowsCol);

        // Scenario 4: Full row with one brick (should give 1 point)
        grid = new Grid();  // Reset the grid
        for (int col = 0; col < Grid.NUM_COLS; col++) {
            grid.getGrid()[col][1] = FacadeTile.WINDOW;
        }
        grid.getGrid()[0][1] = FacadeTile.BRICK;  // Add one brick to the row
        grid.checkCompletion();  // Ensure the completion status is updated
        int scoreOneBrickRow = grid.getGridScore();
        assertEquals(1, scoreOneBrickRow);

        // Scenario 5: Full column with one brick (should give 2 points)
        grid = new Grid();  // Reset the grid
        for (int row = 0; row < Grid.NUM_ROWS; row++) {
            grid.getGrid()[1][row] = FacadeTile.WINDOW;
        }
        grid.getGrid()[1][0] = FacadeTile.BRICK;  // Add one brick to the column
        grid.checkCompletion();  // Ensure the completion status is updated
        int scoreOneBrickCol = grid.getGridScore();
        assertEquals(2, scoreOneBrickCol);

        // Scenario 6: Mix of complete and incomplete rows and columns
        grid = new Grid();  // Reset the grid
        for (int col = 0; col < Grid.NUM_COLS; col++) {
            grid.getGrid()[col][2] = FacadeTile.WINDOW;  // All windows in the third row
            grid.getGrid()[col][3] = (col == 0) ? FacadeTile.BRICK : FacadeTile.WINDOW;  // One brick in the fourth row
        }
        grid.checkCompletion();  // Ensure the completion status is updated
        int scoreMixed = grid.getGridScore();
        assertEquals(3, scoreMixed);
    }


    /**
     * Test for Player.placeTile()
     *
     * @author Haochen Liu
     */
    @Test
    public void testPlaceTile() {
        // Initialize facadeSheet to prevent test failure
        Map<String, FacadeTile> facadeSheet = new HashMap<>();

        // Create a mock Dice object and manually set the results to avoid NullPointerException
        Dice mockDice = new Dice();
        mockDice.setResults(new Dice.Color[]{
                Dice.Color.RED, Dice.Color.RED, Dice.Color.BLUE, Dice.Color.WHITE, Dice.Color.RED
        });

        // Use reflection to manually set the dice field to ensure that the dice in the Player class is not null
        try {
            Player player = new Player(0, facadeSheet);

            // Use reflection to set the dice field to mockDice
            java.lang.reflect.Field diceField = Player.class.getDeclaredField("dice");
            diceField.setAccessible(true);
            diceField.set(player, mockDice);  // Assign mockDice to the dice field inside Player

            // Prepare a Placement object for testing
            Placement placement = new Placement("R3", 4, 0, 0, 0);

            // Test the placeTile method
            FacadeTile placedTile = player.placeTile(placement);

            // Verify the result
            Assertions.assertNotNull(placedTile, "The tile should be placed.");
            Assertions.assertEquals("R3", placedTile.getName(),
                    "The placed tile should have the correct name.");

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            Assertions.fail("Failed to set dice field in Player.");
        }
    }

    /**
     * Test case when the tile is not an XTile.
     * The method should return true.
     * @author Haochen Liu
     */
    @Test
    public void testCanUseTile_NonXTile() {
        // Setup
        Map<String, FacadeTile> facadeSheet = new HashMap<>();
        FacadeTile tile = new FacadeTile.InfinityTile("R2");
        facadeSheet.put("R2", tile);
        Player player = new Player(1, facadeSheet);

        // Test
        boolean canUse = player.canUseTile("R2");

        // Assert
        assertTrue(canUse, "Non-XTile should be usable.");
    }

    /**
     * Test case when the tile is an XTile that is not crossed out.
     * The method should return true.
     * @author Haochen Liu
     */
    @Test
    public void testCanUseTile_XTile_NotCrossedOut() {
        // Setup
        Map<String, FacadeTile> facadeSheet = new HashMap<>();
        List<String> xtTiles = List.of("R4", "R5", "B4L", "B4R", "B5", "P4", "P5", "G4L", "G4R", "G5", "Y4L", "Y4R", "Y5");
        String xtile = xtTiles.get(new Random().nextInt(xtTiles.size()));
        FacadeTile.XTile xTile = new FacadeTile.XTile(xtile);
        facadeSheet.put(xtile, xTile);
        Player player = new Player(1, facadeSheet);

        // Test
        boolean canUse = player.canUseTile(xtile);

        // Assert
        assertTrue(canUse, "XTile that is not crossed out should be usable.");
    }

    /**
     * Test case when the tile is an XTile that is crossed out and the player does not have USE_AGAIN ability.
     * The method should return false.
     * @author Haochen Liu
     */
    @Test
    public void testCanUseTile_XTile_CrossedOut_NoAbility() {
        // Setup
        Map<String, FacadeTile> facadeSheet = new HashMap<>();
        List<String> xtTiles = List.of("R4", "R5", "B4L", "B4R", "B5", "P4", "P5", "G4L", "G4R", "G5", "Y4L", "Y4R", "Y5");
        String xtile = xtTiles.get(new Random().nextInt(xtTiles.size()));
        FacadeTile.XTile xTile = new FacadeTile.XTile(xtile);
        xTile.crossOut(); // Cross out the tile
        facadeSheet.put(xtile, xTile);
        Player player = new Player(1, facadeSheet);

        // Use up any existing USE_AGAIN abilities
        int abilityAvailable = player.getAbilityAvailable(Ability.USE_AGAIN);
        for (int i = 0; i < abilityAvailable; i++) {
            player.useAbility(Ability.USE_AGAIN);
        }
        assertEquals(0, player.getAbilityAvailable(Ability.USE_AGAIN));

        // Test
        boolean canUse = player.canUseTile(xtile);

        // Assert
        assertFalse(canUse, "XTile that is crossed out without USE_AGAIN ability should not be usable.");
    }

    /**
     * Test case when the tile is an XTile that is crossed out and the player have available USE_AGAIN ability.
     * The method should return true.
     * @author Haochen Liu
     */
    @Test
    public void testCanUseTile_XTile_CrossedOut_WithAbility() {
        // Setup
        Map<String, FacadeTile> facadeSheet = new HashMap<>();
        List<String> xtTiles = List.of("R4", "R5", "B4L", "B4R", "B5", "P4", "P5", "G4L", "G4R", "G5", "Y4L", "Y4R", "Y5");
        String xtile = xtTiles.get(new Random().nextInt(xtTiles.size()));
        FacadeTile.XTile xTile = new FacadeTile.XTile(xtile);
        xTile.crossOut(); // Cross out the tile
        facadeSheet.put(xtile, xTile);
        Player player = new Player(1, facadeSheet);

        // advance yellow track until player get a Use Again ability
        while (player.getAbilityAvailable(Ability.USE_AGAIN) == 0)
            player.advanceTrack(Dice.Color.YELLOW.ordinal(), 1);
        assertTrue(player.getAbilityAvailable(Ability.USE_AGAIN) > 0);

        // Test
        boolean canUse = player.canUseTile(xtile);

        // Assert
        assertTrue(canUse, "XTile that is crossed out with USE_AGAIN ability should be usable.");
    }

    /**
     * Test case where the tile can be placed at the bottom of the grid.
     * @author Haochen Liu
     */
    @Test
    public void testCanPlaceTile_ValidPlacementAtBottom() {
        // Setup
        Map<String, FacadeTile> facadeSheet = new HashMap<>();
        FacadeTile tile = new FacadeTile.InfinityTile("R2"); // A tile of size 2
        facadeSheet.put("R2", tile);
        Player player = new Player(1, facadeSheet);

        int size = tile.getSize();
        Placement placement = new Placement("R2", size, 2, 0, 0); // Include size

        // Test
        boolean canPlace = player.canPlaceTile(placement);

        // Assert
        assertTrue(canPlace);
    }

    /**
     * Test case where the tile cannot be placed because it's not adjacent to any tiles and not on the bottom row.
     * @author Haochen Liu
     */
    @Test
    public void testCanPlaceTile_NotAdjacentOrBottom() {
        // Setup
        Map<String, FacadeTile> facadeSheet = new HashMap<>();
        FacadeTile tile = new FacadeTile.InfinityTile("P3");
        facadeSheet.put("P3", tile);
        Player player = new Player(1, facadeSheet);

        int size = tile.getSize();
        Placement placement = new Placement("P3", size, 2, 5, 0); // Position not at bottom and not adjacent

        // Test
        boolean canPlace = player.canPlaceTile(placement);

        // Assert
        assertFalse(canPlace);
    }


    /**
     * Test case where the tile cannot be placed because it exceeds grid dimensions after rotation.
     * @author Haochen Liu
     */
    @Test
    public void testCanPlaceTile_ExceedsGridAfterRotation() {
        // Setup
        Map<String, FacadeTile> facadeSheet = new HashMap<>();
        FacadeTile tile = new FacadeTile.InfinityTile("B3"); // Original shape is L-shaped
        facadeSheet.put("B3", tile);
        Player player = new Player(1, facadeSheet);

        int size = tile.getSize();
        Placement placement = new Placement("B3", size, 4, 0, 1); // Rotate 90 degrees at position that causes out of bounds

        // Test
        boolean canPlace = player.canPlaceTile(placement);

        // Assert
        assertFalse(canPlace);
    }
}