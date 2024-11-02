package comp1110.ass2;

import comp1110.ass2.Dice;
import comp1110.ass2.Game;
import comp1110.ass2.Player;
import comp1110.ass2.gui.Placement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

public class D2CTest {

    /**
     * Test case to verify the correct identification of available tiles
     * based on dice rolls. In this test, the dice are set to produce three
     * red results, one blue, and one wildcard (white). The test checks if
     * the game correctly identifies the possible tiles that the player
     * can select based on these dice.
     *
     * Methods tested:
     * - Game.getPossibleTiles(): Retrieves a list of tiles that can be selected
     *   based on the current dice roll.
     *
     * Expected Results:
     * - The method should return a list containing the "R3" and "R4" tiles,
     *   as these tiles can be selected based on the three red dice.
     * - The method should not include "B3", as there are not enough blue dice.
     */
    @Test
    public void diceTest() {
        Game game = new Game();
        game.registerPlayer(2, new boolean[]{false, false});
        Dice dice = game.getDice();
        dice.setResults(new Dice.Color[]{Dice.Color.RED, Dice.Color.RED, Dice.Color.RED, Dice.Color.BLUE, Dice.Color.WHITE});
        List<String> possibleTiles = game.getPossibleTiles();
        System.out.println(possibleTiles);
        Assertions.assertTrue(possibleTiles.contains("R3"));
        Assertions.assertTrue(possibleTiles.contains("R4"));
        Assertions.assertFalse(possibleTiles.contains("B3"));
    }

    /**
     * Test case to verify tile placement functionality on the player's
     * building grid. The test sets up a game with two players and checks
     * the validity of several tile placements based on their position and
     * orientation.
     *
     * Methods tested:
     * - Player.canPlaceTile(): Verifies if a given tile can be legally placed
     *   on the player's grid.
     * - Player.placeTile(): Places a tile on the player's grid, if valid.
     *
     * Expected Results:
     * - The "Y3" tile should be rejected when placed at (1,0) or (1,1) due to
     *   invalid conditions, but should be accepted at (1,0) with rotation 3
     *   and at (2,0) with rotation 1.
     */
    @Test
    public void placementTest() {
        Game game = new Game();
        game.registerPlayer(2, new boolean[]{false, false});
        Player current = game.getActivePlayer();
        game.getDice().setResults(new Dice.Color[]{Dice.Color.WHITE, Dice.Color.WHITE, Dice.Color.WHITE, Dice.Color.WHITE, Dice.Color.WHITE});
        Placement tile1 = new Placement("B3", 3, 0, 0, 1);
        tile1.setBrick(1);
        Placement tile2 = new Placement("G4L", 4, 3, 0, 0);
        tile2.setBrick(1);
        current.placeTile(tile1);
        current.placeTile(tile2);

        Assertions.assertFalse(current.canPlaceTile(new Placement("Y3", 3, 1, 0, 0)));
        Assertions.assertTrue(current.canPlaceTile(new Placement("Y3", 3, 1, 0, 3)));
        Assertions.assertFalse(current.canPlaceTile(new Placement("Y3", 3, 2, 0, 3)));
        Assertions.assertTrue(current.canPlaceTile(new Placement("Y3", 3, 2, 0, 1)));
        Assertions.assertFalse(current.canPlaceTile(new Placement("Y3", 3, 1, 1, 3)));
    }
}
