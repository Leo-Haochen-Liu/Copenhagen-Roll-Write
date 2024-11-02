package comp1110.ass2;

import java.util.*;

/**
 * Grid recording placement of facade tiles on player's sheet
 * @author Hoachen Liu
 */
public class Grid {
    public static int NUM_ROWS = 9; // Number of rows in the grid
    public static int NUM_COLS = 5; // Number of columns in the grid
    public static Set<Integer> COAT_OF_ARM_ROWS = Set.of(1, 3, 5); // Rows with coat-of-arms bonuses
    public static Set<Integer> COAT_OF_ARM_COLS = Set.of(1, 3); // Columns with coat-of-arms bonuses
    private char[][] grid; // The grid structure to store the tiles
    private Set<String> activeCOAs;

    // A map to store the status (all windows or with X) and corresponding score for rows and columns
    private Map<String, Integer> completionMap;
    private boolean completionNeedsUpdate;

    public Grid() {
        this.grid = new char[NUM_COLS][NUM_ROWS];
        for (int i = 0; i < NUM_COLS; i++) {
            for (int j = 0; j < NUM_ROWS; j++) {
                grid[i][j] = 0;
            }
        }
        this.completionMap = new HashMap<>();
        this.completionNeedsUpdate = false;
        this.activeCOAs = new HashSet<>();
    }

    public char[][] getGrid() {
        return grid;
    }

    public Set<String> getActiveCOAs() {
        if (completionNeedsUpdate)
            checkCompletion();
        return activeCOAs;
    }

    public void resetActiveCOAs() {
        activeCOAs.clear();
    }

    public void useCOA() {
        // remove one element as used
        String a = activeCOAs.iterator().next();
        activeCOAs.remove(a);
    }

    /**
     * Check for completed rows and columns and store results in the map
     * @author Haochen Liu
     */
    void checkCompletion() {
        // Traverse each cell from top to bottom.
        // If an empty cell is found, the row and column
        // corresponding to that cell are considered 'incomplete',
        // and these rows and columns can be skipped during subsequent checks.

        completionNeedsUpdate = false;

        Set<Integer> incompleteCol = new HashSet<>();

        // Check rows for completion
        for (int row = 0; row < NUM_ROWS; row++) {
            if (completionMap.containsKey("row_o_" + row) || completionMap.containsKey("row_x_" + row)) continue;

            boolean rowCompleted = true;
            boolean allWindows = true;

            for (int col = 0; col < NUM_COLS; col++) {
                if (grid[col][row] == 0) {
                    rowCompleted = false;
                    incompleteCol.add(col);
                    break;
                }
                if (grid[col][row] == FacadeTile.BRICK) {
                    allWindows = false;
                }
            }

            if (rowCompleted) {
                if (COAT_OF_ARM_ROWS.contains(row))
                    activeCOAs.add("row_" + row);
                if (allWindows) {
                    completionMap.put("row_o_" + row, 2); // Row with all windows earns 2 points
                } else {
                    completionMap.put("row_x_" + row, 1); // Row with at least one brick earns 1 point
                }
            }
        }

        // Check columns for completion
        for (int col = 0; col < NUM_COLS; col++) {
            if (completionMap.containsKey("col_o_" + col) || completionMap.containsKey("col_x_" + col) || incompleteCol.contains(col)) continue;

            boolean colCompleted = true;
            boolean allWindows = true;

            for (int row = 0; row < NUM_ROWS; row++) {
                if (grid[col][row] == 0) {
                    colCompleted = false;
                    break;
                }
                if (grid[col][row] == FacadeTile.BRICK) {
                    allWindows = false;
                }
            }

            if (colCompleted) {
                if (COAT_OF_ARM_COLS.contains(col))
                    activeCOAs.add("col_" + col);
                if (allWindows) {
                    completionMap.put("col_o_" + col, 4); // Column with all windows earns 4 points
                } else {
                    completionMap.put("col_x_" + col, 2); // Column with at least one brick earns 2 points
                }
            }
        }
    }

    // Getter for summing score
    public int getGridScore() {
        if (completionNeedsUpdate)
            checkCompletion();
        if (completionMap.isEmpty())
            return 0;
        return completionMap.values().stream().reduce(0, Integer::sum);
    }

    /**
     * Method to update the grid with a new facade tile
     * @param tile tile to place
     * @param pose coordinates
     * @author Haochen Liu
     */
    public void updateGrid(FacadeTile tile, Pose pose) {
        assert canBePlaced(tile, pose);
        int x = pose.getX(); // Get the x-coordinate from the pose
        int y = pose.getY(); // Get the y-coordinate from the pose
        int orientation = pose.getOrientation(); // Get the tile's orientation (0°, 90°, 180°, or 270°)
        char[][] shape = tile.getRotatedShape(orientation); // Get the tile shape based on the rotation

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) { // Only check the positions that are part of the tile (non-empty)
                    int newX = x + i;
                    int newY = y + j;

                    grid[newX][newY] = shape[i][j];
                }
            }
        }

        completionNeedsUpdate = true;
    }

    /**
     * Logic to check if the tile can be placed on the grid
     * @param tile tile to check
     * @param pose coordinates of placement
     * @return if can be placed at pose
     * @author Haochen Liu
     */
    public boolean canBePlaced(FacadeTile tile, Pose pose) {
        int x = pose.getX(); // Get the x-coordinate from the pose
        int y = pose.getY(); // Get the y-coordinate from the pose
        int orientation = pose.getOrientation(); // Get the tile's orientation (0°, 90°, 180°, or 270°)
        char[][] shape = tile.getRotatedShape(orientation); // Get the tile shape based on the rotation

        // Step 1: Boundary check - Ensure all parts of the tile fit within the grid
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) { // Only check the positions that are part of the tile (non-empty)
                    int newX = x + i;
                    int newY = y + j;

                    // Check if the new position is outside the grid boundaries
                    if (newX < 0 || newX >= Grid.NUM_COLS || newY < 0 || newY >= Grid.NUM_ROWS) {
                        return false; // Tile is out of bounds
                    }

                    // Step 2: Overlap check - Ensure the tile does not overlap with existing tiles
                    if (grid[newX][newY] != 0) {
                        return false; // Tile overlaps with another placed tile
                    }
                }
            }
        }

        // Step 3: Placement rule check - Ensure the tile is placed at the bottom or adjacent to an existing tile
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int newX = x + i;
                    int newY = y + j;

                    // Check if the tile is on the bottom row or adjacent to an existing tile
                    if (newY == 0) {
                        return true;
                    } else if ((newY > 0 && grid[newX][newY - 1] != 0)) { // Below
                        return true;
                    } else {
                        break;
                    }
                }
            }
        }

        // If no tile is either on the bottom row or adjacent to an existing tile, it cannot be placed
        return false;
    }
}
