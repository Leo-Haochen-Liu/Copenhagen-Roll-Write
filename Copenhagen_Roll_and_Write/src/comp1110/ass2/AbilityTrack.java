package comp1110.ass2;

/**
 * The AbilityTrack class represents a track for each color in the game,
 * which stores the player's progress in unlocking bonuses and abilities.
 * Each track consists of multiple steps, and players can advance along
 * the track to gain bonuses or abilities.
 */
public class AbilityTrack {

    /**
     * Enum to represent different types of steps on the track.
     * - NONE: No bonus or ability.
     * - BONUS: A bonus that can be used by the player.
     * - ABILITY: An ability that can be used by the player.
     */
    public enum Power {
        NONE,
        BONUS,
        ABILITY
    }

    private final Dice.Color color;      // The color associated with this track
    private final Ability ability;       // The ability associated with this color
    private int pos;                     // The current position of the player on this track
    private final Power[] shape;         // The track structure (step-by-step bonuses and abilities)
    private int bonusAvailable;          // Number of bonuses available for this track
    private int abilityAvailable;        // Number of abilities available for this track

    /**
     * Constructor to create an AbilityTrack for a specific color.
     * @param color The color associated with the track.
     * @param shape The structure of the track, defining the steps as NONE, BONUS, or ABILITY.
     */
    public AbilityTrack(Dice.Color color, Power[] shape) {
        this.ability = Ability.getColorAbility(color);
        this.color = color;
        this.pos = 0;
        this.shape = shape;
        this.bonusAvailable = 0;
        this.abilityAvailable = (color == Dice.Color.RED) ? 2 : 0;
    }

    /**
     * Advances the player's position by one step on the track.
     * Increments the bonus or ability available if the step contains one.
     */
    void advance() {
        if (!isTrackComplete()){
            this.pos++;
            if (shape[pos - 1] == Power.BONUS)
                bonusAvailable++;
            if (shape[pos - 1] == Power.ABILITY)
                abilityAvailable++;
        }
    }

    /**
     * Advances the player's position by a given number of steps on the track.
     * @param advanceStep The number of steps to advance.
     */
    void advance(int advanceStep) {
        assert advanceStep <= 2;
        for (int i = 0; i < advanceStep; i++) {
            advance();
        }
    }

    /**
     * Uses one ability if available. Throws an exception if no ability is available.
     */
    public void useAbility() {
        if (abilityAvailable > 0) {
            abilityAvailable--;
        } else {
            throw new IllegalStateException("No ability available to use");
        }
    }

    /**
     * Uses one bonus if available. Throws an exception if no bonus is available.
     */
    public void useBonus() {
        if (bonusAvailable > 0) {
            bonusAvailable--;
        } else {
            throw new IllegalStateException("No bonus available to use");
        }
    }

    // Getters

    public int getPos() {
        return pos;
    }

    public Ability getAbility() {
        return ability;
    }

    public Dice.Color getColor() {
        return color;
    }

    /**
     * Checks if the track is complete (i.e., if the player has advanced through all steps).
     * @return true if the track is complete, false otherwise.
     */
    public boolean isTrackComplete() {
        return pos == shape.length;
    }

    public int getAbilityAvailable() {
        return abilityAvailable;
    }

    /**
     * Gets the number of steps until the next ability is unlocked.
     * @return The number of steps to the next ability, or -1 if no more abilities.
     */
    public int getNextAbility() {
        for (int i = pos; i < shape.length; i++) {
            if (shape[i] == Power.ABILITY)
                return i - pos + 1;
        }
        return -1;
    }

    public int getBonusAvailable() {
        return bonusAvailable;
    }

    /**
     * Gets the number of steps until the next bonus is unlocked.
     * @return The number of steps to the next bonus, or -1 if no more bonuses.
     */
    public int getNextBonus() {
        for (int i = pos; i < shape.length; i++) {
            if (shape[i] == Power.BONUS)
                return i - pos + 1;
        }
        return -1;
    }
}
