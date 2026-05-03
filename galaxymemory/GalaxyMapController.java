package StarVault.galaxymemory;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

import java.util.Random;


public class GalaxyMapController {

    @FXML private Button backButton;
    @FXML private Button createGalaxyButton;
    @FXML private Label contextLabel;
    @FXML private Button newMemoryButton;
    @FXML private AnchorPane spaceAnchorPane;
    @FXML private AnchorPane contentLayer;
    @FXML private HBox galaxyFooterBar;
    @FXML private Label galaxyFooterLabel;
    @FXML private StackPane newGalaxyOverlay;

    @FXML private TextField searchField;
    @FXML private VBox searchResultsBox;

    private boolean isInsideGalaxy;
    private Canvas starCanvas;

    // Star positions as fractions (0.0-1.0) so they scale with window
    private double[] starX;
    private double[] starY;
    private double[] starSize;
    private double[] starAlpha;
    private static final int STAR_COUNT = 220;


    private TextField newGalaxyNameField;
    private StackPane newGalaxyPreviewInner;
    private Label newGalaxyPageLabel;
    private int newGalaxyIconIndex;

    @FXML
    public void initialize() {
        createGalaxyButton.setOnAction(e -> {

            if (!isInsideGalaxy) showNewGalaxyOverlay();

        });

        backButton.setOnAction(event -> {
            isInsideGalaxy = false;
            App.currentGalaxyContext = null;
            refreshView();
        });

        newMemoryButton.setOnAction(event -> createNewStar());
        buildNewGalaxyOverlayContent();

        setupSearch();
        setupStarCanvas();
        refreshView();
    }

    // ── Star Canvas ───────────────────────────────────────────

    private void setupStarCanvas() {
        // Pre-generate star positions as fractions
        Random rand = new Random(42);
        starX = new double[STAR_COUNT];
        starY = new double[STAR_COUNT];
        starSize = new double[STAR_COUNT];
        starAlpha = new double[STAR_COUNT];
        for (int i = 0; i < STAR_COUNT; i++) {
            starX[i] = rand.nextDouble();
            starY[i] = rand.nextDouble();
            starSize[i] = rand.nextDouble() * 1.6 + 0.4;
            starAlpha[i] = 0.35 + rand.nextDouble() * 0.65;
        }

        starCanvas = new Canvas();
        spaceAnchorPane.getChildren().add(0, starCanvas);
        AnchorPane.setTopAnchor(starCanvas, 0.0);
        AnchorPane.setBottomAnchor(starCanvas, 0.0);
        AnchorPane.setLeftAnchor(starCanvas, 0.0);
        AnchorPane.setRightAnchor(starCanvas, 0.0);

        // Redraw stars whenever the pane resizes
        spaceAnchorPane.widthProperty().addListener((obs, o, n) -> {
            starCanvas.setWidth(n.doubleValue());
            drawStars();
        });
        spaceAnchorPane.heightProperty().addListener((obs, o, n) -> {
            starCanvas.setHeight(n.doubleValue());
            drawStars();
        });
    }

    private void drawStars() {
        double w = starCanvas.getWidth();
        double h = starCanvas.getHeight();
        if (w <= 0 || h <= 0) return;

        GraphicsContext gc = starCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        for (int i = 0; i < STAR_COUNT; i++) {
            double x = starX[i] * w;
            double y = starY[i] * h;
            double r = starSize[i];
            double a = starAlpha[i];

            // Larger stars get a soft outer glow
            if (r > 1.4) {
                gc.setFill(Color.color(0.7, 0.8, 1.0, a * 0.25));
                gc.fillOval(x - r * 2.5, y - r * 2.5, r * 5, r * 5);
            }

            gc.setFill(Color.color(0.85, 0.9, 1.0, a));
            gc.fillOval(x - r, y - r, r * 2, r * 2);
        }
    }

