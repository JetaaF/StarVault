
/* 

 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license 

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template 

 */ 

package StarVault.galaxymemory; 

 

import javafx.application.Application; 

import javafx.collections.FXCollections; 

import javafx.collections.ObservableList; 

import javafx.stage.Stage; 

import java.util.ArrayList; 

import java.util.List; 

 

public class App extends Application { 

 

    public static ObservableList<Galaxy> allGalaxies = FXCollections.observableArrayList(); 

    public static Galaxy currentGalaxyContext = null; 

    public static Star currentStarContext = null; 

    public static ScreenController screenController; 

     

    // Developer: Add your images to the src/galaxymemory/images/ folder 

    public static final List<String> GALAXY_ICONS = new ArrayList<>(); 

    public static final List<String> STAR_ICONS = new ArrayList<>(); 

 

    @Override 

    public void start(Stage stage) throws Exception { 

        stage.setTitle("StarVault"); 

         

        // Built-in shapes (always available). Add classpath images under src/StarVault/galaxymemory/images/ and list them here. 

        GALAXY_ICONS.add(MapGraphics.SHAPE_GALAXY);

        GALAXY_ICONS.add("/StarVault/galaxymemory/images/galaxy1.jpg");

        GALAXY_ICONS.add("/StarVault/galaxymemory/images/galaxy2.jpg");

        GALAXY_ICONS.add("/StarVault/galaxymemory/images/galaxy3.png");

        GALAXY_ICONS.add("/StarVault/galaxymemory/images/galaxy4.png");

        GALAXY_ICONS.add("/StarVault/galaxymemory/images/galaxy5.png");

        STAR_ICONS.add(MapGraphics.SHAPE_STAR);

        STAR_ICONS.add("/StarVault/galaxymemory/images/star2.png");

        STAR_ICONS.add("/StarVault/galaxymemory/images/star3.png");

        STAR_ICONS.add("/StarVault/galaxymemory/images/star4.png");

 

        screenController = new ScreenController(stage); 

 

        screenController.loadScreen(ScreenController.GALAXY_MAP, "/StarVault/galaxymemory/galaxy_map.fxml"); 

        screenController.loadScreen(ScreenController.STAR_EDITOR, "/StarVault/galaxymemory/star_editor.fxml"); 

 

        screenController.activate(ScreenController.GALAXY_MAP); 

        stage.show(); 

    } 

 

    public static void main(String[] args) { 

        launch(args); 

    } 

} 
