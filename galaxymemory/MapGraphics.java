package StarVault.galaxymemory; 

 

import javafx.scene.Node; 

import javafx.scene.image.Image; 

import javafx.scene.image.ImageView; 

import javafx.scene.layout.StackPane; 

import javafx.scene.paint.Color; 

import javafx.scene.shape.Circle; 

import javafx.scene.shape.Polygon; 

 

import java.net.URL; 

 

/** 

 * Icons for the galaxy map. Add classpath image paths to {@link App#GALAXY_ICONS} / {@link App#STAR_ICONS}, 

 * or use {@code shape:…} keys so the app works without image files. 

 */ 

public final class MapGraphics { 

 

    public static final String PREFIX_SHAPE = "shape:"; 

    /** Built-in soft “galaxy blob” (white + orange outline). */ 

    public static final String SHAPE_GALAXY = PREFIX_SHAPE + "galaxy"; 

    /** Built-in five-point style star (white fill). */ 

    public static final String SHAPE_STAR = PREFIX_SHAPE + "star"; 

 

    private MapGraphics() {} 

 

    public static boolean isShapeSpec(String appearance) { 

        return appearance != null && appearance.startsWith(PREFIX_SHAPE); 

    } 

 

    /** 

     * @param classpathResource e.g. {@code /StarVault/galaxymemory/images/galaxy1.png} or {@link #SHAPE_GALAXY} 

     * @param outer             width/height hint for layout 

     * @param resourceAnchor    any class in the same module for {@code getResource} 

     */ 

    public static Node buildIcon(String classpathResource, double outer, Class<?> resourceAnchor) { 

        if (classpathResource == null || classpathResource.isEmpty()) { 

            return starShape(outer * 0.42); 

        } 

        if (isShapeSpec(classpathResource)) { 

            String kind = classpathResource.substring(PREFIX_SHAPE.length()); 

            if ("galaxy".equals(kind)) { 

                return galaxyBlob(outer); 

            } 

            return starShape(outer * 0.42); 

        } 

        String res = classpathResource.startsWith("/") ? classpathResource : "/" + classpathResource; 

        URL url = resourceAnchor.getResource(res); 

        if (url != null) { 

            Image img = new Image(url.toExternalForm(), outer, outer, true, true); 

            if (!img.isError()) { 

                ImageView iv = new ImageView(img); 

                iv.setFitWidth(outer); 

                iv.setFitHeight(outer); 

                iv.setPreserveRatio(true); 

                return iv; 

            } 

        } 

        return galaxyBlob(outer); 

    } 

 

    private static StackPane galaxyBlob(double size) { 

        StackPane root = new StackPane(); 

        Circle body = new Circle(size * 0.38, Color.WHITE); 

        body.setStroke(Color.web("#ffb75e")); 

        body.setStrokeWidth(2.5); 

        root.getChildren().add(body); 

        root.setPrefSize(size, size); 

        return root; 

    } 

 

    private static StackPane starShape(double outerRadius) { 

        StackPane root = new StackPane(); 

        Polygon p = new Polygon(); 

        double cx = outerRadius * 1.15; 

        double cy = outerRadius * 1.15; 

        for (int i = 0; i < 10; i++) { 

            double r = (i % 2 == 0) ? outerRadius : outerRadius * 0.42; 

            double a = -Math.PI / 2 + i * Math.PI / 5; 

            p.getPoints().addAll(cx + r * Math.cos(a), cy + r * Math.sin(a)); 

        } 

        p.setFill(Color.WHITE); 

        p.setStroke(Color.web("#ffb75e")); 

        p.setStrokeWidth(1.2); 

        root.getChildren().add(p); 

        root.setPrefSize(outerRadius * 2.3, outerRadius * 2.3); 

        return root; 

    } 

} 