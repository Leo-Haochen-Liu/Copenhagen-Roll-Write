package comp1110.ass2;

import comp1110.ass2.gui.GameGUI;

import java.util.*;

/**
 * The Game class represents the core logic of the game.
 * <p>
 * Key functionalities include:<p>
 * - Managing the list of players.<p>
 * - Handling the dice rolls for the game.<p>
 * - Initializing and managing the facade tiles, categorized by color.<p>
 * - Managing the current player's turn and rotating between players.<p>
 * - Checking the game status to determine if it is over.<p>
 * - Declaring the winner once a player reaches the winning score.<p>
 * - Registering players and determining the setup of the game depending on the number of players.<p>
 * - Providing methods for interacting with GUI elements, such as retrieving possible tiles, available dices,
 *   and updating player statuses.<p>
 * @author Haochen Liu
 */
public class Game {
    // List to store the players in the game
    private List<Player> players;

    // Dice object used for rolling in the game
    private Dice dice;

    // Map to represent the available facade tiles, categorized by color
    public Map<String, FacadeTile> facadeSheet;

    // Index to keep track of the current player's turn
    private int currentPlayerIndex;

    // Constructor to initialize the game components
    public Game() {
        this.players = new ArrayList<>();
        this.dice = new Dice();
        this.facadeSheet = new HashMap<>();
        this.initFacadeSheet();
        this.currentPlayerIndex = 0;
    }

    // Method to initialize the facade sheet with tiles, to be implemented
    private void initFacadeSheet() {
        // TODO: set facade sheet
        for (Dice.Color color : Dice.Color.values()) {
            switch (color) {
                case RED -> {
                    FacadeTile.tileNames.put(color, List.of("R2", "R3", "R4", "R5"));
                    facadeSheet.put("R2", new FacadeTile.InfinityTile("R2"));
                    facadeSheet.put("R3", new FacadeTile.InfinityTile("R3"));
                    facadeSheet.put("R4", new FacadeTile.XTile("R4", 2));
                    facadeSheet.put("R5", new FacadeTile.XTile("R5"));
                }
                case BLUE -> {
                    FacadeTile.tileNames.put(color, List.of("B2", "B3", "B4L", "B4R", "B5"));
                    facadeSheet.put("B2", new FacadeTile.InfinityTile("B2"));
                    facadeSheet.put("B3", new FacadeTile.InfinityTile("B3"));
                    facadeSheet.put("B4L", new FacadeTile.XTile("B4L"));
                    facadeSheet.put("B4R", new FacadeTile.XTile("B4R"));
                    facadeSheet.put("B5", new FacadeTile.XTile("B5"));
                }
                case PURPLE -> {
                    FacadeTile.tileNames.put(color, List.of("P2", "P3", "P4", "P5"));
                    facadeSheet.put("P2", new FacadeTile.InfinityTile("P2"));
                    facadeSheet.put("P3", new FacadeTile.InfinityTile("P3"));
                    facadeSheet.put("P4", new FacadeTile.XTile("P4", 2));
                    facadeSheet.put("P5", new FacadeTile.XTile("P5"));
                }
                case GREEN -> {
                    FacadeTile.tileNames.put(color, List.of("G2", "G3", "G4L", "G4R", "G5"));
                    facadeSheet.put("G2", new FacadeTile.InfinityTile("G2"));
                    facadeSheet.put("G3", new FacadeTile.InfinityTile("G3"));
                    facadeSheet.put("G4L", new FacadeTile.XTile("G4L"));
                    facadeSheet.put("G4R", new FacadeTile.XTile("G4R"));
                    facadeSheet.put("G5", new FacadeTile.XTile("G5"));
                }
                case YELLOW -> {
                    FacadeTile.tileNames.put(color, List.of("Y2", "Y3", "Y4L", "Y4R", "Y5"));
                    facadeSheet.put("Y2", new FacadeTile.InfinityTile("Y2"));
                    facadeSheet.put("Y3", new FacadeTile.InfinityTile("Y3"));
                    facadeSheet.put("Y4L", new FacadeTile.XTile("Y4L"));
                    facadeSheet.put("Y4R", new FacadeTile.XTile("Y4R"));
                    facadeSheet.put("Y5", new FacadeTile.XTile("Y5"));
                }
            }
        }
    }


    /**
     * Method to register a number of players in the game, ensures 2 to 4 players
     * @param num number of players to register (must between 2 and 4)
     */
    public void registerPlayer(int num, boolean[] isAI) {
        assert num >= 2 && num <= 4;
        assert num <= isAI.length;
        // TODO: register new players
        for (int i = 0; i < num; i++) {
            if (isAI[i])
                players.add(new Player.AIPlayer(i, facadeSheet));
            else
                players.add(new Player(i, facadeSheet));
        }
        if (num == 2) {
            Player.maxDicesToChooseNonactive = 2;
            ((FacadeTile.XTile) facadeSheet.get("R4")).crossOut();
            ((FacadeTile.XTile) facadeSheet.get("B4R")).crossOut();
            ((FacadeTile.XTile) facadeSheet.get("P4")).crossOut();
            ((FacadeTile.XTile) facadeSheet.get("G4R")).crossOut();
            ((FacadeTile.XTile) facadeSheet.get("Y4R")).crossOut();
            for (Player p : players) {
                AbilityTrack oneTrack = p.getTrack(new Random().nextInt(5));
                oneTrack.advance(2);
            }
        }
        getActivePlayer().setDice(dice);
    }

    // Passes the turn from the current player to the next player.
    public void passToNextPlayer() {
        players.get(currentPlayerIndex).pass();
        if (checkGameOver()) {
            declareWinner();
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            players.get(currentPlayerIndex).setDice(dice);
            dice.setSelected(null);
        }
    }

    // Method to check if the game is over by verifying if any player has reached a score of 12
    boolean checkGameOver() {
        for (Player player : players) {
            if (player.getScore() >= 12) {
                return true;
            }
        }
        return false;
    }

    // Method to determine and announce the winner, to be implemented
    void declareWinner() {
        // determine the player with the highest score and announce the winner
        int highestScore = 0;
        int winnerID = -1;
        for (Player player : players)
            if (player.getScore() > highestScore) {
                highestScore = player.getScore();
                winnerID = player.getId();
            }
        System.out.println("This game ends with winner being player " + winnerID + " whose score is " + highestScore);
    }

    public Dice getDice() {
        return dice;
    }

    public List<String> getPossibleTiles() {
        return dice.getPossibleTiles(players.get(currentPlayerIndex));
    }

    public Player getPlayer(int id) {
        return players.get(id);
    }

    public Player getActivePlayer() {
        return players.get(currentPlayerIndex);
    }

    public boolean isActivePlayer(int id) {
        return id == currentPlayerIndex;
    }

    public List<String> getAvailableDices() {
        List<String> diceList = new ArrayList<>();
        for (Dice.Color color : dice.getResults()) {
            diceList.add(color.toString());
        }
        return diceList;
    }

    public void setPlayerStatus(GameGUI gui) {
        for (Player p : players) {
            p.setTrackInfo(gui);
        }
    }

    public int[] getPlayerScores() {
        int[] scores = new int[players.size()];
        for (Player p : players)
            scores[p.getId()] = p.getScore();
        return scores;
    }
}
