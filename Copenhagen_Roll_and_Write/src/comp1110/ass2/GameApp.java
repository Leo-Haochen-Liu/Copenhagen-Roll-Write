package comp1110.ass2;

import comp1110.ass2.gui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.*;

/**
 * This class represents the main entry point of the game application.
 * It sets up the GUI, manages the game logic, and handles user interactions
 * such as tile placement, dice selection, and game actions.
 * @author Xinrui Tan
 */
public class GameApp extends Application {

    GameGUI gui;
	Game game;
	LinkedHashSet<String> actionButton; // record possible actions for the button
	FacadeTile tilePlaced; // record the tile (with color) being placed by the active player in a round
	boolean[] advanced; // track if players advanced tracks in a round

	/**
	 * This method is called when the JavaFX application is started.
	 * It initializes the game, sets up the scene, and configures callbacks for user interactions.
	 */
    @Override
    public void start(Stage stage) throws Exception {
		game = new Game();
        gui = new GameGUI();
		actionButton = new LinkedHashSet<>();
        Scene scene = new Scene(gui, GameGUI.WINDOW_WIDTH, GameGUI.WINDOW_HEIGHT);

	// This is where you should set up callbacks (or at least one
	// callback, for the start-of-game event).

	// The following are only provided as examples; you should
	// replace them with your own.

		// Start a new game with the specified number of players
		gui.setOnStartGame((np, isAI) -> {
			gui.setMessage("start new game with " + np + " players");
			game.registerPlayer(np, isAI);
			advanced = new boolean[np];
			beforePlay();
		});

		// Message when a tile is selected
		gui.setOnTileSelected((s) -> {
			gui.setMessage("Tile " + s + " selected.");
			if (!game.isActivePlayer(gui.getSelectedPlayer())) {
				gui.setMessage("Warning: The current shown building is for a non-active player");
				gui.clearTileSelection();
			}
			if (tilePlaced != null && s != null && s.equals("S1O"))
				gui.clearTrackSelection(); // allow only one bonus action for COA
		});

		// Handle the placement of a tile
		gui.setOnTilePlaced((p) -> {
			if (!game.isActivePlayer(gui.getSelectedPlayer())) {
				gui.setMessage("Warning: The current shown building is for a non-active player");
				gui.clearTileSelection();
				return;
			}
			Player current = game.getActivePlayer();
			if (!current.canPlaceTile(p))
				gui.setMessage("Cannot place tile " + p.getTileName() + " at (x=" + p.getX() + ", " + p.getY() + ")!");
			else {
				FacadeTile tile = current.placeTile(p, gui);
				if (game.facadeSheet.containsValue(tile))
					tilePlaced = tile; // this is to ignore single tiles
				actionButton.clear();
				gui.setAvailableActions(new ArrayList<>());
				gui.setMessage("tile placed: " + p);
				gui.clearDiceSelection();
				List<String> sTiles = new ArrayList<>();
				if (current.getAbilityAvailable(Ability.ONE_X) > 0)
					sTiles.add("S1X");
				if (current.getCoaBonuses() > 0)
					sTiles.add("S1O");
				gui.setAvailableTiles(sTiles);
				current.setTrackInfo(gui);
				checkGameOver();
			}
		});

		// Callback for dice selection
		gui.setOnDiceSelectionChanged((i) -> {
			List<Integer> diceSelection = gui.getSelectedDice();
			gui.setMessage("dice selection: " + diceSelection);
			if (tilePlaced == null) {
				// dices should only be changed before tile being placed
				Dice dice = game.getDice();
				Player current = game.getActivePlayer();
				dice.setSelected(diceSelection);
				if (!diceSelection.isEmpty() && current.getAbilityAvailable(Ability.RE_ROLL) > 0)
					actionButton.add(Ability.RE_ROLL.getName());
				if (dice.isSelectedSameColor() && current.getAbilityAvailable(Ability.COLOR_CHANGE) > 0)
					for (Dice.Color color : dice.getChangeableColor())
						actionButton.add(Ability.COLOR_CHANGE.getName() + " to "+ color);
			}
			if (diceSelection.isEmpty()) {
				// remove dice related ability in action button when cancel selection
				actionButton.remove(Ability.RE_ROLL.getName());
				for (Dice.Color color : Dice.Color.values())
					actionButton.remove(Ability.COLOR_CHANGE.getName() + " to "+ color);
			}
			gui.setAvailableActions(actionButton.stream().toList());
		});

		// Callback for performing a game action - this button only used for reroll, nox, and change color ability
		gui.setOnGameAction((s) -> {
			Ability ability = Ability.getAbilityByName(s);
			if (tilePlaced == null) {
				final Player current = game.getActivePlayer();
				if (ability == null) {
					gui.setMessage("Internal error: unknown action: " + s);
					return;
				} else if (List.of(Ability.RE_ROLL, Ability.NO_X, Ability.COLOR_CHANGE).contains(ability)) {
					if (current.getAbilityAvailable(ability) == 0) {
						gui.setMessage("Internal error: should not have action "+s+" available");
						actionButton.remove(s);
					}
				}
				switch (ability) {
					case RE_ROLL -> {
						current.rerollDice();
						current.useAbility(ability);
						gui.setAvailableDice(game.getAvailableDices());
						gui.setAvailableTiles(game.getPossibleTiles());
						current.setTrackInfo(gui);
						gui.setMessage("Selected dice(s) re-rolled");
					}
					case NO_X -> {
						if (!current.isNoX()) {
							current.setNoX();
							gui.setMessage("No X ability will be used once the selected tile is placed");
						} else {
							gui.setMessage("Please don't set No X for multiple times");
						}
					}
					case COLOR_CHANGE -> {
						Dice.Color color = Dice.Color.getColor(s);
						if (color == null) {
							gui.setMessage("Internal error: unknown action: " + s);
							return;
						}
						current.changeDiceColor(color);
						current.useAbility(ability);
						gui.setAvailableDice(game.getAvailableDices());
						gui.setAvailableTiles(game.getPossibleTiles());
						current.setTrackInfo(gui);
						gui.setMessage("Selected dice(s) changed to " + color);
					}
					default -> gui.setMessage("Internal error: unsupported action: " + s);
				}
				gui.setAvailableActions(actionButton.stream().toList());
			} else {
				if (ability != null)
					gui.setMessage("Cannot use ability "+ability+" after tile placement confirmed");
				else
					gui.setMessage("Internal error: unknown action: " + s);
			}
		});

		// send message when select track
		gui.setOnTrackSelectionChanged((i) -> {
			Dice dice = game.getDice();
			List<Integer> selectedTracks = gui.getSelectedTracks();
			if (selectedTracks.isEmpty())
				return;
			if (tilePlaced != null && !game.isActivePlayer(gui.getSelectedPlayer()) &&
					dice.validTrackToAdvanceForNonactivePlayer(tilePlaced, selectedTracks) > 0 &&
					!advanced[gui.getSelectedPlayer()]) {
				gui.setMessage("Press \"Confirm\" button to advance track: " + selectedTracks);
			} else if (tilePlaced != null && game.isActivePlayer(gui.getSelectedPlayer()) &&
					game.getActivePlayer().getCoaBonuses() > 0 && selectedTracks.size() == 1) {
				gui.setMessage("Press \"Confirm\" button to use COA bonus - advance 2 steps on track: " + selectedTracks);
				gui.clearTileSelection();
			} else {
				int id = gui.getSelectedPlayer();
				String msg = (game.isActivePlayer(id)) ? "Active player " : "Non-active player ";
				msg += id;
				gui.setMessage(msg + " meaningless track selection: " + selectedTracks);
			}
		});

		// confirm button used for track advance
		gui.setOnConfirm((s) -> {
			Dice dice = game.getDice();
			List<Integer> selectedTracks = gui.getSelectedTracks();
			if (selectedTracks.isEmpty())
				return;
			int valid = dice.validTrackToAdvanceForNonactivePlayer(tilePlaced, selectedTracks);
			Player player = game.getPlayer(gui.getSelectedPlayer());
			if (tilePlaced != null && !game.isActivePlayer(gui.getSelectedPlayer()) &&
					valid > 0 && !advanced[gui.getSelectedPlayer()]) {
				// non-active players:
				// 1) must only advance track after active player placed a tile
				// 2) the tracks selected must all have a valid dice left to use (determined by valid)
				// 3) the player must not advance track before in this round
				player.advanceTrack(selectedTracks, valid);
				gui.setMessage("track advanced: " + selectedTracks);
				player.setTrackInfo(gui);
				checkGameOver();
				gui.clearTrackSelection();
				advanced[player.getId()] = true;
			} else if (tilePlaced != null && game.isActivePlayer(gui.getSelectedPlayer()) &&
					player.getCoaBonuses() > 0 && selectedTracks.size() == 1) {
				// active player with coa bonuses to use can advance track
				// they must only select one track
				// they can only perform one of the two possible actions,
				// i.e. once select tracks, clear selection for tiles; also true the other way around
				player.coaAdvanceTrack(selectedTracks.get(0));
				gui.setMessage("use COA bonus - track advanced: " + selectedTracks);
				player.setTrackInfo(gui);
				checkGameOver();
				gui.clearTrackSelection();
				gui.clearTileSelection();
				gui.setAvailableTiles(new ArrayList<>());
			} else
				gui.setMessage("No action to confirm: " + s);
		});

		// Passing turn to the next player
		gui.setOnPass((s) -> {
			if (tilePlaced == null)
				return;
			gui.setMessage("pass: " + s);
			List<Integer> notAdvancedPlayers = new ArrayList<>();
			for (int i = 0; i < advanced.length; i++) {
				if (!game.isActivePlayer(i) && !advanced[i] && game.getDice().canPlayerAdvance(tilePlaced, game.getPlayer(i)))
					notAdvancedPlayers.add(i);
			}
			if (!notAdvancedPlayers.isEmpty()) {
				gui.setMessage("Non-active player(s) :" + notAdvancedPlayers + "have not advance tracks.");
				return;
			}
			checkGameOver();
			if (!game.checkGameOver()) {
				game.passToNextPlayer();
				beforePlay();
			}
		});

	// Start the application:
        stage.setScene(scene);
        stage.setTitle("Copenhagen Roll & Write");
        stage.show();
    }

	/**
	 * Check if game is over
	 * if so, end the game to ending interface.
	 */
	private void checkGameOver() {
		if (game.checkGameOver()) {
			game.declareWinner();
			gui.endGame(game.getPlayerScores());
		}
	}

	/**
	 * Prepares for the next player's turn by resetting state and updating the GUI.
	 */
	public void beforePlay() {
		tilePlaced = null;
		actionButton.clear();
        Arrays.fill(advanced, false);
		Player current = game.getActivePlayer();
		gui.setControlPlayer(current.getId());
		game.setPlayerStatus(gui);
		current.rollDice();
		gui.setAvailableDice(game.getAvailableDices());
		gui.setAvailableTiles(game.getPossibleTiles());
		if (current.getAbilityAvailable(Ability.NO_X) > 0) {
			actionButton.add(Ability.NO_X.getName());
		}
		gui.setAvailableActions(actionButton.stream().toList());
	}

}
