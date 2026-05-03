package StarVault.galaxymemory;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Star {
    private final StringProperty title = new SimpleStringProperty("New Star");
    private final StringProperty memory = new SimpleStringProperty("");

    private final StringProperty appearanceResource = new SimpleStringProperty("");

    private final DoubleProperty posX = new SimpleDoubleProperty(100.0);
    private final DoubleProperty posY = new SimpleDoubleProperty(100.0);

    private final List<String> photoPaths = new ArrayList<>();

    public Star() {}

    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    public String getMemory() { return memory.get(); }
    public void setMemory(String memory) { this.memory.set(memory); }
    public StringProperty memoryProperty() { return memory; }

    public String getAppearanceResource() { return appearanceResource.get(); }
    public void setAppearanceResource(String appearanceResource) { this.appearanceResource.set(appearanceResource); }
    public StringProperty appearanceResourceProperty() { return appearanceResource; }

    public double getPosX() { return posX.get(); }
    public void setPosX(double posX) { this.posX.set(posX); }
    public DoubleProperty posXProperty() { return posX; }

    public double getPosY() { return posY.get(); }
    public void setPosY(double posY) { this.posY.set(posY); }
    public DoubleProperty posYProperty() { return posY; }

    public List<String> getPhotoPaths() { return photoPaths; }
    public void addPhotoPath(String path) { photoPaths.add(path); }
    public void removePhotoPath(String path) { photoPaths.remove(path); }

    public Color getColorBasedOnContent() {
        int wordCount = getWordCount();
        if (wordCount == 0) return Color.CYAN;

        if (wordCount < 100) return Color.CYAN;
        if (wordCount < 200) return Color.LIGHTBLUE;
        if (wordCount < 300) return Color.WHITESMOKE;
        if (wordCount < 400) return Color.WHITE;

        return Color.WHITE;
    }

    public int getWordCount() {
        if (getMemory() == null || getMemory().trim().isEmpty()) return 0;
        return getMemory().trim().split("\\s+").length;
    }
}