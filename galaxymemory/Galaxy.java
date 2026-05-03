/* 

 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license 

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template 

 */ 

package StarVault.galaxymemory; 

 

import javafx.beans.property.DoubleProperty; 

import javafx.beans.property.SimpleDoubleProperty; 

import javafx.beans.property.SimpleStringProperty; 

import javafx.beans.property.StringProperty; 

import javafx.collections.FXCollections; 

import javafx.collections.ObservableList; 

 

public class Galaxy { 

    private final StringProperty name = new SimpleStringProperty("New Galaxy"); 

     

    // Developer can put image path or shape type here 

    private final StringProperty appearanceResource = new SimpleStringProperty(""); 

     

    // Position on the main map 

    private final DoubleProperty posX = new SimpleDoubleProperty(300.0); 

    private final DoubleProperty posY = new SimpleDoubleProperty(300.0); 

     

    // Stars within this galaxy 

    private final ObservableList<Star> stars = FXCollections.observableArrayList(); 

 

    public Galaxy(String name, String appearanceResource) { 

        setName(name); 

        setAppearanceResource(appearanceResource); 

    } 

 

    // Getters/Setters for properties 

    public String getName() { return name.get(); } 

    public void setName(String name) { this.name.set(name); } 

    public StringProperty nameProperty() { return name; } 

 

    public String getAppearanceResource() { return appearanceResource.get(); } 

    public void setAppearanceResource(String appearanceResource) { this.appearanceResource.set(appearanceResource); } 

    public StringProperty appearanceResourceProperty() { return appearanceResource; } 

 

    public double getPosX() { return posX.get(); } 

    public void setPosX(double posX) { this.posX.set(posX); } 

    public DoubleProperty posXProperty() { return posX; } 

 

    public double getPosY() { return posY.get(); } 

    public void setPosY(double posY) { this.posY.set(posY); } 

    public DoubleProperty posYProperty() { return posY; } 

 

    public ObservableList<Star> getStars() { return stars; } 

     

    public void addStar(Star star) { 

        stars.add(star); 

    } 

} 