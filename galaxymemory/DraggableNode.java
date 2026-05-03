package StarVault.galaxymemory;

import javafx.scene.Cursor;
import javafx.scene.Node;

/**
 * Makes a node draggable inside its parent using layout coordinates.
 * Also tracks whether a drag occurred (accessible via wasDragged()).
 */
public final class DraggableNode {

    private static final String DRAGGED_KEY = "wasDragged";

    private DraggableNode() {}

    public static void makeDraggable(Node node) {
        final double[] anchor = new double[2];

        node.getProperties().put(DRAGGED_KEY, false);

        node.setOnMousePressed(mouseEvent -> {
            node.getProperties().put(DRAGGED_KEY, false); // reset on each press
            if (mouseEvent.isPrimaryButtonDown()) {
                anchor[0] = mouseEvent.getSceneX() - node.getLayoutX();
                anchor[1] = mouseEvent.getSceneY() - node.getLayoutY();
            }
        });

        node.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                node.getProperties().put(DRAGGED_KEY, true); // mark as dragged
                node.setLayoutX(mouseEvent.getSceneX() - anchor[0]);
                node.setLayoutY(mouseEvent.getSceneY() - anchor[1]);
            }
        });

        node.setOnMouseEntered(e -> node.setCursor(Cursor.OPEN_HAND));
        node.setOnMouseExited(e -> node.setCursor(Cursor.DEFAULT));
    }

    /** Returns true if the node was dragged since the last mouse press. */
    public static boolean wasDragged(Node node) {
        Object val = node.getProperties().get(DRAGGED_KEY);
        return Boolean.TRUE.equals(val);
    }
}