package de.perfectpattern.print.imposition.service.imposition.layout;

import de.perfectpattern.print.imposition.service.imposition.layout.object.PlacedObject;
import de.perfectpattern.print.imposition.service.imposition.layout.label.AbstractLabel;
import de.perfectpattern.print.imposition.model.type.Anchor;

import java.util.List;
import java.util.Map;

/**
 * Model class for a XJDF Layout.
 */
public class Layout {

    private List<PlacedObject> placedObjectsFront;

    private List<PlacedObject> placedObjectsBack;

    private Map<Anchor, List<AbstractLabel>> labels;

    /**
     * Default constructor.
     */
    public Layout() {

    }

    public List<PlacedObject> getPlacedObjectsFront() {
        return placedObjectsFront;
    }

    public void setPlacedObjectsFront(List<PlacedObject> placedObjectsFront) {
        this.placedObjectsFront = placedObjectsFront;
    }

    public List<PlacedObject> getPlacedObjectsBack() {
        return placedObjectsBack;
    }

    public void setPlacedObjectsBack(List<PlacedObject> placedObjectsBack) {
        this.placedObjectsBack = placedObjectsBack;
    }

    public Map<Anchor, List<AbstractLabel>> getLabels() {
        return labels;
    }

    public void setLabels(Map<Anchor, List<AbstractLabel>> labels) {
        this.labels = labels;
    }
}
