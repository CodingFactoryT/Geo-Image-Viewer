package org.madcomputerscientists;

import org.jxmapviewer.viewer.GeoPosition;
import org.madcomputerscientists.MapOverlay.SettingsPanel;

import javax.swing.Timer;
import javax.swing.SwingUtilities;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

public class DumpingSite {
    private final int waypointSize = 40;
    private ArrayList<ImageRecordLocation> imageRecordLocations = new ArrayList<>();
    private Coordinate centerCoordinate;
    private ClickableWaypoint centerWaypoint = new ClickableWaypoint(new GeoPosition(0, 0), waypointSize, waypointSize);

    private DumpingSiteType dumpingSiteType = DumpingSiteType.CRUDE_DUMPING_SITE;
    private String contact = "-";
    private boolean hasMoved = false;

    public DumpingSite(ImageRecordLocation imageRecordLocation) {
        imageRecordLocations.add(imageRecordLocation);
        centerCoordinate = imageRecordLocation.getCoordinate();
        init();
    }

    public DumpingSite(){
        init();
    }

    private void init(){
        this.update();
        this.centerWaypoint.getButton().addActionListener(e -> {
            try {
                MainPanel.mapViewer.dumpingSideIndex = MainPanel.mapViewer.getDumpingSites().indexOf(DumpingSite.this);
                MainPanel.mapViewer.dumpingSiteWaypointClicked(DumpingSite.this);
                MapOverlayPanel.showSidebar();
            } catch (InvocationTargetException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        });

        Timer dumpingSiteMovementTimer = new Timer(10, e -> {
            MapViewer map = MainPanel.mapViewer;
            int mouseXOnScreen = (int) MouseInfo.getPointerInfo().getLocation().getX();
            int mouseYOnScreen = (int) MouseInfo.getPointerInfo().getLocation().getY();

            Point mapPosition = new Point(map.getLocationOnScreen());
            int mouseXOnFrame = (int) (mouseXOnScreen - mapPosition.getX());
            int mouseYOnFrame = (int) (mouseYOnScreen - mapPosition.getY());

            int minX = waypointSize / 2;
            int minY = waypointSize;
            int maxX = map.getWidth() - waypointSize / 2;
            int maxY = map.getHeight() - 2;

            int mapMovementSpeed = SettingsPanel.getMapMovementSpeed();
            int pixelMovementX = 0;
            int pixelMovementY = 0;

            if (mouseXOnFrame < minX) {
                mouseXOnFrame = minX;
                pixelMovementX = -mapMovementSpeed;
            }
            if (mouseXOnFrame > maxX) {
                mouseXOnFrame = maxX;
                pixelMovementX = mapMovementSpeed;
            }
            if (mouseYOnFrame < minY) {
                mouseYOnFrame = minY;
                pixelMovementY = -mapMovementSpeed;
            }
            if (mouseYOnFrame > maxY) {
                mouseYOnFrame = maxY;
                pixelMovementY = mapMovementSpeed;
            }

            try {
                map.updateDumpingSitePosition(DumpingSite.this.centerWaypoint, new Point(mouseXOnFrame, mouseYOnFrame));
            } catch (InvocationTargetException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            map.moveMapRelativeToCurrentPosition(pixelMovementX, pixelMovementY);
            try {
                Thread.sleep(10);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        });

        this.centerWaypoint.getButton().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    hasMoved = true;
                    dumpingSiteMovementTimer.start();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    dumpingSiteMovementTimer.stop();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        });
    }

    public void addImageRecordLocation(ImageRecordLocation imageRecordLocation) {
        if (imageRecordLocation.getCoordinate().getLatitude_dec() == 404 ||
                imageRecordLocation.getCoordinate().getLongitude_dec() == 404) {
            return;
        }
        imageRecordLocations.add(imageRecordLocation);
        this.update();
    }

    /**
     * @return newly calculated center
     */
    public Coordinate recalculateCenter() {
        double latitudeSum = 0;
        double longitudeSum = 0;

        for (ImageRecordLocation irl : imageRecordLocations) {
            latitudeSum += irl.getCoordinate().getLatitude_dec();
            longitudeSum += irl.getCoordinate().getLongitude_dec();
        }

        if(imageRecordLocations.size() == 0){
            return new Coordinate(0,0);
        }

        double latitude = latitudeSum / imageRecordLocations.size();
        double longitude = longitudeSum / imageRecordLocations.size();
        return new Coordinate(latitude, longitude);
    }

    public void update() {
        if (!hasMoved) {
            centerCoordinate = recalculateCenter();
            GeoPosition centerGeoposition = new GeoPosition(centerCoordinate.getLatitude_dec(), centerCoordinate.getLongitude_dec());
            centerWaypoint.setPosition(centerGeoposition);
        }
    }

    public ArrayList<ImageRecordLocation> getImageRecordLocations() {
        return imageRecordLocations;
    }

    public Coordinate getCenterCoordinate() {
        return centerCoordinate;
    }

    public void setCenterCoordinate(Coordinate c) {
        centerCoordinate = c;
        centerWaypoint.setPosition(new GeoPosition(c.getLatitude_dec(), c.getLongitude_dec()));
    }

    public ClickableWaypoint getCenterWaypoint() {
        return centerWaypoint;
    }


    public void setDumpingSiteType(DumpingSiteType dumpingSiteType) {
        this.dumpingSiteType = dumpingSiteType;
    }

    public DumpingSiteType getDumpingSiteType(){
        return dumpingSiteType;
    }

    public void setContact(String value){
        this.contact = value;
    }

    public String getContact(){
        return contact;
    }

    public DumpingSiteType getDumpingSiteType(DumpingSiteType dumpingSiteType) {
        return dumpingSiteType;
    }

    public boolean hasMoved(){
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved){
        this.hasMoved = hasMoved;
    }
}