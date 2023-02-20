package org.madcomputerscientists.MapOverlay;


import org.madcomputerscientists.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class SidebarPanel extends JPanel {
    public static int width = 200;
    public static int height = 300;  //height doesn´t matter because it is automatically set by the BorderLayout

    private ArrayList<DumpingSite> list;
    public int dumpingSiteIndex = -1; // The negative 1 indicates that by default no dumping site is selected
    private static JPanel container = new JPanel();
    private static JScrollPane scrollPane = new JScrollPane();

    public SidebarPanel(MapViewer mapViewer) {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(width, height));
        list = mapViewer.getDumpingSites();
        mapViewer.setSidebarPanelUpdate(this, "drawSidepanel");

        scrollPane.getVerticalScrollBar().setUnitIncrement(30);

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        container.setOpaque(false);
        scrollPane.setViewportView(container);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVisible(false);
        this.add(scrollPane, BorderLayout.CENTER);
        drawSidepanel();
    }

    public static BufferedImage scaleImage(BufferedImage image, int scaleType) {
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double scale = (width - 20 - ((Integer)UIManager.get("ScrollBar.width")).intValue()) / (double) image.getWidth();   //20 = 2*10 (10 = border on left and right side)
        int newWidth = (int) (imageWidth * scale);
        int newHeight = (int) (imageHeight * scale);

        // Draw the scaled image
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, scaleType);
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, newWidth, newHeight, null);
        return newImage;
    }

    public void drawSidepanel() {
        ArrayList<DumpingSite> dumpingSites = MainPanel.mapViewer.getDumpingSites();
        if (dumpingSiteIndex == -1 || dumpingSites.size() == 0) {
            return;
        }
        container.removeAll();
        scrollPane.setVisible(true);

        DumpingSite dumpingSite = dumpingSites.get(dumpingSiteIndex);
        Coordinate centerCoordinate = dumpingSite.getCenterCoordinate();
        String cLat = "";
        String cLong = "";
        if (SettingsPanel.getCoordinateRepresentation() == CoordinateRepresentation.DMS) {
            cLat = centerCoordinate.getLatitude_str();
            cLong = centerCoordinate.getLongitude_str();
        }
        else {
            cLat = centerCoordinate.getLatitude_dec_Formatted();
            cLong = centerCoordinate.getLongitude_dec_Formatted();
        }
        JPanel headerPanel = new JPanel();
        headerPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("Dumping Site " + (dumpingSiteIndex+1) + ":          "));

        JButton deleteButton = new JButton();
        deleteButton.setPreferredSize(new Dimension(24, 24));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);

        ResourceFetcher resourceFetcher = new ResourceFetcher();
        deleteButton.setIcon(resourceFetcher.getIconByName("delete.png"));
        deleteButton.addActionListener(e -> {
            MainPanel.mapViewer.dumpingSideIndex = -1;
            MainPanel.mapViewer.deleteDumpingSite(dumpingSite);
            MenuPanel.updateTable();
            clearSidepanel();
            MapOverlayPanel.hideSidebar();
        });
        headerPanel.add(deleteButton);
        container.add(headerPanel);
        container.add(new JLabel("Latitude: " + cLat));
        container.add(new JLabel("Longitude: " + cLong));

        JPanel contactPanel = new JPanel();
        contactPanel.setMaximumSize(new Dimension(190, 40));
        contactPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        contactPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        contactPanel.add(new JLabel("Contact: "));

        String contact = dumpingSite.getContact();
        if(contact.isEmpty()){
            contact = "-";
        }
        JTextField contactField = new JTextField(contact);
        contactField.setPreferredSize(new Dimension(100, 20));
        contactField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                dumpingSite.setContact(contactField.getText());
                MenuPanel.updateTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                dumpingSite.setContact(contactField.getText());
                MenuPanel.updateTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        contactPanel.add(contactField);
        container.add(contactPanel);

        JPanel typePanel = new JPanel();
        typePanel.setMaximumSize(new Dimension(190, 100));
        typePanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.X_AXIS));
        JLabel typeLabel = new JLabel("Type:   ");

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.Y_AXIS));
        ButtonGroup typeGroup = new ButtonGroup();

        JRadioButton crudeDumpingSiteButton = new JRadioButton("Crude Dumpingsite");
        JRadioButton transitionPointButton = new JRadioButton("Transition Point");

        if(dumpingSite.getDumpingSiteType() == DumpingSiteType.CRUDE_DUMPING_SITE){
            transitionPointButton.setSelected(false);
            crudeDumpingSiteButton.setSelected(true);
        } else if(dumpingSite.getDumpingSiteType() == DumpingSiteType.TRANSITION_POINT){
            crudeDumpingSiteButton.setSelected(false);
            transitionPointButton.setSelected(true);
        }

        crudeDumpingSiteButton.addActionListener(e -> {
            dumpingSite.setDumpingSiteType(DumpingSiteType.CRUDE_DUMPING_SITE);
            MenuPanel.updateTable();
        });
        transitionPointButton.addActionListener(e -> {
            dumpingSite.setDumpingSiteType(DumpingSiteType.TRANSITION_POINT);
            MenuPanel.updateTable();
        });

        typeGroup.add(crudeDumpingSiteButton);
        typeGroup.add(transitionPointButton);
        typePanel.add(typeLabel);

        radioButtonPanel.add(crudeDumpingSiteButton);
        radioButtonPanel.add(transitionPointButton);
        typePanel.add(radioButtonPanel);
        container.add(typePanel);

        for (int i = 0; i < list.get(dumpingSiteIndex).getImageRecordLocations().size(); ++i) {
            try {
                ImageRecordLocation irl = list.get(dumpingSiteIndex).getImageRecordLocations().get(i);
                BufferedImage scaledImage = scaleImage(irl.getBufferedImage(), BufferedImage.TYPE_INT_ARGB);

                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
                imageLabel.setLayout(new BorderLayout());

                imageLabel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) { }

                    @Override
                    public void mousePressed(MouseEvent e) { }

                    @Override
                    public void mouseReleased(MouseEvent e) { }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        irl.getWaypoint().getButton().setIcon(resourceFetcher.getIconByName("imageRecordLocationHover.png"));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        irl.getWaypoint().getButton().setIcon(resourceFetcher.getIconByName("imageRecordLocation.png"));
                    }
                });

                JButton imageDeleteButton = new JButton();
                imageDeleteButton.setPreferredSize(new Dimension(24, 24));
                imageDeleteButton.setFocusPainted(false);
                imageDeleteButton.setBorderPainted(false);
                imageDeleteButton.setContentAreaFilled(false);

                imageDeleteButton.setIcon(resourceFetcher.getIconByName("delete.png"));
                imageDeleteButton.addActionListener(e -> {
                    container.remove(imageLabel);
                    dumpingSite.getImageRecordLocations().remove(irl);
                    MainPanel.mapViewer.remove(irl.getWaypoint().getButton());
                    MainPanel.mapViewer.getImageRecordLocationWaypoints().remove(irl.getWaypoint());
                    File imageFile = new File(Main.imageRecordLocationPath + "/" + irl.getFileName());
                    if(imageFile.exists()){
                        imageFile.delete();
                    }
                    if(!dumpingSite.hasMoved()) {
                        dumpingSite.recalculateCenter();
                    }
                    if(dumpingSite.getImageRecordLocations().size() == 0){
                        container.removeAll();
                        MapOverlayPanel.hideSidebar();
                        MainPanel.mapViewer.deleteDumpingSite(dumpingSite);
                        MenuPanel.updateTable();
                    }
                    try {
                        MainPanel.mapViewer.update();
                    } catch (InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    } catch (NoSuchMethodException ex) {
                        throw new RuntimeException(ex);
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                JPanel rightAlignedNorthPanel = new JPanel();
                rightAlignedNorthPanel.setOpaque(false);
                rightAlignedNorthPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                rightAlignedNorthPanel.add(imageDeleteButton);
                imageLabel.add(rightAlignedNorthPanel, BorderLayout.NORTH);
                container.add(imageLabel);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateUI();
    }

    public static void clearSidepanel(){
        container.removeAll();
        scrollPane.setVisible(false);
    }
}
