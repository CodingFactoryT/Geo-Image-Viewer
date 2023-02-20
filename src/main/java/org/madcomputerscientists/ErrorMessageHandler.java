package org.madcomputerscientists;

import org.madcomputerscientists.MapOverlay.SettingsPanel;

import javax.swing.JLabel;
import javax.swing.Timer;

import java.awt.Dimension;
import java.awt.Font;

import java.io.IOException;

public class ErrorMessageHandler extends JLabel {

    private static int lastErrorMessages = 0;
    private static int errorMessages = 0;
    private static long lastErrorMessageTime = System.currentTimeMillis();
    private final Timer removeTimer = new Timer(100, e -> {
        if(System.currentTimeMillis() > lastErrorMessageTime + SettingsPanel.getErrorMessageRemoveDelay() && this.isVisible()){
            this.setVisible(false);
            lastErrorMessageTime = System.currentTimeMillis();
            lastErrorMessages = 0;
            errorMessages = 0;
        }

        if(lastErrorMessages != errorMessages){
            this.setVisible(true);
            this.setText(String.valueOf(errorMessages));
        }
    });

    public ErrorMessageHandler(){
        this.setPreferredSize(new Dimension(50, 25));
        ResourceFetcher resourceFetcher = new ResourceFetcher();
        this.setIcon(resourceFetcher.getIconByName("warning.png"));
        this.setFont(new Font("Sans Serif", Font.BOLD, 16));
        this.setText("0");
        this.setVisible(false);
        removeTimer.start();
    }

    public static void addErrorMessage(String errorMessage) {
        if(!SettingsPanel.getAreErrorMessagesShown()){
            return;
        }

        System.err.println(errorMessage);

        lastErrorMessageTime = System.currentTimeMillis();
        lastErrorMessages = errorMessages;
        errorMessages++;

        if (!SettingsPanel.getIsLoggingEnabled()) {
            return;
        }

        try {
            Logger.addLogEntry(errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
