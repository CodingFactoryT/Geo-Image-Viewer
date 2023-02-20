package org.madcomputerscientists;

import org.madcomputerscientists.MapOverlay.*;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

public class MapOverlayPanel extends JPanel {

    private static JPanel sidebarPanel = new JPanel();

    private static SidebarPanel sidebar;
    private static JPanel sidebarPlaceholder = new JPanel();
    private JButton menuButton = new JButton();
    public static JPanel centerPanel = new JPanel();
    private JPanel placeHolderPanel = new JPanel();
    public static MenuPanel menuPanel = new MenuPanel();
    private static Tutorial tutorialPanel = new Tutorial();
    public static LayerChangeButton layerChangeButton = new LayerChangeButton();
    private static final int settingsButtonSize = 70;


    public MapOverlayPanel(MapViewer mapViewer) {
        sidebar = new SidebarPanel(mapViewer);
        sidebarPlaceholder.setPreferredSize(new Dimension(SidebarPanel.width, SidebarPanel.height));
        sidebarPlaceholder.setOpaque(false);
        sidebarPlaceholder.setBackground(Color.red);

        sidebarPanel.setOpaque(false);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.add(sidebar);
        sidebarPanel.add(sidebarPlaceholder);

        menuButton.addActionListener(e -> toggleMenuPanel());
        menuButton.setPreferredSize(new Dimension(settingsButtonSize, settingsButtonSize));
        menuButton.setFocusPainted(false);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setIcon(new ResourceFetcher().getIconByName("menu.png"));

        updateCenterPanelMargin(SettingsPanel.getCenterPanelMargin());
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(menuPanel);
        centerPanel.add(tutorialPanel);
        placeHolderPanel.setOpaque(false);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        northPanel.setOpaque(false);
        northPanel.add(layerChangeButton);
        northPanel.add(new ScreenshotButton());
        northPanel.add(menuButton);

        JPanel eastPanel = new JPanel();
        eastPanel.setPreferredSize(new Dimension(SidebarPanel.width, SidebarPanel.height));
        eastPanel.setOpaque(false);
        eastPanel.setLayout(new BorderLayout());
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10,10,10,10));
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(new ErrorMessageHandler(), BorderLayout.LINE_END);
        eastPanel.add(bottomPanel, BorderLayout.PAGE_END);

        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        this.add(northPanel, BorderLayout.NORTH);
        this.add(eastPanel, BorderLayout.EAST);
        this.add(placeHolderPanel, BorderLayout.SOUTH);
        this.add(sidebarPanel, BorderLayout.WEST);
        this.add(centerPanel, BorderLayout.CENTER);

        if(SettingsPanel.getIsTutorialShownOnStartup()){
            this.showTutorialPanel();
        } else {
            this.hideCenterPanel();
        }
        this.hideSidebar();
    }

    public void toggleMenuPanel() {
        boolean isVisible = !centerPanel.isVisible() || tutorialPanel.isVisible();
        tutorialPanel.setVisible(false);
        centerPanel.setVisible(isVisible);
        menuPanel.setVisible(isVisible);
    }

    public void showTutorialPanel() {
        centerPanel.setVisible(true);
        menuPanel.setVisible(false);
        tutorialPanel.setVisible(true);
    }

    public static void hideCenterPanel() {
        centerPanel.setVisible(false);
        tutorialPanel.setVisible(false);
        menuPanel.setVisible(false);
    }

    public static void showSidebar() {
        sidebarPlaceholder.setVisible(false);
        sidebar.setVisible(true);
        sidebarPanel.setVisible(true);
    }

    public static void hideSidebar(){
        sidebar.setVisible(false);
        sidebarPlaceholder.setVisible(true);
        sidebarPanel.setVisible(true);
    }

    public static void updateCenterPanelMargin(int margin){
        centerPanel.setBorder(BorderFactory.createEmptyBorder(margin-settingsButtonSize,margin,margin,margin));
    }
}
