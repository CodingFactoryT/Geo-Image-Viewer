package org.madcomputerscientists.MapOverlay;

import org.madcomputerscientists.MainPanel;
import org.madcomputerscientists.ResourceFetcher;

import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

public class ScreenshotButton extends JButton implements ActionListener {

    public ScreenshotButton() {
        this.setPreferredSize(new Dimension(70,70));
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setIcon(new ResourceFetcher().getIconByName("image.png"));
        this.addActionListener(this);
    }

    public BufferedImage getMapViewerImage() {
        int width = MainPanel.mapViewer.getWidth();
        int height = MainPanel.mapViewer.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        MainPanel.setOverlayPanelVisibility(false);
        MainPanel.mapViewer.paint(image.createGraphics());
        MainPanel.setOverlayPanelVisibility(true);
        return image;
    }

    private void save(BufferedImage screenShot, File file, String filetype) {
        try {
            ImageIO.write(screenShot, filetype, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose the Path where to save the screenshot");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setSelectedFile(new File("Screenshot"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        String[] acceptedExtensions = new String[]{"png", "jpg", "gif", "tiff"};

        for (String s : acceptedExtensions) {
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("." + s, s));
        }

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            BufferedImage screenShot = this.getMapViewerImage();
            String fileType = ((FileNameExtensionFilter)fileChooser.getFileFilter()).getExtensions()[0];
            File fileToBeSaved = new File(fileChooser.getSelectedFile().getAbsolutePath() + "." + fileType);

            this.save(screenShot, fileToBeSaved, fileType);
        }
    }
}
