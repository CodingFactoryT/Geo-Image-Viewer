package org.madcomputerscientists.MapOverlay;

import org.madcomputerscientists.MapOverlayPanel;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class Tutorial extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel();

    private final Color COLOR_CLICKABLE = new Color(0x05839F);
    private final Color COLOR_NOT_CLICKABLE = Color.GRAY;
    private final Color COLOR_TEXT = Color.WHITE;

    private final String CONTENT_NEXT = "NEXT >";
    private final String CONTENT_CLOSE = "CLOSE ";
    private final String CONTENT_BACK = "< BACK";
    private final String CONTENT_SKIP = "SKIP";


    public Tutorial() {
        this.setLayout(new BorderLayout());

        contentPanel.setLayout(cardLayout);
        contentPanel.add(getFileUploadPanelTutorial());
        contentPanel.add(getMapMovementPanelTutorial());
        contentPanel.add(getWaypointMovePanelTutorial());
        contentPanel.add(getSidebarPanelTutorial());
        contentPanel.add(getListViewPanelTutorial());
        contentPanel.add(getImExportPanelTutorial());
        contentPanel.add(getLayerChangePanelTutorial());
        contentPanel.add(getScreenshotPanelTutorial());

        this.add(contentPanel, BorderLayout.CENTER);
        this.add(getNavigationPanel(), BorderLayout.SOUTH);
    }

    public JPanel getNavigationPanel() {
        JPanel navigationRightAlignedPanel = new JPanel();

        JButton nextButton = new JButton(CONTENT_NEXT);
        JButton previousButton = new JButton(CONTENT_BACK);

        nextButton.addActionListener(e -> {
            if (nextButton.getText().equals(CONTENT_CLOSE)) {
                MapOverlayPanel.hideCenterPanel();
            }
            previousButton.setBackground(COLOR_CLICKABLE);
            previousButton.setEnabled(true);
            cardLayout.next(contentPanel);
            for (int i = 0; i < contentPanel.getComponentCount(); ++i) {
                if (contentPanel.getComponent(i).isVisible() && contentPanel.getComponent(i) == contentPanel.getComponent(contentPanel.getComponentCount() - 1)) {
                    nextButton.setText(CONTENT_CLOSE);
                }
            }
        });
        nextButton.setBackground(COLOR_CLICKABLE);
        nextButton.setFocusPainted(false);
        nextButton.setForeground(COLOR_TEXT);


        previousButton.addActionListener(e -> {
            if (previousButton.getBackground().equals(COLOR_NOT_CLICKABLE)) {
                return;
            }
            nextButton.setText(CONTENT_NEXT);
            if (nextButton.getText().equals(CONTENT_CLOSE)) {
                MapOverlayPanel.hideCenterPanel();
            }
            cardLayout.previous(contentPanel);
            for (int i = 0; i < contentPanel.getComponentCount(); ++i) {
                if (contentPanel.getComponent(i).isVisible() && contentPanel.getComponent(i) == contentPanel.getComponent(0)) {
                    previousButton.setBackground(COLOR_NOT_CLICKABLE);
                    previousButton.setEnabled(false);
                }
            }
        });
        previousButton.setBackground(COLOR_NOT_CLICKABLE);
        previousButton.setFocusPainted(false);
        previousButton.setEnabled(false);
        previousButton.setForeground(COLOR_TEXT);

        JButton skipButton = new JButton(CONTENT_SKIP);
        skipButton.addActionListener(e -> MapOverlayPanel.hideCenterPanel());
        skipButton.setBackground(COLOR_CLICKABLE);
        skipButton.setForeground(COLOR_TEXT);
        skipButton.setFocusPainted(false);

        navigationRightAlignedPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        navigationRightAlignedPanel.add(previousButton);
        navigationRightAlignedPanel.add(nextButton);

        JPanel navigationLeftAlignedPanel = new JPanel();
        navigationLeftAlignedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        navigationLeftAlignedPanel.add(skipButton);

        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(1,2));
        navigationPanel.add(navigationLeftAlignedPanel);
        navigationPanel.add(navigationRightAlignedPanel);

        return navigationPanel;
    }

    public JPanel getFileUploadPanelTutorial(){
        String description = "You can upload a file by using the drag-and-drop feature of this app"+
                "\nSimply drag the image(s) you want to import onto the application window and they " +
                "get automatically imported\nYou can also use folders to upload the images that are contained " +
                "in these\n\nSometime, the images need a bit to upload, especially, if you have a large amount " +
                "of it\nSimply wait until the red border isn´t visible anymore and you´re good to go";
        return getPanelByGifNameAndDescription("fileUpload.gif", description, "Uploading Files");
    }

    public JPanel getMapMovementPanelTutorial(){
        String description = "You can move the map while pressing your left mouse button\n" +
                "If you use the scroll-wheel of your mouse, you can zoom in and out";
        return getPanelByGifNameAndDescription("mapMovement.gif", description, "Map Movement");    }

    public JPanel getWaypointMovePanelTutorial(){
        String description = "You can move waypoints while pressing your right mouse button\n" +
                "If you drag the waypoint against one of the window´s edges, the map will move and you can continue moving your waypoint";
        return getPanelByGifNameAndDescription("waypointMove.gif", description, "Moving Dumpingsites");
    }

    public JPanel getSidebarPanelTutorial(){
        String description = "Once you click on a waypoint, the sidebar-panel will appear." +
                "\nHere you can see the waypoint coordinates and set the contact (e.g. e-mail or name) as well as the dumpingsite-type." +
                "\nThe images that were categorized into this dumpingsite are also visible." +
                "\nIf you hover over an image, one of the blue dots will turn orange. This is the dot representing the location where the image was taken" +
                "\nYou can delete images or the whole dumpingsite by pressing the corresponding delete button." +
                "\nIf you delete a dumpingsite or click anywhere on the map, the sidebar-panel will disappear.";
        return getPanelByGifNameAndDescription("sidebarPanel.gif", description, "Sidebar-Panel");
    }

    public JPanel getListViewPanelTutorial(){
        String description = "You can view the most important information about every DumpingSite in the ListView\n" +
                "Simply navigate in the menu to \"ListView\" and you will find the table containing the data";
        return getPanelByGifNameAndDescription("listView.gif", description, "ListView");
    }

    public JPanel getImExportPanelTutorial(){
        String description = "You can import or export the settings or the dumpingsites if you want to exchange them with another person." +
                "\nIf you want to import the dumpingsites, make sure that you select the folder called \"Dumpingsites\" and not only the files it contains";
        return getPanelByGifNameAndDescription("im_export.gif", description, "Import/Export");
    }

    public JPanel getLayerChangePanelTutorial(){
        String description = "You can change the representation of the map by clicking on the given icon and selecting a map theme";
        return getPanelByGifNameAndDescription("layerChange.gif", description, "Map Representation");
    }

    public JPanel getScreenshotPanelTutorial(){
        String description = "you can make a screenshot of the map by clicking the image icon on the top\n" +
                "After that, you choose where to save your screenshot, which name it should have and in which format it should be saved\n" +
                "Simply click on save and your screenshot gets saved into your selected directory";
        return getPanelByGifNameAndDescription("screenshot.gif", description, "Taking Screenshots");
    }

    public JPanel getPanelByGifNameAndDescription(String gifName, String description, String title){
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setLayout(new GridLayout(1, 2));

        JLabel gifLabel = new JLabel();
        gifLabel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        URL gifURL = getClass().getResource("/gifs/" + gifName);
        gifLabel.setIcon(new StretchIcon(gifURL));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        textPanel.setBackground(Color.white);

        JTextArea titleArea = new JTextArea(title + "\n");
        titleArea.setEditable(false);
        titleArea.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
        titleArea.setFont(new Font("Helvetica", Font.BOLD, 20));

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font("Helvetica", Font.PLAIN, 15));

        textPanel.add(titleArea, BorderLayout.NORTH);
        textPanel.add(descriptionArea, BorderLayout.CENTER);

        panel.add(gifLabel);
        panel.add(textPanel);

        return panel;
    }
}

