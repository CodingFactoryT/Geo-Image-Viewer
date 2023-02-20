package org.madcomputerscientists;

import com.drew.imaging.ImageProcessingException;
import org.madcomputerscientists.MapOverlay.LayerChangePanel;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import java.awt.Dimension;

import java.io.IOException;

public class MainPanel extends JLayeredPane {
    public static MapViewer mapViewer;
    private static MapOverlayPanel mapOverlayPanel;
    private JPanel popupPanel = new JPanel();
    public static LayerChangePanel layerChangePanel = new LayerChangePanel();

    public MainPanel() throws ImageProcessingException, IOException {
        this.setLayout(null);

        mapViewer = new MapViewer();
        mapViewer.setBounds(0,0,1280,720);
        this.add(mapViewer, JLayeredPane.DEFAULT_LAYER);

        mapOverlayPanel = new MapOverlayPanel(mapViewer);
        mapOverlayPanel.setBounds(0,0,1280,720);
        this.add(mapOverlayPanel, JLayeredPane.PALETTE_LAYER);

        popupPanel.setBounds(0,0,1280,720);
        popupPanel.setOpaque(false);
        popupPanel.setLayout(null);
        popupPanel.add(layerChangePanel);
        this.add(popupPanel, JLayeredPane.POPUP_LAYER);
    }

    public static void setOverlayPanelVisibility(boolean isVisible) {
        mapOverlayPanel.setVisible(isVisible);
    }

    @Override
    public void repaint() {
        Dimension bounds = Main.mainFrame.getContentPane().getSize();
        mapOverlayPanel.setBounds(0,0,bounds.width, bounds.height);
        mapViewer.setBounds(0,0,bounds.width, bounds.height);
        popupPanel.setBounds(0,0,bounds.width, bounds.height);
    }
}
