package comp1110.ass2;

import java.util.*;

import comp1110.ass2.gui.GameGUI;
import comp1110.ass2.gui.Placement;

/**
 * The Player class represents a player in the game.
 * It handles the player's score, grid, dice, ability tracks, and game actions like placing tiles.
 */
public class Player {
    private final int id;
    private int score;
    private Grid grid; // The player's grid for placing facade tiles
    private List<AbilityTrack> tracks; // List of ability tracks for the player
    private Dice dice;
    private boolean noX;
    private int coaBonuses;
    final Map<String, FacadeTile> facadeSheet;
    public static int maxDicesToChooseNonactive = 1;

    /**
     * Constructor to initialize a player with a given ID and facade sheet.
     * @param id The player's unique ID.
     * @param facadeSheet The map of facade tiles available to the player.
     */
    public Player(int id, Map<String, FacadeTile> facadeSheet) {
        this.id = id;
        this.score = 0;
        this.grid = new Grid();
        this.tracks = new ArrayList<>();
        this.tracks.add(new AbilityTrack(
                Dice.Color.RED,
                new AbilityTrack.Power[]{
                        AbilityTrack.Power.BONUS,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.BONUS,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.NONE
                }
        ));
        this.tracks.add(new AbilityTrack(
                Dice.Color.BLUE,
                new AbilityTrack.Power[]{
                        AbilityTrack.Power.BONUS,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.BONUS,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.NONE
                }
        ));
        this.tracks.add(new AbilityTrack(
                Dice.Color.PURPLE,
                new AbilityTrack.Power[]{
                        AbilityTrack.Power.BONUS,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.BONUS,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.NONE
                }
        ));
        this.tracks.add(new AbilityTrack(
                Dice.Color.GREEN,
                new AbilityTrack.Power[]{
                        AbilityTrack.Power.BONUS,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.BONUS,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.NONE
                }
        ));
        this.tracks.add(new AbilityTrack(
                Dice.Color.YELLOW,
                new AbilityTrack.Power[]{
                        AbilityTrack.Power.BONUS,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.BONUS,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.ABILITY,
                        AbilityTrack.Power.NONE,
                        AbilityTrack.Power.NONE
                }
        ));
        this.dice = null;
        this.facadeSheet = facadeSheet;
    }

    public void setDice(Dice dice) {
        this.dice = dice;
    }

    /**
     * Resets the player's state at the end of their turn.
     * Clears dice and coat-of-arms bonuses, and updates the player's score.
     */
    public void pass() {
        this.dice = null;
        this.coaBonuses = 0;
        grid.resetActiveCOAs();
        updateScore();
    }

    /**
     * Rolls the player's dice. Throws an exception if the dice are not available.
     */
    public void rollDice() {
        if (dice != null)
            dice.roll();
        else
            throw new UnsupportedOperationException("inactive player cannot use dices");
    }

    /**
     * Rerolls the player's dice. Throws an exception if the dice are not available.
     */
    public void rerollDice() {
        if (dice != null)
            dice.reroll();
        else
            throw new UnsupportedOperationException("inactive player cannot use dices");
    }

    /**
     * Changes the color of the player's dice. Throws an exception if the dice are not available.
     * @param color The new color to change the dice to.
     */
    public void changeDiceColor(Dice.Color color) {
        if (dice != null)
            dice.setChangeColor(color);
        else
            throw new UnsupportedOperationException("inactive player cannot use dices");
    }

    /**
     * Sets the noX flag to true, enabling the "no X" ability for the player.
     */
    public void setNoX() {
        this.noX = true;
    }

    public boolean isNoX() {
        return noX;
    }

    /**
     * Advances the player's ability track for the given colors when they are not active.
     * @param colors The list of colors to advance.
     * @param maxAdvance the steps to advance per track
     * @author Xinrui Tan
     */
    public void advanceTrack(List<Integer> colors, int maxAdvance) {
        assert colors.size() * maxAdvance <= maxDicesToChooseNonactive;
        for (int color : colors)
            advanceTrack(color, maxAdvance);
    }

    /**
     * Advances the player's ability track for a specific color, up to a maximum value.
     * @param color The color track to advance.
     * @param maxAdvance The maximum steps to advance.
     */
    public void advanceTrack(int color, int maxAdvance) {
        tracks.get(color).advance(Math.min(maxAdvance, maxDicesToChooseNonactive));
        updateScore();
    }

    /**
     * Advances the player's coat-of-arms bonus track by 2 steps.
     * @param color The color track to advance.
     */
    public void coaAdvanceTrack(int color) {
        coaBonuses--;
        grid.useCOA();
        tracks.get(color).advance(2);
        updateScore();
    }

    public boolean completeTracks() {
        for (AbilityTrack track : tracks) {
            if (!track.isTrackComplete())
                return false;
        }
        return true;
    }

    /**
     * Uses the specified ability for the player.
     * @param ability The ability to use.
     */
    public void useAbility(Ability ability) {
        getTrack(ability).useAbility();
    }

    /**
     * Checks whether the player can use a specific tile.
     * @param name The name of the tile.
     * @return true if the player can use the tile, false otherwise.
     */
    public boolean canUseTile(String name) {
        FacadeTile tile = facadeSheet.get(name);
        if (tile instanceof FacadeTile.XTile xTile)
            return !xTile.isCrossedOut() || (getAbilityAvailable(Ability.USE_AGAIN) > 0);
        return true;
    }

