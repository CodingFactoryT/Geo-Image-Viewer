package org.madcomputerscientists;

import com.drew.imaging.ImageProcessingException;

import javax.swing.JFrame;

import java.awt.*;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

public class MainFrame extends JFrame {
    public static MainPanel mainPanel;

    public MainFrame() throws ImageProcessingException, IOException {
        try {
            mainPanel = new MainPanel();
        } catch (ImageProcessingException | IOException e) {
            throw new RuntimeException(e);
        }
        this.setTitle("Geo Image Viewer");
        this.getContentPane().setPreferredSize(new Dimension(1280, 720));
        ResourceFetcher resourceFetcher = new ResourceFetcher();
        this.setIconImage(resourceFetcher.getImageByName("/AppIcon.png"));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(1150, 600));
        this.pack();
        this.setLocationRelativeTo(null);
        MapOverlayPanel.layerChangeButton.setLayerChangePanelPosition();

        this.getContentPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                MapOverlayPanel.layerChangeButton.setLayerChangePanelPosition();
            }
        });

        this.addWindowStateListener(e -> {
            Window w = e.getWindow();
            w.invalidate();
            w.validate();
        });
        this.setVisible(true);
    }
}
