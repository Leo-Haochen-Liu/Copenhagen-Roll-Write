package comp1110.ass2;

/**
 * enum Ability discribing special abbilities
 * related to each color track.
 */
public enum Ability {
    RE_ROLL("Re-Roll", Dice.Color.RED),
    NO_X("No X", Dice.Color.BLUE),
    ONE_X("One X", Dice.Color.PURPLE),
    COLOR_CHANGE("Color Change", Dice.Color.GREEN),
    USE_AGAIN("Use Again", Dice.Color.YELLOW);

    final String name;
    final Dice.Color color;
    Ability(String name, Dice.Color color) {
        this.name = name;
        this.color = color;
    }

    public static Ability getColorAbility(Dice.Color color) {
        for (Ability ability : values())
            if (ability.color == color)
                return ability;
        return null;
    }

    public static Ability getAbilityByName(String name) {
        for (Ability ability : values())
            if (name.startsWith(ability.getName()))
                return ability;
        return null;
    }

    public String getName() {
        return name;
    }

    public Dice.Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name;
    }
}
