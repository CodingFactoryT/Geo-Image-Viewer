package org.madcomputerscientists;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.JButton;
import javax.swing.Icon;

import java.awt.Dimension;

public class ClickableWaypoint extends DefaultWaypoint {
    private ResourceFetcher resourceFetcher = new ResourceFetcher();
    private Icon defaultIcon = resourceFetcher.getIconByName("imageRecordLocation.png");
    private JButton button = new JButton();

    public ClickableWaypoint(GeoPosition geoPosition, int width, int height) {
        super(geoPosition);
        button.setContentAreaFilled(false);
        button.setIcon(defaultIcon);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setSize(new Dimension(width, height));
        button.setBorder(null);
    }

    public JButton getButton() {
        return button;
    }

    public boolean equals(ClickableWaypoint waypoint){
        return this.getPosition().equals(waypoint.getPosition());
    }
}