    // ── Search ────────────────────────────────────────────────

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal == null ? "" : newVal.trim().toLowerCase();
            if (query.isEmpty()) {
                hideSearchResults();
            } else {
                showSearchResults(query);
            }
        });

        spaceAnchorPane.setOnMouseClicked(e -> {
            if (!searchResultsBox.isHover()) {
                hideSearchResults();
            }
        });
    }

    private void showSearchResults(String query) {
        searchResultsBox.getChildren().clear();
        int count = 0;

        for (Galaxy galaxy : App.allGalaxies) {
            for (Star star : galaxy.getStars()) {
                boolean titleMatch = star.getTitle().toLowerCase().contains(query);
                boolean memoryMatch = star.getMemory().toLowerCase().contains(query);
                if (titleMatch || memoryMatch) {
                    searchResultsBox.getChildren().add(buildResultRow(star, galaxy));
                    count++;
                }
            }
        }

        if (count == 0) {
            Label none = new Label("No results for: " + query);
            none.setStyle(
                "-fx-text-fill: #3a4a6a;" +
                "-fx-font-family: Avenir;" +
                "-fx-font-style: italic;" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 8px 12px;"
            );
            searchResultsBox.getChildren().add(none);
        }

        searchResultsBox.setVisible(true);
        searchResultsBox.setManaged(true);
        searchResultsBox.toFront();
    }

    private Node buildResultRow(Star star, Galaxy galaxy) {
        VBox row = new VBox(2);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color: transparent; -fx-background-radius: 8px; -fx-cursor: hand;");

        Label starName = new Label("✦  " + star.getTitle());
        starName.setStyle("-fx-text-fill: #c8d8ff; -fx-font-family: Avenir; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label galaxyName = new Label("    in " + galaxy.getName());
        galaxyName.setStyle("-fx-text-fill: #546080; -fx-font-family: Avenir; -fx-font-size: 11px; -fx-font-style: italic;");

        row.getChildren().addAll(starName, galaxyName);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(100,70,220,0.2); -fx-background-radius: 8px; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent; -fx-background-radius: 8px; -fx-cursor: hand;"));

        row.setOnMouseClicked(e -> {
            hideSearchResults();
            searchField.clear();
            App.currentGalaxyContext = galaxy;
            isInsideGalaxy = true;
            refreshView();
            App.currentStarContext = star;
            App.screenController.activate(ScreenController.STAR_EDITOR);
        });

        return row;
    }

    private void hideSearchResults() {
        searchResultsBox.setVisible(false);
        searchResultsBox.setManaged(false);
        searchResultsBox.getChildren().clear();
    }

    // ── Galaxy overlay ────────────────────────────────────────


    private void buildNewGalaxyOverlayContent() {
        Region veil = new Region();
        veil.setMaxWidth(Double.MAX_VALUE);
        veil.setMaxHeight(Double.MAX_VALUE);
        veil.setStyle("-fx-background-color: rgba(0,0,0,0.38);");
        veil.setPickOnBounds(true);

        VBox card = new VBox(14);
        card.setPadding(new Insets(22));
        card.setMaxWidth(360);
        card.getStyleClass().add("new-galaxy-dialog-card");
        card.setEffect(new DropShadow(24, Color.color(0, 0, 0, 0.25)));

        Label heading = new Label("New galaxy");
        heading.getStyleClass().add("title-label");

        Label nameCap = new Label("Galaxy name");
        nameCap.getStyleClass().add("text-label");
        newGalaxyNameField = new TextField("New Galaxy");
        newGalaxyNameField.getStyleClass().add("accent-input");

        Label lookCap = new Label("Appearance");
        lookCap.getStyleClass().add("text-label");

        StackPane previewShell = new StackPane();
        previewShell.getStyleClass().add("galaxy-dialog-preview");
        newGalaxyPreviewInner = new StackPane();
        previewShell.getChildren().add(newGalaxyPreviewInner);

        newGalaxyPageLabel = new Label();
        newGalaxyPageLabel.getStyleClass().add("text-label");

        Button prevBtn = new Button("<");
        prevBtn.getStyleClass().add("accent-button");
        Button nextBtn = new Button(">");
        nextBtn.getStyleClass().add("accent-button");


        prevBtn.setOnAction(e -> {
            List<String> paths = App.GALAXY_ICONS;
            if (paths.isEmpty()) return;

            newGalaxyIconIndex = (newGalaxyIconIndex - 1 + paths.size()) % paths.size();
            refreshNewGalaxyPreview();
        });
        nextBtn.setOnAction(e -> {
            List<String> paths = App.GALAXY_ICONS;

            if (paths.isEmpty()) return;

            newGalaxyIconIndex = (newGalaxyIconIndex + 1) % paths.size();
            refreshNewGalaxyPreview();
        });

        HBox carousel = new HBox(14);
        carousel.setAlignment(Pos.CENTER);
        carousel.getChildren().addAll(prevBtn, newGalaxyPageLabel, nextBtn);

        Button addButton = new Button("Add");
        addButton.getStyleClass().add("accent-button");

        HBox addRow = new HBox();
        addRow.setAlignment(Pos.CENTER_RIGHT);
        addRow.getChildren().add(addButton);


        card.getChildren().addAll(heading, nameCap, newGalaxyNameField, lookCap, previewShell, carousel, addRow);
        VBox.setMargin(previewShell, new Insets(4, 0, 4, 0));

        veil.setOnMouseClicked(ev -> { if (ev.getTarget() == veil) hideNewGalaxyOverlay(); });

        addButton.setOnAction(e -> {
            List<String> paths = App.GALAXY_ICONS;
            if (paths.isEmpty()) { hideNewGalaxyOverlay(); return; }

            String fullPath = paths.get(newGalaxyIconIndex);
            String name = newGalaxyNameField.getText().trim();
            Galaxy created = new Galaxy(name.isEmpty() ? "New Galaxy" : name, fullPath);
            App.allGalaxies.add(created);
            hideNewGalaxyOverlay();
            refreshView();
        });

        card.setPickOnBounds(true);
        card.setOnMouseClicked(ev -> ev.consume());

        newGalaxyOverlay.getChildren().setAll(veil, card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    private void refreshNewGalaxyPreview() {
        newGalaxyPreviewInner.getChildren().clear();
        List<String> paths = App.GALAXY_ICONS;

        if (paths.isEmpty()) { newGalaxyPageLabel.setText("0/0"); return; }
        if (newGalaxyIconIndex < 0) newGalaxyIconIndex = 0;
        if (newGalaxyIconIndex >= paths.size()) newGalaxyIconIndex = paths.size() - 1;
        Node graphic = MapGraphics.buildIcon(paths.get(newGalaxyIconIndex), 170, GalaxyMapController.class);

        newGalaxyPreviewInner.getChildren().add(graphic);
        newGalaxyPageLabel.setText((newGalaxyIconIndex + 1) + "/" + paths.size());
    }

    private void showNewGalaxyOverlay() {
        newGalaxyNameField.setText("New Galaxy");
        newGalaxyIconIndex = 0;
        refreshNewGalaxyPreview();
        newGalaxyOverlay.setVisible(true);
        newGalaxyOverlay.setManaged(true);
        newGalaxyOverlay.toFront();
    }

    private void hideNewGalaxyOverlay() {
        newGalaxyOverlay.setVisible(false);
        newGalaxyOverlay.setManaged(false);
    }


    public void refreshView() {
        hideNewGalaxyOverlay();
        contentLayer.getChildren().clear();

        if (!isInsideGalaxy) {
            createGalaxyButton.setVisible(true);
            createGalaxyButton.setManaged(true);
            contextLabel.setVisible(false);
            contextLabel.setManaged(false);
            backButton.setVisible(false);
            newMemoryButton.setVisible(false);
            galaxyFooterBar.setVisible(false);

            for (Galaxy g : App.allGalaxies) {
                contentLayer.getChildren().add(createGalaxyNode(g));
            }
        } else {
            createGalaxyButton.setVisible(false);
            createGalaxyButton.setManaged(false);
            contextLabel.setVisible(true);
            contextLabel.setManaged(true);
            contextLabel.setText("Galaxy map");
            backButton.setVisible(true);
            newMemoryButton.setVisible(true);
            galaxyFooterBar.setVisible(true);
            if (App.currentGalaxyContext != null) {
                galaxyFooterLabel.setText("Galaxy: " + App.currentGalaxyContext.getName());
            }

            if (App.currentGalaxyContext != null) {
                for (Star s : App.currentGalaxyContext.getStars()) {
                    contentLayer.getChildren().add(createStarNode(s));
                }
            }
        }
    }

    private Node createGalaxyNode(Galaxy galaxy) {
        VBox vbox = new VBox(8);
        vbox.setAlignment(Pos.CENTER);
        vbox.getStyleClass().add("draggable-content-label");

        Node icon = MapGraphics.buildIcon(galaxy.getAppearanceResource(), 72, GalaxyMapController.class);
        vbox.getChildren().add(icon);

        Label label = new Label(galaxy.getName());
        label.getStyleClass().add("galaxy-name-label");
        vbox.getChildren().add(label);

        vbox.setLayoutX(galaxy.getPosX());
        vbox.setLayoutY(galaxy.getPosY());

        DraggableNode.makeDraggable(vbox);

        vbox.setOnMouseReleased(e -> {
            galaxy.setPosX(vbox.getLayoutX());
            galaxy.setPosY(vbox.getLayoutY());
        });

        vbox.setOnMouseClicked(mouseEvent -> {

            if (DraggableNode.wasDragged(vbox)) return;

            if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY) {
                App.currentGalaxyContext = galaxy;
                isInsideGalaxy = true;
                refreshView();
            }
        });

        return vbox;
    }

    private Node createStarNode(Star star) {
        VBox vbox = new VBox(6);
        vbox.setAlignment(Pos.CENTER);
        vbox.getStyleClass().add("draggable-content-label");

        Node icon = MapGraphics.buildIcon(star.getAppearanceResource(), 56, GalaxyMapController.class);
        Color phase = star.getColorBasedOnContent();
        DropShadow glow = new DropShadow(18, phase);
        glow.setSpread(0.35);
        icon.setEffect(glow);

        StackPane wrap = new StackPane(icon);
        vbox.getChildren().add(wrap);

        Label label = new Label(star.getTitle());
        label.getStyleClass().add("star-title-label");
        vbox.getChildren().add(label);

        vbox.setLayoutX(star.getPosX());
        vbox.setLayoutY(star.getPosY());

        DraggableNode.makeDraggable(vbox);

        vbox.setOnMouseReleased(e -> {
            star.setPosX(vbox.getLayoutX());
            star.setPosY(vbox.getLayoutY());
        });

        vbox.setOnMouseClicked(mouseEvent -> {

            if (DraggableNode.wasDragged(vbox)) return;
            if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY) {

                App.currentStarContext = star;
                App.screenController.activate(ScreenController.STAR_EDITOR);
            }
        });

        return vbox;
    }

    private void createNewStar() {

        if (App.currentGalaxyContext == null) return;
        Star newStar = new Star();
        if (!App.STAR_ICONS.isEmpty()) newStar.setAppearanceResource(App.STAR_ICONS.get(0));
        App.currentGalaxyContext.addStar(newStar);
        App.currentStarContext = newStar;

        double w = contentLayer.getWidth() > 0 ? contentLayer.getWidth() : 800;
        double h = contentLayer.getHeight() > 0 ? contentLayer.getHeight() : 600;
        newStar.setPosX(w / 2 - 28);
        newStar.setPosY(h / 2 - 28);

        App.screenController.activate(ScreenController.STAR_EDITOR);
    }
}

