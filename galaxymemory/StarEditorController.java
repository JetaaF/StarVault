package StarVault.galaxymemory;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;

public class StarEditorController {

    @FXML private StackPane starPreviewHolder;
    @FXML private Button closeEditorButton;
    @FXML private Button starPrevButton;
    @FXML private Label starPaginationText;
    @FXML private Button starNextButton;
    @FXML private StackPane starIconContainer;
    @FXML private TextField starTitleField;
    @FXML private TextArea starMemoryText;
    @FXML private Button saveStarButton;
    @FXML private Label wordCountLabel;
    @FXML private Label starPhaseLabel;
    @FXML private Region phaseColorSwatch;

    @FXML private Button addPhotoButton;
    @FXML private HBox photoStrip;
    @FXML private ScrollPane photoScrollPane;


    private int currentIconIndex;

    @FXML
    public void initialize() {
        starPrevButton.setOnAction(e -> changeIcon(-1));
        starNextButton.setOnAction(e -> changeIcon(1));
        saveStarButton.setOnAction(e -> saveAndClose());
        closeEditorButton.setOnAction(e -> closeWithoutSave());

        addPhotoButton.setOnAction(e -> pickPhoto());

        starMemoryText.textProperty().addListener((observable, oldValue, newValue) -> updateLiveStats(newValue));

        photoScrollPane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: rgba(120, 90, 220, 0.35);" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;"
        );

