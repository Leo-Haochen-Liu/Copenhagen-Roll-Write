package comp1110.ass2;

import java.util.*;
import java.util.function.Predicate;

/**
 * FacadeTile is an abstract class that represents the tiles in the game.
 * It holds information such as the shape, size, and color of the tiles.
 * Subclasses can implement specific tile types such as InfinityTile and XTile.
 */
public abstract class FacadeTile {
    final char[][] shape; // The original shape of the tile
    final int size;
    final String name;
    final Dice.Color color;
    public static final char WINDOW = 'o';
    public static final char BRICK = 'x';
    static Map<Dice.Color, List<String>> tileNames = new HashMap<>();

    /**
     * Constructor to initialize the tile's name, size, color, and shape.
     * @param name The name of the tile, which determines its size, color, and shape.
     */
    private FacadeTile(String name) {
        this.name = name;
        this.size = Integer.parseInt(name.substring(1, 2));
        this.color = switch (name.charAt(0)) {
            case 'R' -> Dice.Color.RED;
            case 'B' -> Dice.Color.BLUE;
            case 'P' -> Dice.Color.PURPLE;
            case 'G' -> Dice.Color.GREEN;
            case 'Y' -> Dice.Color.YELLOW;
            case 'S' -> null;
            default -> throw new IllegalStateException("Unexpected color: " + name.charAt(0));
        };

        // Determine the shape of the tile based on its name
        this.shape = switch (name) {
            case "S1X" -> new char[][]{{BRICK}};
            case "S1O" -> new char[][]{{WINDOW}};
            case "R2", "B2", "P2", "G2", "Y2" -> new char[][]{{WINDOW, WINDOW}};
            case "R3", "B3", "Y3" -> new char[][]{{WINDOW, 0}, {WINDOW, WINDOW}};
            case "P3", "G3" -> new char[][]{{WINDOW, WINDOW, WINDOW}};
            case "R4" -> new char[][]{{WINDOW, WINDOW}, {WINDOW, WINDOW}};
            case "R5" -> new char[][]{{WINDOW, 0}, {WINDOW, WINDOW}, {WINDOW, WINDOW}};
            case "B4L" -> new char[][]{{WINDOW, 0, 0}, {WINDOW, WINDOW, WINDOW}};
            case "B4R" -> new char[][]{{WINDOW, WINDOW, WINDOW}, {WINDOW, 0, 0}};
            case "B5" -> new char[][]{{WINDOW, 0, 0}, {WINDOW, WINDOW, WINDOW}, {WINDOW, 0, 0}};
            case "P4" -> new char[][]{{WINDOW, WINDOW, WINDOW, WINDOW}};
            case "P5" -> new char[][]{{WINDOW}, {WINDOW}, {WINDOW}, {WINDOW}, {WINDOW}};
            case "G4L" -> new char[][]{{0, WINDOW, 0}, {WINDOW, WINDOW, WINDOW}};
            case "G4R" -> new char[][]{{WINDOW, WINDOW, WINDOW}, {0, WINDOW, 0}};
            case "G5" -> new char[][]{{0, WINDOW, 0}, {WINDOW, WINDOW, WINDOW}, {0, WINDOW, 0}};
            case "Y4L" -> new char[][]{{WINDOW, WINDOW, 0}, {0, WINDOW, WINDOW}};
            case "Y4R" -> new char[][]{{0, WINDOW, WINDOW}, {WINDOW, WINDOW, 0}};
            case "Y5" -> new char[][]{{WINDOW, WINDOW, 0}, {0, WINDOW, 0}, {0, WINDOW, WINDOW}};
            default -> throw new IllegalStateException("Unexpected shape: " + name);
        };
    }

    // Getter methods

    /**
     * Returns the color of the tile.
     * @return The tile's color.
     */
    public Dice.Color getColor() {
        return color;
    }

    /**
     * Returns the name of the tile.
     * @return The tile's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the size of the tile.
     * @return The tile's size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the shape of the tile.
     * @return The tile's shape (2D char array).
     */
    public char[][] getShape() {
        return shape;
    }

    /**
     * Updates the shape of the tile by setting bricks ('x') or windows ('o').
     * @param window A predicate that returns true if the position should be a window, false for brick.
     */
    void setBrick(Predicate<Integer> window) {
        int i = 0;
        for (int j = 0; j < shape.length; j++) {
            for (int k = 0; k < shape[j].length; k++) {
                if (shape[j][k] == 0)
                    continue;
                if (window.test(i))
                    shape[j][k] = WINDOW;
                else
                    shape[j][k] = BRICK;
                i++;
            }
        }
    }

    /**
     * Rotates the shape of the tile based on the orientation (0 = no rotation, 1 = 90°, 2 = 180°, 3 = 270°).
     * @param orientation The orientation of the shape.
     * @return The rotated shape.
     */
    public char[][] getRotatedShape(int orientation) {
        return switch (orientation) {
            case 0 -> shape; // No rotation, return original shape
            case 1 -> rotate90(shape); // Rotate 90 degrees
            case 2 -> rotate180(shape); // Rotate 180 degrees
            case 3 -> rotate270(shape); // Rotate 270 degrees
            default -> throw new IllegalArgumentException("Invalid orientation");
        };
    }

    // Helper methods for rotating the tile's shape

    /**
     * Rotates the shape by 90 degrees clockwise.
     * @param shape The original shape.
     * @return The rotated shape.
     */
    private static char[][] rotate90(char[][] shape) {
        int rows = shape.length;
        int cols = shape[0].length;
        char[][] rotated = new char[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - i - 1] = shape[i][j];
            }
        }
        return rotated;
    }

    /**
     * Rotates the shape by 180 degrees clockwise.
     * @param shape The original shape.
     * @return The rotated shape.
     */
    private static char[][] rotate180(char[][] shape) {
        int rows = shape.length;
        int cols = shape[0].length;
        char[][] rotated = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[rows - i - 1][cols - j - 1] = shape[i][j];
            }
        }
        return rotated;
    }

    /**
     * Rotates the shape by 270 degrees clockwise.
     * @param shape The original shape.
     * @return The rotated shape.
     */
    private static char[][] rotate270(char[][] shape) {
        int rows = shape.length;
        int cols = shape[0].length;
        char[][] rotated = new char[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[cols - j - 1][i] = shape[i][j];
            }
        }
        return rotated;
    }

    // Subclasses representing specific types of tiles

    /**
     * InfinityTile represents a tile with infinite use.
     */
    public static class InfinityTile extends FacadeTile {
        InfinityTile(String name) {
            super(name);
        }
    }

    /**
     * XTile represents a tile that can be crossed out after use, with a limited number of uses.
     */
    public static class XTile extends FacadeTile {
        // Indicates if the tile has been crossed out
        private int numAvailable;
        private boolean useAgain;

        XTile(String name) {
            super(name);
            this.numAvailable = 1;
            this.useAgain = false;
        }

        XTile(String name, int num) {
            super(name);
            assert num > 0;
            this.numAvailable = num;
        }

        // Getter for the crossed out status
        boolean isCrossedOut() {
            return numAvailable <= 0;
        }

        // Method to cross out the tile, reducing its available uses
        void crossOut() {
            this.numAvailable -= 1;
        }
    }
}
