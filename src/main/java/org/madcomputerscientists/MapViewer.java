package org.madcomputerscientists;

import com.drew.imaging.ImageProcessingException;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import org.madcomputerscientists.MapOverlay.CoordinateRepresentation;
import org.madcomputerscientists.MapOverlay.MenuPanel;
import org.madcomputerscientists.MapOverlay.SettingsPanel;
import org.madcomputerscientists.MapOverlay.SidebarPanel;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;

public class MapViewer extends JXMapViewer {
    private ArrayList<DumpingSite> dumpingSites = new ArrayList<>();
    private Set<ClickableWaypoint> dumpingSiteWaypoints = new HashSet<>();
    private Set<ClickableWaypoint> imageRecordLocationWaypoints = new HashSet<>();

    private Object sidebarPanel = null;
    private String sidebarPanelUpdate;

    public int dumpingSideIndex = -1;

    public MapViewer() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setThreadPoolSize(Runtime.getRuntime().availableProcessors());
        this.setTileFactory(tileFactory);

        this.setAddressLocation(new GeoPosition(0,0));
        this.setZoom(17);

        int borderThickness = FileDropTarget.BORDER_THICKNESS;
        this.setBorder(BorderFactory.createEmptyBorder(borderThickness, borderThickness, borderThickness, borderThickness));
        this.addMouseWheelListener(new ZoomMouseWheelListenerCursor(this));

        MouseInputListener mil = new PanMouseInputListener(this);
        this.addMouseListener(mil);
        this.addMouseMotionListener(mil);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                MapViewer a = (MapViewer) e.getSource();
                try {
                    a.update();
                } catch (InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                } catch (NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        this.setDropTarget(new DropTarget(this, new FileDropTarget()));
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }

