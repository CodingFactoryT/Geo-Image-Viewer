package org.madcomputerscientists;

import org.madcomputerscientists.MapOverlay.SettingsPanel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static void addLogEntry(String logEntry) throws IOException {
        FileWriter fw = new FileWriter(SettingsPanel.getLoggingFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeStamp = "[" + LocalDateTime.now().format(dateTimeFormatter) + "]";
        bw.write(timeStamp + " " + logEntry);
        bw.newLine();
        bw.close();
        fw.close();
    }
}
