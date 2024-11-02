package comp1110.ass2;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Class to describe the 5 color dice used in the game.
 * This class manages the dice roll, color results, and related actions like rerolling and color change.
 */
public class Dice {

    /**
     * Enum to represent the six different colors of the dice.
     * It includes a method to randomly generate a color.
     */
    public enum Color {
        RED("Red"),
        BLUE("Blue"),
        PURPLE("Purple"),
        GREEN("Green"),
        YELLOW("Yellow"),
        WHITE("White");

        private final String name;
        private static final Random ColorRNG = new Random(); // random generator

        Color(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        /**
         * Randomly generates and returns a color.
         * @return A randomly selected color.
         */
        public static Color randomColor() {
            Color[] colors = values();
            return colors[ColorRNG.nextInt(colors.length)];
        }

        /**
         * Get the color based on its index in the enum.
         * @param i The index of the color.
         * @return The corresponding color.
         */
        public static Color getColor(int i) {
            return values()[i];
        }

        /**
         * Get the color based on its string name.
         * @param str The string representing the color name.
         * @return The corresponding Color or null if not found.
         */
        public static Color getColor(String str) {
            List<String> words = List.of(str.split(" "));
            for (Color color : values())
                if (words.contains(color.name))
                    return color;
            return null;
        }
    }

    private Color[] results;
    private List<Integer> selected;
    public static final int NUM_DICE = 5;

    /**
     * Constructor to initialize the dice results.
     */
    public Dice() {
        this.results = new Color[NUM_DICE];
    }

    /**
     * Roll the dice and assign random colors to each dice.
     */
    public void roll() {
        for (int i = 0; i < NUM_DICE; i++) {
            results[i] = Color.randomColor();
        }
    }

    /**
     * Get the current results of the dice.
     * @return Array of dice results.
     */
    public Color[] getResults() {
        return results;
    }

    /**
     * Set the dice results (for testing purposes).
     * @param results The new dice results.
     */
    public void setResults(Color[] results) {
        // for test only
        this.results = results;
    }

    /**
     * Set the selected dice for future actions (like reroll or color change).
     * @param selected List of selected dice indices.
     */
    public void setSelected(List<Integer> selected) {
        this.selected = selected;
    }

    /**
     * Validate if the track can advance for a non-active player based on their tile and selected tracks.
     * @param tile The tile that the active player placed.
     * @param tracks The list of track indices.
     * @return The valid number of steps to advance per track.
     */
    public int validTrackToAdvanceForNonactivePlayer(FacadeTile tile, List<Integer> tracks) {
        int size = tile.getSize();
        Color color = tile.getColor();
        Map<Color, Integer> count = countResult();
        // get dice color count for the color of the placed tile
        int color_count = count.getOrDefault(color, 0);
        // remove dices used by active player
        if (size < color_count)
            count.replace(color, color_count - size);
        else {
            count.remove(color);
            size -= color_count;
        }
        int whites = count.getOrDefault(Color.WHITE, 0) - size;

        if (tracks.size() == 1) {
            // most cases should go here
            // in most cases should return 1
            // only when 1) there are only 2 players
            //           2) the non-active player chose only one track
            //           3) there are enough dice of this color or white dice
            // this can return 2
            return min(count.getOrDefault(Color.getColor(tracks.get(0)), 0) + max(whites, 0),
                    Player.maxDicesToChooseNonactive);
        }

        if (tracks.size() <= Player.maxDicesToChooseNonactive) {
            int valid = tracks.stream().mapToInt(
                    (c -> (count.getOrDefault(Color.getColor(c), 0) > 0) ? 1 : 0)
            ).reduce(0, Integer::sum);
            if ((valid == tracks.size()) || (whites > 0 && whites >= tracks.size() - valid))
                return 1;
        }
        return 0;
    }

    /**
     * Check if no more tracks are available to advance for a tile.
     * @param tile The tile being placed.
     * @return True if no tracks are advanceable, false otherwise.
     */
    public boolean canPlayerAdvance(FacadeTile tile, Player player) {
        Map<Color, Integer> countMap = countResult();
        if (tile.getSize() == 5 &&
                countMap.getOrDefault(tile.getColor(), 0)
                        + countMap.getOrDefault(Color.WHITE, 0) == 5)
            return false;
        if (player.completeTracks())
            return false;
        int size = tile.getSize();
        Color color = tile.getColor();
        // get dice color count for the color of the placed tile
        int color_count = countMap.getOrDefault(color, 0);
        // remove dices used by active player
        if (size < color_count)
            countMap.replace(color, color_count - size);
        else {
            countMap.remove(color);
            size -= color_count;
        }
        int whites = countMap.getOrDefault(Color.WHITE, 0) - size;
        countMap.remove(Color.WHITE);
        if (whites > 0)
            return true;
        for (Color color1 : countMap.keySet())
            if (!player.getTrack(color1.ordinal()).isTrackComplete())
                return true;
        return false;
    }

    /**
     * Check if the selected dice are of the same color.
     * @return True if all selected dice are the same color, false otherwise.
     */
    public boolean isSelectedSameColor() {
        if (selected != null && !selected.isEmpty()) {
            Color color = results[selected.get(0)];
            for (int i : selected) {
                if (results[i] != color)
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Get the list of colors that can be changed to.
     * @return A list of changeable colors or null if not applicable.
     */
    public List<Color> getChangeableColor() {
        if (!isSelectedSameColor())
            return null;
        List<Color> changeableColors = new ArrayList<>(List.of(Color.values()));
        changeableColors.remove(results[selected.get(0)]);
        return changeableColors;
    }

    /**
     * Count the number of each color in the dice results.
     * @return A map of color counts.
     */
    Map<Color, Integer> countResult() {
        Map<Color, Integer> counter = new HashMap<>();
        for (Color color : results) {
            int count = counter.getOrDefault(color, 0);
            counter.put(color, count+1);
        }
        return counter;
    }

    /**
     * Get the possible tiles that the active player can place based on their bonus and the dice results.
     * @param activePlayer The player currently taking the turn.
     * @return A list of possible tile names the player can use.
     */
    public List<String> getPossibleTiles(Player activePlayer) {
        List<String> possibleTiles = new ArrayList<>();
        Map<Color, Integer> map = countResult();
        int numWhites = map.getOrDefault(Color.WHITE, 0);
        for (Color color : map.keySet()) {
            if (color == Color.WHITE)
                continue;
            int count = map.get(color) + numWhites;
            int bonus = activePlayer.getBonusAvailable(color);
            // If the count is 1 and there's no bonus, skip this tile
            if (count == 1 && bonus == 0)
                continue;

            // Check the tiles associated with the current color
            for (String tileName : FacadeTile.tileNames.get(color)) {
                int size = Integer.parseInt(tileName.substring(1, 2));

                // Skip tiles that are too large unless a bonus is available
                if (size > count && bonus == 0)
                    break;

                // Add the tile to the possible list if it's usable
                if (size <= count || (size == count + 1 && bonus > 0))
                    if (activePlayer.canUseTile(tileName))
                        possibleTiles.add(tileName);
            }
        }
        return possibleTiles;
    }

    /**
     * Change the color of the selected dice to the specified color.
     * @param color The new color to apply to the selected dice.
     */
    public void setChangeColor(Color color) {
        for (int i : selected) {
            results[i] = color;
        }
    }

    /**
     * Reroll the selected dice to get new random colors.
     */
    public void reroll() {
        for (int i : selected) {
            results[i] = Color.randomColor();
        }
    }
}