        prepareEditor();
    }

    public void prepareEditor() {
        Star ctx = App.currentStarContext;

        if (ctx == null) return;

        starTitleField.setText(ctx.getTitle());
        starMemoryText.setText(ctx.getMemory());


        String appearance = ctx.getAppearanceResource();
        currentIconIndex = 0;
        if (appearance != null && !appearance.isEmpty()) {
            int idx = App.STAR_ICONS.indexOf(appearance);

            if (idx >= 0) currentIconIndex = idx;
        }

        updateIconPreview();
        updateLiveStats(ctx.getMemory());
        refreshPhotoStrip();
    }

    private void pickPhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Add Photo");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
        );

        File file = chooser.showOpenDialog(addPhotoButton.getScene().getWindow());
        if (file != null && App.currentStarContext != null) {
            App.currentStarContext.addPhotoPath(file.toURI().toString());
            refreshPhotoStrip();
        }
    }

    private void refreshPhotoStrip() {
        photoStrip.getChildren().clear();
        Star ctx = App.currentStarContext;
        if (ctx == null) return;

        for (String path : ctx.getPhotoPaths()) {
            photoStrip.getChildren().add(buildPhotoThumb(path));
        }

        if (ctx.getPhotoPaths().isEmpty()) {
            Label hint = new Label("No photos yet — tap + Add Photo");
            hint.setStyle("-fx-text-fill: #3a4a6a; -fx-font-family: Avenir; -fx-font-size: 12px; -fx-font-style: italic;");
            photoStrip.getChildren().add(hint);
        }
    }

    private StackPane buildPhotoThumb(String uriPath) {
        StackPane container = new StackPane();
        container.setPrefSize(90, 90);
        container.setMinSize(90, 90);
        container.setMaxSize(90, 90);
        container.setStyle(
            "-fx-background-color: #080e22;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-color: rgba(120,90,220,0.45);" +
            "-fx-border-radius: 10px;" +
            "-fx-border-width: 1.5px;"
        );

        try {
            Image img = new Image(uriPath, 90, 90, true, true);
            if (!img.isError()) {
                ImageView iv = new ImageView(img);
                iv.setFitWidth(86);
                iv.setFitHeight(86);
                iv.setPreserveRatio(true);

                Rectangle clip = new Rectangle(86, 86);
                clip.setArcWidth(18);
                clip.setArcHeight(18);
                iv.setClip(clip);

                DropShadow glow = new DropShadow(10, Color.color(0.5, 0.35, 1.0, 0.6));
                iv.setEffect(glow);

                iv.setCursor(javafx.scene.Cursor.HAND);
                iv.setOnMouseClicked(ev -> showPhotoFullscreen(uriPath));

                Button remove = new Button("×");
                remove.setStyle(
                    "-fx-background-color: rgba(20,8,40,0.85);" +
                    "-fx-text-fill: #ffb8c8;" +
                    "-fx-font-size: 11px;" +
                    "-fx-background-radius: 10px;" +
                    "-fx-padding: 0 4 0 4;" +
                    "-fx-cursor: hand;"
                );
                remove.setVisible(false);
                StackPane.setAlignment(remove, javafx.geometry.Pos.TOP_RIGHT);

                container.setOnMouseEntered(e -> remove.setVisible(true));
                container.setOnMouseExited(e -> remove.setVisible(false));

                remove.setOnAction(e -> {
                    if (App.currentStarContext != null) {
                        App.currentStarContext.removePhotoPath(uriPath);
                        refreshPhotoStrip();
                    }
                });

                container.getChildren().addAll(iv, remove);
            }
        } catch (Exception ex) {
            Label err = new Label("?");
            err.setStyle("-fx-text-fill: #546080;");
            container.getChildren().add(err);
        }

        return container;
    }

    private void showPhotoFullscreen(String uriPath) {
        if (addPhotoButton.getScene() == null) return;
        javafx.scene.Parent root = addPhotoButton.getScene().getRoot();
        if (!(root instanceof javafx.scene.layout.AnchorPane)) return;
        javafx.scene.layout.AnchorPane rootPane = (javafx.scene.layout.AnchorPane) root;

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(4,8,28,0.92);");
        javafx.scene.layout.AnchorPane.setTopAnchor(overlay, 0.0);
        javafx.scene.layout.AnchorPane.setBottomAnchor(overlay, 0.0);
        javafx.scene.layout.AnchorPane.setLeftAnchor(overlay, 0.0);
        javafx.scene.layout.AnchorPane.setRightAnchor(overlay, 0.0);

        Image full = new Image(uriPath, 1024, 768, true, true);
        ImageView large = new ImageView(full);
        large.setPreserveRatio(true);
        large.setFitWidth(900);
        large.setFitHeight(660);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
            "-fx-background-color: rgba(20,8,40,0.9);" +
            "-fx-text-fill: #ffb8c8;" +
            "-fx-font-size: 18px;" +
            "-fx-background-radius: 18px;" +
            "-fx-min-width: 36px;" +
            "-fx-min-height: 36px;" +
            "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(ev -> rootPane.getChildren().remove(overlay));
        StackPane.setAlignment(closeBtn, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtn, new javafx.geometry.Insets(20));

        overlay.getChildren().addAll(large, closeBtn);
        overlay.setOnMouseClicked(ev -> {
            if (ev.getTarget() == overlay) rootPane.getChildren().remove(overlay);
        });

        rootPane.getChildren().add(overlay);
    }

    private void changeIcon(int direction) {
        if (App.STAR_ICONS.isEmpty()) return;
        currentIconIndex += direction;
        if (currentIconIndex < 0) currentIconIndex = App.STAR_ICONS.size() - 1;
        if (currentIconIndex >= App.STAR_ICONS.size()) currentIconIndex = 0;

        updateIconPreview();
    }

    private void updateIconPreview() {
        if (App.STAR_ICONS.isEmpty()) {
            starPreviewHolder.getChildren().clear();
            starPaginationText.setText("0/0");
            return;
        }
        String path = App.STAR_ICONS.get(currentIconIndex);
        starPreviewHolder.getChildren().clear();

        if (MapGraphics.isShapeSpec(path)) {
            Node shape = MapGraphics.buildIcon(path, 200, StarEditorController.class);
            starPreviewHolder.getChildren().add(shape);
        } else {
            String res = path.startsWith("/") ? path : "/" + path;
            URL url = StarEditorController.class.getResource(res);
            if (url != null) {
                Image img = new Image(url.toExternalForm(), 240, 240, true, true);
                if (!img.isError()) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(240);
                    iv.setFitHeight(240);
                    iv.setPreserveRatio(true);
                    starPreviewHolder.getChildren().add(iv);
                }
            }
        }
        if (starPreviewHolder.getChildren().isEmpty()) {

            starPreviewHolder.getChildren().add(
                MapGraphics.buildIcon(MapGraphics.SHAPE_STAR, 200, StarEditorController.class)
            );

        }

        starPaginationText.setText((currentIconIndex + 1) + "/" + App.STAR_ICONS.size());
    }

    private void updateLiveStats(String content) {
        int wordCount = 0;
        if (content != null && !content.trim().isEmpty()) {
            wordCount = content.trim().split("\\s+").length;
        }

        wordCountLabel.setText(String.valueOf(wordCount));

        Color targetColor = Color.CYAN;
        String phase = "O (Blue)";


        if (wordCount >= 100) { targetColor = Color.LIGHTBLUE;  phase = "B (Blue-white)"; }
        if (wordCount >= 200) { targetColor = Color.WHITESMOKE; phase = "A (White)"; }
        if (wordCount >= 300) { targetColor = Color.WHITE;      phase = "F (Yellow-white)"; }
        if (wordCount >= 400) { targetColor = Color.WHITE;      phase = "White-hot"; }

        starPhaseLabel.setText(phase);

        phaseColorSwatch.setStyle("-fx-background-color: " + toRGB(targetColor) + ";");
    }

    private void saveAndClose() {
        if (App.currentStarContext == null) {
            App.screenController.activate(ScreenController.GALAXY_MAP);
            return;
        }

        App.currentStarContext.setTitle(starTitleField.getText());
        App.currentStarContext.setMemory(starMemoryText.getText());

        if (!App.STAR_ICONS.isEmpty()) {
            App.currentStarContext.setAppearanceResource(App.STAR_ICONS.get(currentIconIndex));
        }

        App.currentStarContext = null;
        App.screenController.activate(ScreenController.GALAXY_MAP);
    }

    private void closeWithoutSave() {
        App.currentStarContext = null;
        App.screenController.activate(ScreenController.GALAXY_MAP);
    }

    private String toRGB(Color c) {

        return "rgb(" + (int)(c.getRed() * 255) + "," + (int)(c.getGreen() * 255) + "," + (int)(c.getBlue() * 255) + ")";
    }
}

