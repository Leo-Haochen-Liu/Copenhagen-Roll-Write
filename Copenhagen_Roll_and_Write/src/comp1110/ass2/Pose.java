package comp1110.ass2;

public record Pose(int x, int y, int orientation) {
    public Pose {
        // Ensure x and y are within the grid boundaries
        assert x >= 0 && x < Grid.NUM_COLS;  // Adjusted to reference columns for x
        assert y >= 0 && y < Grid.NUM_ROWS;  // Adjusted to reference rows for y
        // Ensure the orientation is valid (0° to 270°)
        assert orientation >= 0 && orientation <= 3;
    }

    // Getter for x-coordinate
    public int getX() {
        return x;
    }

    // Getter for y-coordinate
    public int getY() {
        return y;
    }

    // Getter for orientation
    public int getOrientation() {
        return orientation;
    }
}