    /**
     * Checks whether the player can place a tile at a given placement on the grid.
     * @param placement The placement details of the tile.
     * @return true if the tile can be placed, false otherwise.
     */
    public boolean canPlaceTile(Placement placement) {
        return grid.canBePlaced(facadeSheet.getOrDefault(placement.getTileName(), new FacadeTile.InfinityTile(placement.getTileName())),
                new Pose(placement.getX(), placement.getY(), placement.getRotation()));
    }

    /**
     * Places a tile on the grid based on the player's placement and abilities.
     * @param placement The placement details of the tile.
     * @return the tile being placed
     */
    public FacadeTile placeTile(Placement placement) {
        FacadeTile tile = facadeSheet.getOrDefault(placement.getTileName(), new FacadeTile.InfinityTile(placement.getTileName()));
        if (tile.getSize() > 1) {
            Map<Dice.Color, Integer> counter = dice.countResult();
            if (tile.getSize() > counter.getOrDefault(tile.getColor(), 0) + counter.getOrDefault(Dice.Color.WHITE, 0))
                tracks.get(tile.getColor().ordinal()).useBonus();
            if (tile instanceof FacadeTile.XTile xTile) {
                if (xTile.isCrossedOut())
                    getTrack(Ability.USE_AGAIN).useAbility();
                xTile.crossOut();
            }
            if (noX) {
                noX = false;
                getTrack(Ability.NO_X).useAbility();
            } else {
                tile.setBrick(placement::getWindow);
            }
        } else if (placement.getTileName().equals("S1X")) {
            getTrack(Ability.ONE_X).useAbility();
        } else if (placement.getTileName().equals("S1O")) {
            coaBonuses--;
            grid.useCOA();
        }
        grid.updateGrid(tile, new Pose(placement.getX(), placement.getY(), placement.getRotation()));
        coaBonuses = grid.getActiveCOAs().size();
        updateScore();
        return tile;
    }

    /**
     * wrapper for placeTile(), can update the GUI accordingly.
     * @param placement The placement details of the tile.
     * @param gui The GameGUI object to update.
     * @return The tile that was placed.
     */
    public FacadeTile placeTile(Placement placement, GameGUI gui) {
        int x = placement.getX();
        int y = placement.getY();
        int r = placement.getRotation();
        FacadeTile tile = placeTile(placement);
        char[][] shape = tile.getRotatedShape(r);

        // Update the GUI with the new tile placement
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int newX = x + i;
                    int newY = y + j;
                    String color = (tile.getColor() == null) ? "Gray" : tile.getColor().toString();
                    gui.setFacadeSquare(id, newX, newY, color, shape[i][j] == FacadeTile.WINDOW);
                }
            }
        }

        // Update COA bonuses in the GUI if applicable
        if (coaBonuses > 0) {
            for (String coa : grid.getActiveCOAs()) {
                if (coa.startsWith("row")) {
                    gui.setRowCoA(id, Integer.parseInt(coa.substring(4)), true);
                } else if (coa.startsWith("col")) {
                    gui.setColumnCoA(id, Integer.parseInt(coa.substring(4)), true);
                }
            }
        }

        if (placement.getTileName().startsWith("S1")) {
            gui.clearTileSelection();
            gui.setAvailableTiles(new ArrayList<>());
        }

        return tile;
    }

    public int getBonusAvailable(Dice.Color color) {
        return getTrack(color.ordinal()).getBonusAvailable();
    }

    public int getAbilityAvailable(Ability ability) {
        return getTrack(ability).getAbilityAvailable();
    }

    public int getCoaBonuses() {
        return coaBonuses;
    }

    // Method to update the player's score based on completed rows/columns in the grid
    private void updateScore() {
        this.score = grid.getGridScore();
        for (AbilityTrack track : tracks) {
            if (track.isTrackComplete())
                score += 2;
        }
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    AbilityTrack getTrack(int color) {
        return tracks.get(color);
    }

    AbilityTrack getTrack(Ability ability) {
        return tracks.get(ability.getColor().ordinal());
    }

    public void setTrackInfo(GameGUI gui) {
        for (AbilityTrack track : tracks) {
            gui.setTrackInfo(
                    id,
                    track.getColor().toString(),
                    track.getPos(),
                    track.getBonusAvailable(),
                    track.getAbilityAvailable(),
                    track.getNextBonus(),
                    track.getNextAbility()
            );
        }
        gui.setScore(id, score);
    }

    /**
     * The AIPlayer class represents an AI-controlled player in the game.
     * It extends the Player class and provides AI-specific logic.
     */
    public static class AIPlayer extends Player {

        /**
         * Constructor to initialize an AI player with a given ID and facade sheet.
         * @param id The player's unique ID.
         * @param facadeSheet The map of facade tiles available to the AI player.
         */
        public AIPlayer(int id, Map<String, FacadeTile> facadeSheet) {
            super(id, facadeSheet);
        }

        /**
         * Method to handle the AI player's actions. This method will be implemented in the future.
         */
        public void play() {
            // TODO
        }
    }
}
