/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package StarVault.galaxymemory;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.net.URL;

import java.io.IOException;
import java.util.HashMap;

/**
 * Manages the switching of screens/scenes in the application.
 */
public class ScreenController {
    private final Stage stage;
    private final HashMap<String, Scene> scenes = new HashMap<>();
    private final HashMap<String, Object> controllers = new HashMap<>();

    public static final String GALAXY_MAP = "GALAXY_MAP";
    public static final String STAR_EDITOR = "STAR_EDITOR";

    public ScreenController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Loads the screen from FXML and caches the scene and controller.
     */
public void loadScreen(String name, String fxmlFile) {
    try {
        // Try to find the file relative to THIS class
        URL fxmlLocation = getClass().getResource(fxmlFile);
        
        if (fxmlLocation == null) {
            throw new IOException("Cannot find FXML file at: " + fxmlFile);
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();
        
        // Ensure CSS is loaded similarly
        URL cssLocation = getClass().getResource("style.css");
        if (cssLocation != null) {
            Scene scene = new Scene(root);
            scene.getStylesheets().add(cssLocation.toExternalForm());
            scenes.put(name, scene);
        } else {
            scenes.put(name, new Scene(root));
        }
        
        controllers.put(name, loader.getController());
    } catch (IOException e) {
        System.err.println(e.getMessage());
    }
}

    /**
     * Swaps the currently displayed scene.
     */
    public void activate(String name) {
        if (scenes.containsKey(name)) {
            stage.setScene(scenes.get(name));
            
            // Inform controllers of a switch if they need to refresh
            Object c = controllers.get(name);
            if (c instanceof GalaxyMapController) {
                ((GalaxyMapController) c).refreshView();
            } else if (c instanceof StarEditorController) {
                ((StarEditorController) c).prepareEditor();
            }
        }
    }

    public Object getController(String name) {
        return controllers.get(name);
    }
}