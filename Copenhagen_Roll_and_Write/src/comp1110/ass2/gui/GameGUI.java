package comp1110.ass2.gui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;

import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.List;

public class GameGUI extends BorderPane {

    static final int BUILDING_WIDTH = 5;
    static final int BUILDING_HEIGHT = 9;

    static final int TITLE_FONT_SIZE = 24;
    static final int LARGE_FONT_SIZE = 20;
    static final int MEDIUM_FONT_SIZE = 18;
    static final int SMALL_FONT_SIZE = 12;

    private static final Border boxBorder = new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderStroke.MEDIUM));

    // GUI components
    private Label current_player_view;
    private TabPane player_selector;
    private PlayerStateView player_view;
    private BuildingView building_view;
    private LibraryView library_view;
    private FlowPane price_view;
    private DiceView dice_view;
    private StackPane control_view;
    private Node game_setup_controls;
    private Node current_player_controls;
    private Button b_reroll;
    private Button b_confirm;
    private Button b_pass;
    private MenuButton b_colour_change;

    private BiConsumer<Integer, boolean[]> onStartGame;
    private Consumer<String> onTileSelected;
    private Consumer<Placement> onTilePlaced;
    private Consumer<String> onConfirm;
    private Consumer<String> onPass;
    private Consumer<String> onGameAction;
    //private Consumer<String> onReroll;
    //private Consumer<String> onColourChange;

    private Placement candidate = null;
    private int candidate_index = -1;

    private void makeSetupControls() {
        VBox controls = new VBox();
        controls.setSpacing(4);
        // controls.setAlignment(Pos.CENTER);
        ToggleGroup np = new ToggleGroup();
        FlowPane npPane = new FlowPane();
        npPane.setHgap(2);
        npPane.getChildren().add(new Text("Number of players:"));
        for (int i = 2; i <= 4; i++) {
            RadioButton b = new RadioButton(Integer.toString(i));
            b.setToggleGroup(np);
            b.setUserData(i);
            npPane.getChildren().add(b);
        }
        controls.getChildren().add(npPane);
        FlowPane aiPane = new FlowPane();
        aiPane.setHgap(2);
        aiPane.getChildren().add(new Text("AI:"));
        for (int i = 0; i < 4; i++) {
            CheckBox b = new CheckBox(Integer.toString(i + 1));
            aiPane.getChildren().add(b);
        }
        controls.getChildren().add(aiPane);
        FlowPane gsPane = new FlowPane();
        gsPane.setHgap(2);
        gsPane.getChildren().add(new Text("Init state:"));
        TextField initStateString = new TextField();
        // initStateString.setPrefWidth(800);
        gsPane.getChildren().add(initStateString);
        controls.getChildren().add(gsPane);
        Button b_start = new Button("Start");
        b_start.setOnAction(e -> {
		Toggle np_selected = np.getSelectedToggle();
		boolean[] isAI = new boolean[4];
		for (int i = 0; i < 4; i++)
		    isAI[i] = ((CheckBox) (aiPane.getChildren().get(i + 1))).isSelected();
		String gameString = initStateString.getText();
		if (gameString.length() > 0) {
		    doStart(0, gameString, isAI);
		}
		else if (np_selected != null) {
		    int n = (Integer) np_selected.getUserData();
		    //System.out.println("selected " + n + " players");
		    doStart(n, null, isAI);
		}
	    });
        controls.getChildren().add(b_start);
        game_setup_controls = controls;
    }

    private void doStart(int nPlayers, String gameString, boolean[] isAI) {
        player_selector.getTabs().clear();
        for (int i = 0; i < nPlayers; i++) {
            Tab t = new Tab("Player " + Integer.toString(i));
            player_selector.getTabs().add(t);
        }
        control_view.getChildren().clear();
        control_view.getChildren().add(current_player_controls);
        player_selector.getSelectionModel().select(0);
        player_selector.getSelectionModel().selectedIndexProperty().addListener(
            (property, old_value, new_value) -> {
		showState();
	    });
	if (onStartGame != null)
	    onStartGame.accept(nPlayers, isAI);
        showState();
    }

    private Pane makeGameOverControls(int[] finalScores) {
        GridPane controls = new GridPane();
        controls.setAlignment(Pos.CENTER);
        controls.setHgap(10);
        controls.setVgap(4);
        Text header = new Text("Final scores");
        header.setFont(Font.font(TITLE_FONT_SIZE));
        // header.setAlignment(Pos.CENTER);
        controls.add(header, 0, 0, 2, 1);
        GridPane.setHalignment(header, HPos.CENTER);
	for (int i = 0; (i < finalScores.length) && (i < 4); i++) {
	    Text player_i = new Text("Player" + Integer.toString(i));
	    controls.add(player_i, 0, 1 + i, 1, 1);
	    GridPane.setHalignment(player_i, HPos.LEFT);
	    Text score_i = new Text(Integer.toString(finalScores[i]));
	    controls.add(score_i, 1, 1 + i, 1, 1);
	    GridPane.setHalignment(score_i, HPos.RIGHT);
	}
        Button b_again = new Button("Play again");
        b_again.setOnAction(e -> {
		control_view.getChildren().clear();
		control_view.getChildren().add(game_setup_controls);
		showState();
	    });
	int n = Math.min(finalScores.length, 4);
        controls.add(b_again, 0, n + 1, 2, 1);
        GridPane.setHalignment(b_again, HPos.CENTER);
        Button b_quit = new Button("Quit");
        b_quit.setOnAction(e -> Platform.exit());
        controls.add(b_quit, 0, n + 2, 2, 1);
        GridPane.setHalignment(b_quit, HPos.CENTER);
        return controls;
    }

    private void makePlayerControls() {
        GridPane controls = new GridPane();
        controls.setHgap(3);
        controls.setVgap(3);
        controls.setPadding(new Insets(3, 3, 3, 3));
        // b_reroll = new Button("Reroll");
        // controls.add(b_reroll, 0, 0);
	// b_reroll.setOnAction((e) -> {
	// 	if (onReroll != null)
	// 	    onReroll.accept(b_reroll.getText());
	//     });
        b_confirm = new Button("Confirm (player #)");
        controls.add(b_confirm, 0, 1);
	b_confirm.setOnAction((e) -> {
		if (candidate != null) {
		    Placement tmp = candidate;
		    candidate = null;
		    library_view.clearSelection();
		    if (onTilePlaced != null)
			onTilePlaced.accept(tmp);
		    showState();
		}
		else if (onConfirm != null) {
		    onConfirm.accept(b_confirm.getText());
		    showState();
		}
	    });
        b_pass = new Button("Pass (player #)");
        controls.add(b_pass, 0, 2);
	b_pass.setOnAction((e) -> {
		if (onPass != null) {
		    onPass.accept(b_pass.getText());
		    showState();
		}
	    });
        b_colour_change = new MenuButton("Action...");
        // for (int i = 0; i < 5; i++) {
        //     Colour c = Colour.values()[i];
        //     MenuItem toC = new MenuItem("to " + c.toString());
	//     toC.setOnAction((e) -> {
	// 	    if (onColourChange != null)
	// 		onColourChange.accept(toC.getText());
	// 	});
        //     // coa_XX.setTextFill(c.getFXColor());
        //     b_colour_change.getItems().add(toC);
        // }
        controls.add(b_colour_change, 0, 0);
        current_player_controls = controls;
    }

    private void showState() {
	int i = player_selector.getSelectionModel().getSelectedIndex();
	// System.out.println("i = " + i);
	if (candidate != null && (i == candidate_index))
	    building_view.show(i, candidate, false);
	else
	    building_view.show(i);
	if (i >= 0) {
	    player_view.show(i);
	}
    }

    private void makeMainLayout() {
        current_player_view = new Label("                 ");
        current_player_view.setFont(Font.font(24));
        this.setTop(current_player_view);

        VBox left = new VBox();
        left.setBorder(boxBorder);
        left.setAlignment(Pos.CENTER);
        left.setFillWidth(true);

        player_selector = new TabPane();
        player_selector.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        left.getChildren().add(player_selector);
        GridPane player_pane = new GridPane();
        //player_pane.setBorder(boxBorder);
        //player_pane.setSpacing(4);
        player_pane.setHgap(10);
        player_pane.setVgap(4);
        player_pane.setPadding(new Insets(2, 2, 2, 2));
        player_pane.setAlignment(Pos.CENTER);

        player_view = new PlayerStateView();
        player_pane.add(player_view, 0, 0);
        //player_pane.setHgrow(player_view, Priority.ALWAYS);
        building_view = new BuildingView(BUILDING_WIDTH, BUILDING_HEIGHT);
        building_view.setFocusTraversable(true);
        player_pane.add(building_view, 1, 0, 1, 2);
        //player_pane.setHgrow(building_view, Priority.ALWAYS);
        for (int x = 0; x < BUILDING_WIDTH; x++)
            for (int y = 0; y < BUILDING_HEIGHT; y++) {
                final int fx = x;
                final int fy = y;
                building_view.getSquare(x, y).setOnMouseClicked(e -> {
			if (candidate != null) {
			    if (e.getButton() == MouseButton.SECONDARY) {
				// interaction_state.setBrick(fx, fy);
			    }
			    else {
				candidate.setPosition(fx, fy);
			    }
			    showState();
			}
			building_view.requestFocus();
		    });
            }

        building_view.setOnKeyPressed(e -> {
		if (candidate == null) {
		    e.consume();
		    return;
		}
		switch (e.getCode()) {
                case RIGHT -> {
		    candidate.movePosition(1, 0);
		    showState();
		}
                case LEFT -> {
		    candidate.movePosition(-1, 0);
		    showState();
		}
                case UP -> {
		    candidate.movePosition(0, 1);
		    showState();
		}
                case DOWN -> {
		    candidate.movePosition(0, -1);
		    showState();
		}
                case SPACE, R -> {
		    candidate.rotateClockwise();
                    showState();
                }
                case DIGIT0, NUMPAD0 -> { candidate.setNoBrick(); showState(); }
                case DIGIT1, NUMPAD1 -> { candidate.setBrick(0); showState(); }
                case DIGIT2, NUMPAD2 -> { candidate.setBrick(1); showState(); }
                case DIGIT3, NUMPAD3 -> { candidate.setBrick(2); showState(); }
                case DIGIT4, NUMPAD4 -> { candidate.setBrick(3); showState(); }
                case DIGIT5, NUMPAD5 -> { candidate.setBrick(4); showState(); }
		}
		e.consume();
	    });

        left.getChildren().add(player_pane);
        VBox.setVgrow(player_pane, Priority.ALWAYS);
        this.setCenter(left);
        BorderPane.setAlignment(left, Pos.CENTER);
        BorderPane.setMargin(left, new Insets(4, 4, 4, 4));

        VBox right = new VBox();
        right.setSpacing(4);
        right.setFillWidth(true);
        library_view = new LibraryView();
        library_view.setPrefWidth(400);
        library_view.setPrefHeight(200);
        library_view.setBorder(boxBorder);
        library_view.setPadding(new Insets(2, 2, 2, 2));
	library_view.setOnSelectionChanged((tile) -> {
		if (tile != null) {
		    candidate = new Placement(tile, library_view.getItemSize(tile), 0, 0, 0);
		    if (tile.equals("S1O")) candidate.setNoBrick();
		    candidate_index = player_selector.getSelectionModel().getSelectedIndex();
		}
		else {
		    candidate = null;
		}
		if (onTileSelected != null)
		    onTileSelected.accept(tile);
		showState();
	    });
        right.getChildren().add(library_view);
        price_view = new FlowPane();
        price_view.setAlignment(Pos.CENTER_LEFT);
        price_view.setBorder(boxBorder);
        price_view.setPadding(new Insets(2, 2, 2, 2));
        price_view.setMinHeight(40);
        // right.getChildren().add(price_view);
        dice_view = new DiceView(5);
        dice_view.setBorder(boxBorder);
        dice_view.setPadding(new Insets(2, 2, 2, 2));
        right.getChildren().add(dice_view);
        control_view = new StackPane();
        control_view.setBorder(boxBorder);
        control_view.setPadding(new Insets(2, 2, 2, 2));
        control_view.setAlignment(Pos.CENTER);
        right.getChildren().add(control_view);
        VBox.setVgrow(control_view, Priority.ALWAYS);
        BorderPane.setAlignment(right, Pos.CENTER_RIGHT);
        BorderPane.setMargin(right, new Insets(4, 4, 4, 4));
        this.setRight(right);
    }

    //////////////////////////////////////////////////////////////////////
    // public interface

    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 700;
    public static final int MAX_N_PLAYERS = 4;
    
    public GameGUI() {
	super(); // BorderPane no-arg constructor
	makeMainLayout();
        makeSetupControls();
        makePlayerControls();
        control_view.getChildren().add(game_setup_controls);
        showState();
    }


    /**
     * Set text in the message field at the top.
     */
    public void setMessage(String msg) {
	current_player_view.setText(msg);
    }

    /**
     * Set list of tiles to be shown in the tile "library" (top right).
     * This will also clear any current selection.
     */
    public void setAvailableTiles(List<String> tiles) {
	candidate = null;
	library_view.clearSelection();
	library_view.show(tiles);
    }

    /**
     * Clear selected tile. This does not change which tiles are shown
     * in the tile library, but will unselect the currently selected
     * tile, if any. This also means the selected tile will disappear
     * from the building view.
     */
    public void clearTileSelection() {
	candidate = null;
	library_view.clearSelection();
    }

    /**
     * Set list of dice (colours) to be shown in the dice view.
     * This will also clear any current dice selection.
     */
    public void setAvailableDice(List<String> colours) {
	dice_view.selectors().clearSelection();
	dice_view.show(colours);
    }

    /**
     * Clear dice selection. This does not change which dice are shown
     * in the dice view, but will unselect any currently selected dice.
     */
    public void clearDiceSelection() {
	dice_view.selectors().clearSelection();
    }

    /**
     * Get the currently selected dice.
     * @return a list of indices of the currently selected dice.
     */
    public List<Integer> getSelectedDice() {
	return dice_view.selectors().getSelection();
    }

    /**
     * Set the square at (x,y) in the specified player's building
     * facade to show the specified colour and window
     *
     * Use colour = "White" and window = false to make a square empty.
     * @param player The player whose building should be updated
     *        (0 to number of players - 1).
     * @param x The x position (column) of the square (0-4)
     * @param y The y position (row) of the square (0-8)
     * @param colour The colour to show. Must be one of the strings
     *        "Red", "Blue", "Purple", "Green", "Yellow", "Gray", or
     *        "White". The colour can be abbreviated to the initial
     *        letter only.
     * @param window true iff the square should show a window, false
     *        if it should not.
     */
    public void setFacadeSquare(int player, int x, int y, String colour, boolean window) {
	Colour c = Colour.getColour(colour);
	building_view.setSquare(player, x, y, c, window);
    }

    /**
     * Toggle display status of the coat-of-arms next to a row.
     * @param player The player whose building should be updated
     *        (0 to number of players - 1).
     * @param y The row index. Note that this must be one of 1, 3 or 5.
     * @param highlightOn Whether the CoA should be highlighted (shown
     *        in gold colour) or not (shown in black).
     */
    public void setRowCoA(int player, int y, boolean highlightOn) {
	building_view.setRowCoA(player, y, highlightOn);
    }

    /**
     * Toggle display status of the coat-of-arms on top of a column
     * @param player The player whose building should be updated
     *        (0 to number of players - 1).
     * @param y The row index. Note that this must be one of 1 or 3.
     * @param highlightOn Whether the CoA should be highlighted (shown
     *        in gold colour) or not (shown in black).
     */
    public void setColumnCoA(int player, int x, boolean highlightOn) {
	building_view.setColumnCoA(player, x, highlightOn);
    }

    /**
     * Set the score shown for one of the players.
     */
    public void setScore(int player, int score) {
        player_view.setScore(player, score);
    }

    /**
     * Set/update the information to be shown for one of a player's
     * ability tracks.
     * @param player The player whose ability track should be updated
     *        (0 to number of players - 1).
     * @param colour The colour of the track. Must be one of "Red",
     *        "Blue", "Purple", "Green" or "Yellow".
     * @param nMarked Number to be shown in the "X" column.
     * @param nBonusAvailable Number to show in the "Avail/+" column.
     * @param nAbilityAvailable Number to show in the "Avail/star" column.
     * @param nBonusToNext Number to show in the "Next/+" column.
     * @param nAbilityToNext Number to show in the "Next/star" column.
     */
    public void setTrackInfo(int player, String colour, int nMarked,
			     int nBonusAvailable, int nAbilityAvailable,
			     int nBonusToNext, int nAbilityToNext) {
	player_view.setTrackInfo(player, colour, nMarked,
				 nBonusAvailable, nAbilityAvailable,
				 nBonusToNext, nAbilityToNext);
    }

    /**
     * Clear track selection.
     */
    public void clearTrackSelection() {
	player_view.getTrackSelectors().clearSelection();
    }

    /**
     * Get the currently selected ability track(s).
     * @return a list of indices of the currently selected track(s).
     */
    public List<Integer> getSelectedTracks() {
	return player_view.getTrackSelectors().getSelection();
    }

    /**
     * End the current game.
     * This will bring up the end of game screen (in the lower right
     * corner), which shows the final scores and offers the choice to
     * quit or play again.
     */
    public void endGame(int[] finalScores) {
	Pane gameOverControls = makeGameOverControls(finalScores);
	control_view.getChildren().clear();
	control_view.getChildren().add(gameOverControls);
    }

    /**
     * Returns the index of the player whose score sheet is currently
     * being shown in the left part of the GUI.
     */
    public int getSelectedPlayer() {
	int i = player_selector.getSelectionModel().getSelectedIndex();
	return i;
    }

    /**
     * Set the player whose score sheet should be shown in the left part
     * of the GUI.
     * @param player The player to display (0 to number of players - 1).
     */
    public void setSelectedPlayer(int player) {
        player_selector.getSelectionModel().select(player);
    }

    /**
     * Set the list of actions to appear in the "Action..." menu.
     */
    public void setAvailableActions(List<String> actions) {
	b_colour_change.getItems().clear();
	for (String s : actions) {
            MenuItem act = new MenuItem(s);
	    act.setOnAction((e) -> {
		    if (onGameAction != null)
			onGameAction.accept(act.getText());
		    showState();
		});
            b_colour_change.getItems().add(act);
        }
    }

    /**
     * Update the text on the Confirm and Pass buttons to indicate that
     * the current decision belongs to player i.
     */
    public void setControlPlayer(int i) {
	b_confirm.setText("Confirm (player " + Integer.toString(i) + ")");
	b_pass.setText("Pass (player " + Integer.toString(i) + ")");
    }

    // register event callbacks

    /**
     * Set the event handler to be called when a new game is started.
     * The handler will receive two arguments: the number of players
     * (an integer) and an array of boolean values, of the same length
     * as the number of players, indicating which players should be AI
     * controlled. (Of course, if your game does not have an AI, you
     * can ignore the second argument.)
     */
    public void setOnStartGame(BiConsumer<Integer, boolean[]> handler) {
	onStartGame = handler;
    }

    /**
     * Set the event handler to be called when the user selects a tile
     * from the "tile library" (on the top-right in the display). The
     * handler will receive one argument, which is the name of the
     * tile.
     *
     * The selected tile will be displayed as a "candidate" (outline)
     * on the building display of the currently selected player's
     * score sheet.
     */
    public void setOnTileSelected(Consumer<String> handler) {
	onTileSelected = handler;
    }

    /**
     * Set the event handler to be called when the user confirms
     * placement of a selected tile.
     * The handler will receive one argument, which is an object of
     * type `Placement` that contains all the details of the intended
     * placement.
     */
    public void setOnTilePlaced(Consumer<Placement> handler) {
	onTilePlaced = handler;
    }

    /**
     * Set the event handler to be called when the user changes the
     * selection of any die in the dice diplay. The event handler will
     * receive one argument, which is the index of of the die whose
     * selection status has changed.
     * This event only informs the handler that a dies selection has
     * changed, not whether the die is now selected or unselected. You
     * can use the `getSelectedDice()` method to get the indices of
     * currently selected dice.
     */
    public void setOnDiceSelectionChanged(IntConsumer handler) {
	dice_view.selectors().setOnSelectionChanged((i) -> {
		handler.accept(i);
		showState();
	    });
    }

    /**
     * Set the event handler to be called when the user changes the
     * selection (checkbox) of any of the ability tracks. The event
     * handler will receive one argument, which is the index (0-4)
     * of the track whose selection status has changed.
     * This event only informs the handler that a selection has
     * changed, not whether the track is now selected or unselected.
     * You can use the `getSelectedTracks()` method to get the indices
     * of currently selected tracks.
     */
    public void setOnTrackSelectionChanged(IntConsumer handler) {
	player_view.getTrackSelectors().setOnSelectionChanged((i) -> {
		handler.accept(i);
		showState();
	    });
    }

    /**
     * Set the event handler to be called when the "Confirm" button is
     * pressed in any situation except when it is pressed to confirm a
     * tile placement (this will generate a TilePlaced event instead).
     * The event handler will receive one argument, which is the
     * current label of the button.
     */
    public void setOnConfirm(Consumer<String> handler) {
	onConfirm = handler;
    }

    /**
     * Set the event handler to be called when the "Pass" button is
     * pressed. The event handler will receive one argument, which is
     * the current label of the button.
     */
    public void setOnPass(Consumer<String> handler) {
	onPass = handler;
    }

    /**
     * Set the event handler to be called when an item from the "Action"
     * menu button is selected. The event handler will receive one
     * argument, which is the label of the menu item.
     */
    public void setOnGameAction(Consumer<String> handler) {
	onGameAction = handler;
    }

    // /**
    //  * Set the event handler to be called when the "Reroll" button is
    //  * pressed. The event handler will receive one argument, which is
    //  * the current label of the button.
    //  */
    // public void setOnReroll(Consumer<String> handler) {
    // 	onReroll = handler;
    // }

    // /**
    //  * Set the event handler to be called when an item from the "Change"
    //  * menu button is select The event handler will receive one argument,
    //  * which is the label of the item (i.e., one of "to Red", "to Blue",
    //  * etc)
    //  */
    // public void setOnColourChange(Consumer<String> handler) {
    // 	onColourChange = handler;
    // }

}
