package org.madcomputerscientists.MapOverlay;

import org.madcomputerscientists.MainPanel;
import org.madcomputerscientists.ResourceFetcher;

import javax.swing.JButton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LayerChangeButton extends JButton implements ActionListener {
    private boolean isSelectionMenuVisible = false;
    public static int size = 70;

    public LayerChangeButton() {
        this.setPreferredSize(new Dimension(size, size));
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setIcon(new ResourceFetcher().getIconByName("layer.png"));
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleLayerChangePanelVisibility();
    }

    private void toggleLayerChangePanelVisibility() {
        isSelectionMenuVisible = !isSelectionMenuVisible;
        MainPanel.layerChangePanel.setVisible(isSelectionMenuVisible);
    }

    public void setLayerChangePanelPosition() {
        MainPanel.layerChangePanel.setBounds(new Point(this.getX(), this.getY()));
    }
}
