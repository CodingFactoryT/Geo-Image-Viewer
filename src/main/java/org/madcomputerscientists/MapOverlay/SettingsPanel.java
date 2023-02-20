package org.madcomputerscientists.MapOverlay;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.madcomputerscientists.Main;
import org.madcomputerscientists.MainPanel;
import org.madcomputerscientists.MapOverlayPanel;
import org.madcomputerscientists.MapViewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SettingsPanel extends JPanel {
    private static boolean isLoggingEnabled;
    private static String loggingFile;
    private static int mapMovementSpeed;
    private static boolean isTutorialShownOnStartup;
    private static CoordinateRepresentation coordinateRepresentation;
    private static boolean areErrorMessagesShown;
    private static int errorMessageRemoveDelay;
    private static int centerPanelMargin; // min: 70
    private static int dumpingSiteDetectionRadius;

    private static JCheckBox isLoggingEnabledCheckbox = new JCheckBox();
    private static JButton selectLoggingFileButton = new JButton("Select Log-Directory");
    private static JTextArea loggingFilePathDisplayArea = new JTextArea();
    private static JSlider mapMovementSpeedSlider = new JSlider();
    private static JCheckBox isTutorialShownOnStartupCheckbox = new JCheckBox();
    private static ButtonGroup coordinateRepresentationGroup = new ButtonGroup();
    private static JRadioButton dmsButton = new JRadioButton("DMS");
    private static JRadioButton decimalButton = new JRadioButton("DECIMAL");
    private static JCheckBox areErrorMessagesShownCheckbox = new JCheckBox();
    private static JSlider errorMessageRemoveDelaySlider = new JSlider();
    private static JSlider centerPanelMarginSlider = new JSlider();
    private static JSlider dumpingSiteDetectionRadiusSlider = new JSlider();

    private static JButton loadSettingsButton = new JButton("Load Settings");
    private static JButton saveSettingsButton = new JButton("Save Settings");


    public SettingsPanel() {
        this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        this.setLayout(new GridBagLayout());

        JPanel settings = new JPanel();
        settings.setOpaque(false);
        settings.setBorder(new EmptyBorder(10,10,10,10));
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
        settings.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        File settingsFile = new File(Main.settingsFilePath);
        if(!settingsFile.exists()) {
            setIsLoggingEnabled(false);
            setLoggingFile("-");
            setMapMovementSpeed(8);
            setIsTutorialShownOnStartup(true);
            setCoordinateRepresentation(CoordinateRepresentation.DMS);
            setAreErrorMessagesShown(true);
            setErrorMessageRemoveDelay(7000);
            setCenterPanelMargin(200);   // min: 70
            setDumpingSiteDetectionRadius(100); // currently does not work
        }

        JPanel isLoggingEnabledPanel = new JPanel();
        isLoggingEnabledPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel isLoggingEnabledLabel = new JLabel("Enable Logging: ");
        isLoggingEnabledCheckbox.addActionListener(e -> {
            setIsLoggingEnabled(isLoggingEnabledCheckbox.isSelected());
            selectLoggingFileButton.setEnabled(getIsLoggingEnabled());
        });
        isLoggingEnabledPanel.add(isLoggingEnabledLabel);
        isLoggingEnabledPanel.add(isLoggingEnabledCheckbox);
        settings.add(isLoggingEnabledPanel);

        JPanel setLoggingFilePanel = new JPanel();
        setLoggingFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel setLoggingFileLabel = new JLabel("Log File Saved At: ");
        loggingFilePathDisplayArea.setEditable(false);
        loggingFilePathDisplayArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        loggingFilePathDisplayArea.setLineWrap(true);
        selectLoggingFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Choose a directory where you want to save the log file");
            fileChooser.setMultiSelectionEnabled(false);
            int returnCode = fileChooser.showOpenDialog(SettingsPanel.this);
            if(returnCode == JFileChooser.APPROVE_OPTION){
                setLoggingFile(fileChooser.getCurrentDirectory().getPath() + "\\geoimageviewer-log.txt");
            }
        });
        setLoggingFilePanel.add(setLoggingFileLabel);
        setLoggingFilePanel.add(loggingFilePathDisplayArea);
        settings.add(setLoggingFilePanel);
        JPanel selectLoggingFileButtonPanel = new JPanel();
        selectLoggingFileButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        selectLoggingFileButtonPanel.add(selectLoggingFileButton);
        settings.add(selectLoggingFileButtonPanel);

        JPanel mapMovementSpeedPanel = new JPanel();
        mapMovementSpeedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel mapMovementSpeedLabel = new JLabel("Map Movement Speed: ");
        mapMovementSpeedSlider.addChangeListener(e -> {
            setMapMovementSpeed(mapMovementSpeedSlider.getValue());
        });
        mapMovementSpeedSlider.setMinimum(2);
        mapMovementSpeedSlider.setMaximum(20);
        mapMovementSpeedPanel.add(mapMovementSpeedLabel);
        mapMovementSpeedPanel.add(mapMovementSpeedSlider);
        settings.add(mapMovementSpeedPanel);

        JPanel isTutorialShownOnStartupPanel = new JPanel();
        isTutorialShownOnStartupPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel isTutorialShownOnStartupLabel = new JLabel("Display Tutorial On Startup: ");
        isTutorialShownOnStartupCheckbox.addActionListener(e -> {
            setIsTutorialShownOnStartup(isTutorialShownOnStartupCheckbox.isSelected());
        });
        isTutorialShownOnStartupPanel.add(isTutorialShownOnStartupLabel);
        isTutorialShownOnStartupPanel.add(isTutorialShownOnStartupCheckbox);
        settings.add(isTutorialShownOnStartupPanel);

        JPanel coordinateRepresentationPanel = new JPanel();
        coordinateRepresentationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel coordinateRepresentationLabel = new JLabel("Coordinate Representation: ");
        dmsButton.setSelected(true);
        dmsButton.addActionListener(e -> setCoordinateRepresentation(CoordinateRepresentation.DMS));
        decimalButton.setSelected(false);
        decimalButton.addActionListener(e -> setCoordinateRepresentation(CoordinateRepresentation.DECIMAL));
        coordinateRepresentationGroup.add(dmsButton);
        coordinateRepresentationGroup.add(decimalButton);
        coordinateRepresentationPanel.add(coordinateRepresentationLabel);
        coordinateRepresentationPanel.add(dmsButton);
        coordinateRepresentationPanel.add(decimalButton);
        settings.add(coordinateRepresentationPanel);

        JPanel setAreErrorMessagesShownPanel = new JPanel();
        setAreErrorMessagesShownPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel setAreErrorMessagesShownLabel = new JLabel("Show Error Messages: ");
        areErrorMessagesShownCheckbox.addActionListener(e -> {
            setAreErrorMessagesShown(areErrorMessagesShownCheckbox.isSelected());
        });
        setAreErrorMessagesShownPanel.add(setAreErrorMessagesShownLabel);
        setAreErrorMessagesShownPanel.add(areErrorMessagesShownCheckbox);
        settings.add(setAreErrorMessagesShownPanel);

        JPanel errorMessageRemoveDelayPanel = new JPanel();
        errorMessageRemoveDelayPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel errorMessageRemoveDelayLabel = new JLabel("Error Message Remove Delay: ");
        errorMessageRemoveDelaySlider.addChangeListener(e -> {
            setErrorMessageRemoveDelay(errorMessageRemoveDelaySlider.getValue());
        });
        errorMessageRemoveDelaySlider.setMinimum(1000);
        errorMessageRemoveDelaySlider.setMaximum(10000);
        errorMessageRemoveDelayPanel.add(errorMessageRemoveDelayLabel);
        errorMessageRemoveDelayPanel.add(errorMessageRemoveDelaySlider);
        settings.add(errorMessageRemoveDelayPanel);

        JPanel centerPanelMarginPanel = new JPanel();
        centerPanelMarginPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel centerPanelMarginLabel = new JLabel("Center Panel Size: ");
        centerPanelMarginSlider.addChangeListener(e -> {
            setCenterPanelMargin(centerPanelMarginSlider.getValue());
        });
        centerPanelMarginSlider.setMinimum(70);
        centerPanelMarginSlider.setMaximum(250);
        centerPanelMarginSlider.setInverted(true);
        centerPanelMarginPanel.add(centerPanelMarginLabel);
        centerPanelMarginPanel.add(centerPanelMarginSlider);
        settings.add(centerPanelMarginPanel);

        JPanel dumpingSiteDetectionPanel = new JPanel();
        dumpingSiteDetectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel dumpingSiteDetectionLabel = new JLabel("Dumping Site Detection Radius: ");
        dumpingSiteDetectionRadiusSlider.addChangeListener(e -> {
            setDumpingSiteDetectionRadius(dumpingSiteDetectionRadiusSlider.getValue());
        });
        dumpingSiteDetectionRadiusSlider.setMinimum(1);
        dumpingSiteDetectionRadiusSlider.setMaximum(500);
        dumpingSiteDetectionPanel.add(dumpingSiteDetectionLabel);
        dumpingSiteDetectionPanel.add(dumpingSiteDetectionRadiusSlider);
        settings.add(dumpingSiteDetectionPanel);

        // Load and Save settings

        loadSettingsButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Choose the file to save your settings.");
            fileChooser.setMultiSelectionEnabled(false);

            int returnCode = fileChooser.showOpenDialog(SettingsPanel.this);
            if (returnCode == JFileChooser.APPROVE_OPTION) {
                fileChooser.getSelectedFile().getAbsolutePath();
                loadSettingsFromFile(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        saveSettingsButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Choose the file to save your settings.");
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setApproveButtonText("Save");

            int returnCode = fileChooser.showOpenDialog(SettingsPanel.this);
            if (returnCode == JFileChooser.APPROVE_OPTION) {
                try {
                    saveSettingsToFile(fileChooser.getSelectedFile().getAbsolutePath());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2 ,10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buttonPanel.add(saveSettingsButton);
        buttonPanel.add(loadSettingsButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.8;
        this.add(settings, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.2;
        this.add(buttonPanel, gbc);

        if(settingsFile.exists()) { //has to be called again as the change-listeners of some sliders fire on initialization and otherwise the values would be restored to the default ones
            loadSettingsFromFile(Main.settingsFilePath);
        }
    }

    private static String[] HEADERS = { "isLoggingEnabled", "loggingFile", "mapMovementSpeed", "isTutorialShownOnStartup", "coordinateRepresentation"
                        , "areErrorMessagesShown", "errorMessageRemoveDelay", "centerPanelMargin", "dumpingSiteDetectionRadius"
    };

    public static void saveSettingsToFile(String path) throws IOException {
        File settingsFile = new File(path);
        if(!settingsFile.exists()) {
            File applicationPath = new File(System.getProperty("user.home") + "/.geoimageviewer");
            applicationPath.mkdir();
            settingsFile.createNewFile();
        }

        FileWriter out = new FileWriter(path);

        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(HEADERS))) {
                printer.printRecord(isLoggingEnabled, loggingFile, mapMovementSpeed, isTutorialShownOnStartup,
                        coordinateRepresentation, areErrorMessagesShown, errorMessageRemoveDelay, centerPanelMargin, dumpingSiteDetectionRadius);
        }
    }

    public static void loadSettingsFromFile(String path) {
        try {
            Reader reader = Files.newBufferedReader(Path.of(path));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader(HEADERS)
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : records) {  //iterates only one time as there is only one settings entry
                setIsLoggingEnabled(Boolean.parseBoolean(record.get("isLoggingEnabled")));
                setLoggingFile(record.get("loggingFile"));
                setMapMovementSpeed(Integer.parseInt(record.get("mapMovementSpeed")));
                setIsTutorialShownOnStartup(Boolean.parseBoolean(record.get("isTutorialShownOnStartup")));
                setCoordinateRepresentation(CoordinateRepresentation.valueOf(record.get("coordinateRepresentation")));
                setAreErrorMessagesShown(Boolean.parseBoolean(record.get("areErrorMessagesShown")));
                setErrorMessageRemoveDelay(Integer.parseInt(record.get("errorMessageRemoveDelay")));
                setCenterPanelMargin(Integer.parseInt(record.get("centerPanelMargin")));
                setDumpingSiteDetectionRadius(Integer.parseInt(record.get("dumpingSiteDetectionRadius")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean getIsLoggingEnabled() {
        return isLoggingEnabled;
    }

    public static void setIsLoggingEnabled(boolean isLoggingEnabled) {
        isLoggingEnabledCheckbox.setSelected(isLoggingEnabled);
        SettingsPanel.isLoggingEnabled = isLoggingEnabled;
    }

    public static String getLoggingFile() {
        return loggingFile;
    }

    public static void setLoggingFile(String loggingFile) {
        loggingFilePathDisplayArea.setText(loggingFile);
        SettingsPanel.loggingFile = loggingFile;
    }

    public static int getMapMovementSpeed() {
        return mapMovementSpeed;
    }

    public static void setMapMovementSpeed(int mapMovementSpeed) {
        mapMovementSpeedSlider.setValue(mapMovementSpeed);
        SettingsPanel.mapMovementSpeed = mapMovementSpeed;
    }

    public static boolean getIsTutorialShownOnStartup() {
        return isTutorialShownOnStartup;
    }

    public static void setIsTutorialShownOnStartup(boolean isTutorialShownOnStartup) {
        isTutorialShownOnStartupCheckbox.setSelected(isTutorialShownOnStartup);
        SettingsPanel.isTutorialShownOnStartup = isTutorialShownOnStartup;
    }

    public static CoordinateRepresentation getCoordinateRepresentation() {
        return coordinateRepresentation;
    }

    public static void setCoordinateRepresentation(CoordinateRepresentation coordinateRepresentation) {
        if(coordinateRepresentation == CoordinateRepresentation.DMS){
            dmsButton.setSelected(true);
            decimalButton.setSelected(false);
        } else if(coordinateRepresentation == CoordinateRepresentation.DECIMAL){
            dmsButton.setSelected(false);
            decimalButton.setSelected(true);
        }
        SettingsPanel.coordinateRepresentation = coordinateRepresentation;
        MenuPanel.updateTable();
        try {
            if(MainPanel.mapViewer != null){
                MainPanel.mapViewer.update();
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean getAreErrorMessagesShown() {
        return areErrorMessagesShown;
    }

    public static void setAreErrorMessagesShown(boolean areErrorMessagesShown) {
        areErrorMessagesShownCheckbox.setSelected(areErrorMessagesShown);
        SettingsPanel.areErrorMessagesShown = areErrorMessagesShown;
    }

    public static int getErrorMessageRemoveDelay() {
        return errorMessageRemoveDelay;
    }

    public static void setErrorMessageRemoveDelay(int errorMessageRemoveDelay) {
        errorMessageRemoveDelaySlider.setValue(errorMessageRemoveDelay);
        SettingsPanel.errorMessageRemoveDelay = errorMessageRemoveDelay;
    }

    public static int getCenterPanelMargin() {
        return centerPanelMargin;
    }

    public static void setCenterPanelMargin(int centerPanelMargin) {
        centerPanelMarginSlider.setValue(centerPanelMargin);
        MapOverlayPanel.updateCenterPanelMargin(centerPanelMargin);
        SettingsPanel.centerPanelMargin = centerPanelMargin;
    }

    public static int getDumpingSiteDetectionRadius() {
        return dumpingSiteDetectionRadius;
    }

    public static void setDumpingSiteDetectionRadius(int dumpingSiteDetectionRadius) {
        dumpingSiteDetectionRadiusSlider.setValue(dumpingSiteDetectionRadius);
        SettingsPanel.dumpingSiteDetectionRadius = dumpingSiteDetectionRadius;
    }
}
