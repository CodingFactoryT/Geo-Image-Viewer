package org.madcomputerscientists;

import com.drew.imaging.ImageProcessingException;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.madcomputerscientists.MapOverlay.MenuPanel;
import org.madcomputerscientists.MapOverlay.SettingsPanel;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.util.Collection;

public class Main {
    private static String imageDirectory = null;
    public static String savedDataPath = System.getProperty("user.home") + "/.geoimageviewer";
    public static String settingsFilePath = savedDataPath + "/settings.txt";
    public static String dumpingsitePath = savedDataPath + "/dumpingsites";
    public static String imageRecordLocationPath = dumpingsitePath + "/imageRecordLocations";
    public static MainFrame mainFrame;

    public static void main(String[] args) {
        File settingsFile = new File(settingsFilePath);
        if(settingsFile.exists()) {
            SettingsPanel.loadSettingsFromFile(settingsFilePath);
        }

        Options options = new Options();

        Option oImageDir = new Option("d", "image-directory", true, "path to directory containing images");
        oImageDir.setRequired(false);
        options.addOption(oImageDir);

        Option oLogFile = new Option("l", "log-file", true, "path to log file");
        oLogFile.setRequired(false);
        options.addOption(oLogFile);

        Option oHelp = new Option("h", "help", false, "print this help message and exit");
        oHelp.setRequired(false);
        options.addOption(oHelp);

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption(oHelp)) {
                System.out.println("geo image viewer - visualize geolocations on a map");
                System.out.println("command line arguments:");

                Collection<Option> opts = options.getOptions();

                for (Option tmp : opts) {
                    System.out.printf("\t-%s\t--%-20s %s\n", tmp.getOpt(), tmp.getLongOpt(), tmp.getDescription());
                }

                System.exit(0);
            }

            if (line.hasOption(oImageDir)) {
                imageDirectory = line.getOptionValue(oImageDir);
            }

            if (line.hasOption(oLogFile)) {
                //isLoggingEnabled = true;
                SettingsPanel.setLoggingFile(line.getOptionValue(oLogFile));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mainFrame = new MainFrame();
        } catch (ImageProcessingException | IOException e) {
            throw new RuntimeException(e);
        }

        MenuPanel.loadDumpingSitesFromPath(dumpingsitePath);

        //save Settings and Dumpingsites before window closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                SettingsPanel.saveSettingsToFile(settingsFilePath);
                MenuPanel.saveDumpingSitesToPath(dumpingsitePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "Shutdown-thread"));
    }

    public static String getImageDirectory() {
        return imageDirectory;
    }
}
