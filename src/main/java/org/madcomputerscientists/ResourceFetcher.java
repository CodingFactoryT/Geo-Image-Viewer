package org.madcomputerscientists;

import javax.imageio.ImageIO;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.image.BufferedImage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceFetcher {
    public ResourceFetcher() {

    }

    public Icon getIconByName(String name) {
        ImageIcon icon = new ImageIcon();
        InputStream iconStream = new BufferedInputStream(getClass().getResourceAsStream("/icons/" + name));
        try {
            icon = new ImageIcon(ImageIO.read(iconStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return icon;
    }

    public BufferedImage getImageByName(String name) {
        BufferedImage image = null;
        InputStream imageStream = new BufferedInputStream(getClass().getResourceAsStream(name));
        try {
            image = ImageIO.read(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }
}