            @Override
            public void mousePressed(MouseEvent e) {
                ((SidebarPanel) sidebarPanel).dumpingSiteIndex = -1;
                SidebarPanel.clearSidepanel();  //clear sidepanel if user clicks on the map to deselect previously selected dumpingsites
                MapOverlayPanel.hideSidebar();  //hide it after it was cleared
                clearWaypointsOnScreen(imageRecordLocationWaypoints);
            }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        });

        if (Main.getImageDirectory() != null) {
            try {
                Main.mainFrame.setCursor(Cursor.WAIT_CURSOR);
                List<File> files = Arrays.asList(new File(Main.getImageDirectory()).listFiles());
                this.addImageRecordLocationsByFile(files);
                Main.mainFrame.setCursor(Cursor.DEFAULT_CURSOR);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setSidebarPanelUpdate(Object object, String methodName)
    {
        sidebarPanel = object;
        sidebarPanelUpdate = methodName;
    }

    private Object methodCaller(Object theObject, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return theObject.getClass().getMethod(methodName).invoke(theObject);
    }

    public void update() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.updateDumpingSiteWaypoints();
        this.updateCompoundPainter();

        SidebarPanel pan = (SidebarPanel) sidebarPanel;
        if (pan == null) {
            return;
        }

        pan.dumpingSiteIndex = dumpingSideIndex;
        methodCaller(sidebarPanel, sidebarPanelUpdate);
    }

    public void addImageRecordLocationsByFile(List<File> listOfFiles) throws ImageProcessingException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        file:
        for (File file : listOfFiles) {
            ImageRecordLocation irl = new ImageRecordLocation(file);
            if (irl.getCoordinate().getLatitude_dec() == 404 ||     //if there was an error while initializing ImageRecordLocation, e.g. the Geoposition is not set
                irl.getCoordinate().getLongitude_dec() == 404) {    //in the metadata, the ImageRecordLocation won´t be added to the map
                ErrorMessageHandler.addErrorMessage("\"" + irl.getFileName() + "\"" + " does not contain any information about the GPS-Location");
                continue;
            }
            boolean spotInRangeFound = false;

            for (DumpingSite dumpingSite : dumpingSites) {
                for (ImageRecordLocation irlFromSpot : dumpingSite.getImageRecordLocations()) {
                    if (irl.equals(irlFromSpot)) {       //ImageRecordLocation already exists
                        ErrorMessageHandler.addErrorMessage("\"" + irl.getFileName() + "\"" + " already exists");
                        continue file;
                    }
                }
                if (dumpingSite.getCenterCoordinate().isInRange(irl.getCoordinate(), SettingsPanel.getDumpingSiteDetectionRadius())) {
                    dumpingSite.addImageRecordLocation(irl);
                    spotInRangeFound = true;
                }
            }

            if (!spotInRangeFound) {
                DumpingSite dumpingSite = new DumpingSite(irl);
                ResourceFetcher resourceFetcher = new ResourceFetcher();
                dumpingSite.getCenterWaypoint().getButton().setIcon(resourceFetcher.getIconByName("dumpingSite.png"));
                dumpingSites.add(dumpingSite);
                MapOverlayPanel.menuPanel.addRowToListViewByDumpingSite(dumpingSite);
            }
        }
        this.update();
    }

    public void addDumpingSite(DumpingSite dumpingSite) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ResourceFetcher resourceFetcher = new ResourceFetcher();
        dumpingSite.getCenterWaypoint().getButton().setIcon(resourceFetcher.getIconByName("dumpingSite.png"));

        dumpingSites.add(dumpingSite);
        MapOverlayPanel.menuPanel.addRowToListViewByDumpingSite(dumpingSite);
        this.update();
    }
    public void setTileFactoryInfo(TileFactoryInfo info) {
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        this.setTileFactory(tileFactory);
    }

    private void updateDumpingSiteWaypoints() {
        clearWaypointsOnScreen(dumpingSiteWaypoints);
        for (DumpingSite s : dumpingSites) {
            dumpingSiteWaypoints.add(s.getCenterWaypoint());
            this.add(s.getCenterWaypoint().getButton());
        }
    }

    private void addImageRecordLocationsOfDumpingSiteToMap(DumpingSite dumpingSite){
        for (ImageRecordLocation s : dumpingSite.getImageRecordLocations()) {
            imageRecordLocationWaypoints.add(s.getWaypoint());
            this.add(s.getWaypoint().getButton());
        }
    }

    private void displayImageRecordLocationsOfDumpingSite(DumpingSite dumpingSite) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        clearWaypointsOnScreen(imageRecordLocationWaypoints);
        addImageRecordLocationsOfDumpingSiteToMap(dumpingSite);
        this.update();
    }

    private void updateCompoundPainter() {
        WaypointPainter<ClickableWaypoint> dumpingSitePainter = new ClickableWaypointRenderer();
        dumpingSitePainter.setWaypoints(dumpingSiteWaypoints);

        WaypointPainter<ClickableWaypoint> imageRecordLocationPainter = new ClickableWaypointRenderer();
        imageRecordLocationPainter.setWaypoints(imageRecordLocationWaypoints);

        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(imageRecordLocationPainter);
        painters.add(dumpingSitePainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        this.setOverlayPainter(painter);
    }

    private void clearWaypointsOnScreen(Set<ClickableWaypoint> set){
        for (ClickableWaypoint waypoint : set) {
            this.remove(waypoint.getButton());
        }
        set.clear();
    }

    public void dumpingSiteWaypointClicked(DumpingSite dumpingSite) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        displayImageRecordLocationsOfDumpingSite(dumpingSite);
    }

    public void updateDumpingSitePosition(ClickableWaypoint waypoint, Point pixelCoordinate) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        GeoPosition newPosition = this.convertPointToGeoPosition(new Point((int) pixelCoordinate.getX(), (int) pixelCoordinate.getY()));
        int dumpingSiteIndex = getDumpingSiteIndexByCenterWaypoint(waypoint);
        DumpingSite dumpingSite = dumpingSites.get(dumpingSiteIndex);
        dumpingSite.getCenterWaypoint().setPosition(newPosition);
        dumpingSite.getCenterCoordinate().setCoordinate(newPosition.getLatitude(), newPosition.getLongitude());

        Coordinate centerCoordinate = dumpingSite.getCenterCoordinate();
        String latitude = "";
        String longitude = "";
        if(SettingsPanel.getCoordinateRepresentation() == CoordinateRepresentation.DMS){
            latitude = centerCoordinate.getLatitude_str();
            longitude = centerCoordinate.getLongitude_str();
        } else if(SettingsPanel.getCoordinateRepresentation() == CoordinateRepresentation.DECIMAL){
            latitude = centerCoordinate.getLatitude_dec_Formatted();
            longitude = centerCoordinate.getLongitude_dec_Formatted();
        }

        MenuPanel.setTableData(dumpingSiteIndex, 1, latitude);
        MenuPanel.setTableData(dumpingSiteIndex, 2, longitude);
        this.update();
    }

    public ArrayList<DumpingSite> getDumpingSites() {
        return dumpingSites;
    }

    public void moveMapRelativeToCurrentPosition(int pixelMovementX, int pixelMovementY){
        Point2D currentCenterInPixels = this.convertGeoPositionToPoint(this.getCenterPosition());
        Point2D newCenterInPixels = new Point((int) currentCenterInPixels.getX() + pixelMovementX, (int) currentCenterInPixels.getY() + pixelMovementY);
        GeoPosition newCenter = this.convertPointToGeoPosition(newCenterInPixels);
        this.setCenterPosition(newCenter);
    }

    private int getDumpingSiteIndexByCenterWaypoint(ClickableWaypoint centerWaypoint){
        for(DumpingSite dumpingSite : dumpingSites){
            if(dumpingSite.getCenterWaypoint().equals(centerWaypoint)){
                return dumpingSites.indexOf(dumpingSite);
            }
        }
        return -1;
    }

    public void deleteDumpingSite(DumpingSite dumpingSite) {
        dumpingSites.remove(dumpingSite);
        clearWaypointsOnScreen(imageRecordLocationWaypoints);
        for(ImageRecordLocation irl : dumpingSite.getImageRecordLocations()){
            File imageFile = new File(Main.imageRecordLocationPath + "/" + irl.getFileName());
            if(imageFile.exists()){
                imageFile.delete();
            }
        }

        try {
            update();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<ClickableWaypoint> getImageRecordLocationWaypoints() {
        return imageRecordLocationWaypoints;
    }
}
