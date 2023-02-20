package org.madcomputerscientists;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.JButton;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class ClickableWaypointRenderer extends WaypointPainter<ClickableWaypoint> {
    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        for (ClickableWaypoint waypoint : this.getWaypoints()) {
            Point2D point = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());

            Rectangle viewportBounds = map.getViewportBounds();
            int x = (int) (point.getX() - viewportBounds.getX());
            int y = (int) (point.getY() - viewportBounds.getY());

            JButton button = waypoint.getButton();
            button.setLocation(x - button.getWidth() / 2, y - button.getHeight());
        }
    }
}
