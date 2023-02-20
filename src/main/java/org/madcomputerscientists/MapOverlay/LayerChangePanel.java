package org.madcomputerscientists.MapOverlay;

import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.madcomputerscientists.ResourceFetcher;
import org.madcomputerscientists.MapOverlayPanel;
import org.madcomputerscientists.MapViewer;
import org.madcomputerscientists.MainPanel;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LayerChangePanel extends JPanel implements ActionListener {
    private static final int buttonSize = LayerChangeButton.size;
    private static final int rows = 2;
    private static final int columns = 3;
    private static final int gap = 10;
    public static int width = rows * buttonSize + gap * (rows-1);
    public static int height = columns * buttonSize + gap * (columns-1);

    public LayerChangePanel() {
        this.setLayout(new GridLayout(rows,columns, gap, gap));
        this.add(getButton("/layers/default.png", "Default"));
        this.add(getButton("/layers/osm.png", "OSM"));
        this.add(getButton("/layers/satellite.png", "Satellite"));
        this.add(getButton("/layers/hybrid.png", "Hybrid"));
        this.setBackground(Color.lightGray);
        this.setVisible(false);
    }

    public void setBounds(Point layerChangeButtonLocation) {
        int centerX = (int) (layerChangeButtonLocation.getX() + buttonSize / 2);
        int x = centerX - width / 2;
        int y = MapOverlayPanel.layerChangeButton.getY() + buttonSize;

        this.setBounds(x, y ,width, height);
    }

    private JLabel getButton(String imageName, String description) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(buttonSize,buttonSize+30));
        label.setLayout(new BoxLayout(label, BoxLayout.Y_AXIS));

        LayerButton button = new LayerButton();
        button.setName(description);
        button.setBounds(0,0,buttonSize,buttonSize);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        Image previewImage = new ResourceFetcher().getImageByName(imageName);
        previewImage = previewImage.getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(previewImage));
        button.addActionListener(this);
        JTextField descriptionTextArea = new JTextField(description);
        descriptionTextArea.setHorizontalAlignment(JTextField.CENTER);
        descriptionTextArea.setOpaque(false);
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setFocusable(false);
        descriptionTextArea.setBorder(null);

        label.add(button);
        label.add(descriptionTextArea);

        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LayerButton button = (LayerButton) e.getSource();
        MapViewer mapViewer = MainPanel.mapViewer;
        TileFactoryInfo info;
        int zoom = mapViewer.getZoom();
        switch (button.getName()) {
            case "Default":
                info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
                if (mapViewer.getZoom() > info.getMaximumZoomLevel()) zoom = info.getMaximumZoomLevel();
                if (mapViewer.getZoom() < info.getMinimumZoomLevel()) zoom = info.getMinimumZoomLevel();
                mapViewer.setTileFactoryInfo(info);
                mapViewer.setZoom(zoom);
                break;
            case "OSM":
                info = new OSMTileFactoryInfo();
                if (mapViewer.getZoom() > info.getMaximumZoomLevel()) zoom = info.getMaximumZoomLevel();
                if (mapViewer.getZoom() < info.getMinimumZoomLevel()) zoom = info.getMinimumZoomLevel();
                mapViewer.setTileFactoryInfo(info);
                mapViewer.setZoom(zoom);
                break;
            case "Satellite":
                info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
                if (mapViewer.getZoom() > info.getMaximumZoomLevel()) zoom = info.getMaximumZoomLevel();
                if (mapViewer.getZoom() < info.getMinimumZoomLevel()) zoom = info.getMinimumZoomLevel();
                mapViewer.setTileFactoryInfo(info);
                mapViewer.setZoom(zoom);
                break;
            case "Hybrid":
                info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID);
                if (mapViewer.getZoom() > info.getMaximumZoomLevel()) zoom = info.getMaximumZoomLevel();
                if (mapViewer.getZoom() < info.getMinimumZoomLevel()) zoom = info.getMinimumZoomLevel();
                mapViewer.setTileFactoryInfo(info);
                mapViewer.setZoom(zoom);
                break;
            default:
                try {
                    throw new Exception("Layer type not found");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
        }
    }
}

class LayerButton extends JButton {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
