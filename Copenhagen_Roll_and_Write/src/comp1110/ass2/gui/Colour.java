package comp1110.ass2.gui;

import javafx.scene.paint.Color;
import java.util.List;

public enum Colour {
    RED(Color.RED, "Red"),
    BLUE(Color.BLUE, "Blue"),
    PURPLE(Color.PURPLE, "Purple"),
    GREEN(Color.GREEN, "Green"),
    YELLOW(Color.YELLOW, "Yellow"),
    WHITE(Color.WHITE, "White"),
    GRAY(Color.GREY, "Gray");

    private Color fxColor;
    private String name;

    Colour(Color fxColor, String name) {
        this.fxColor = fxColor;
        this.name = name;
    }

    public Color getFXColor() {
        return fxColor;
    }

    public String toString() {
        return name;
    }

    public static Colour getColour(String name) {
        for (var value : Colour.values()) {
            if (value.toString().startsWith(name)) {
                return value;
            }
        }
        return null;
    }

}
