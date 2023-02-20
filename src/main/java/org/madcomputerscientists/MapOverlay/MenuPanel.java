package org.madcomputerscientists.MapOverlay;

import com.drew.imaging.ImageProcessingException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.madcomputerscientists.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class MenuPanel extends JPanel {
    private JPanel selectionPanel = new JPanel();
    private JPanel contentPanel = new JPanel();
    private CardLayout cardLayout = new CardLayout();
    public static JTable table = null;
    private static ArrayList<String[]> data = new ArrayList<>();
    public JPanel listViewPanel = getListViewPanel();
    private static DefaultTableModel tableModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }
    };

    public MenuPanel() {
        this.setBackground(new Color(0xB5B5B5));
        this.setLayout(new BorderLayout());

        contentPanel.setLayout(cardLayout);

        contentPanel.add(getSettingsScrollPane(), "SettingsPanel");
        contentPanel.add(listViewPanel, "ListViewPanel");

        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "SettingsPanel");
        });

        JButton listViewButton = new JButton("ListView");
        listViewButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "ListViewPanel");
        });

        selectionPanel.add(settingsButton);
        selectionPanel.add(listViewButton);
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
        selectionPanel.setOpaque(false);
        contentPanel.setOpaque(false);

        this.add(selectionPanel, BorderLayout.WEST);
        this.add(contentPanel, BorderLayout.CENTER);
    }

    private JScrollPane getSettingsScrollPane(){
        JScrollPane scrollPane = new JScrollPane(new SettingsPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);     // vertical mouse wheel sensitivity while scrolling
        scrollPane.getHorizontalScrollBar().setUnitIncrement(30);     // horizontal mouse wheel sensitivity while scrolling
        scrollPane.setViewportBorder(null);
        return scrollPane;
    }

    public JPanel getListViewPanel(){
        JPanel listViewPanel = new JPanel();
        listViewPanel.setOpaque(false);
        listViewPanel.setLayout(new GridLayout(2,1));
        listViewPanel.setBorder(new EmptyBorder(10,10,10,10));
        listViewPanel.setLayout(new GridBagLayout());

        String[] columnNames = {"ID", "Latitude", "Longitude", "Type", "Contact"};

        for(String columnName : columnNames){
            tableModel.addColumn(columnName);
        }

        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setColumnSelectionAllowed(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(table.getTableHeader());
        tablePanel.add(table);

        JScrollPane scrollPane = new JScrollPane(tablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);     // vertical mouse wheel sensitivity while scrolling
        scrollPane.getHorizontalScrollBar().setUnitIncrement(30);     // horizontal mouse wheel sensitivity while scrolling
        scrollPane.setBorder(null);
        scrollPane.setViewportBorder(null);

        JButton importButton = new JButton("Import");
        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Choose the folder that contains the dumpingsites.txt file and the imageRecordLocations folder");
            fileChooser.setMultiSelectionEnabled(false);

            int returnCode = fileChooser.showOpenDialog(MenuPanel.this);
            if (returnCode == JFileChooser.APPROVE_OPTION) {
                loadDumpingSitesFromPath(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    MainPanel.mapViewer.update();
                } catch (InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                } catch (NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Choose a folder to save your dumping sites.");
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setApproveButtonText("Save");

            int returnCode = fileChooser.showOpenDialog(MenuPanel.this);
            if (returnCode == JFileChooser.APPROVE_OPTION) {
                try {
                    saveDumpingSitesToPath(fileChooser.getSelectedFile().getAbsolutePath() + "/Dumpingsites");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2 ,10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.8;
        listViewPanel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.2;
        listViewPanel.add(buttonPanel, gbc);

        return listViewPanel;
    }

    public static void addRowToListViewByDumpingSite(DumpingSite dumpingSite) {
        Coordinate centerCoordinate = dumpingSite.getCenterCoordinate();
        String latitude = "";
        String longitude = "";
        if(SettingsPanel.getCoordinateRepresentation() == CoordinateRepresentation.DMS){
            latitude = centerCoordinate.getLatitude_str();
            longitude = centerCoordinate.getLongitude_str();
        } else if(SettingsPanel.getCoordinateRepresentation() == CoordinateRepresentation.DECIMAL){
            latitude = centerCoordinate.getLatitude_dec_Formatted();
            longitude = centerCoordinate.getLongitude_dec_Formatted();
        }
        String[] newRow = {String.valueOf(data.size() + 1), latitude, longitude, dumpingSite.getDumpingSiteType().toString(), dumpingSite.getContact()};

        data.add(newRow);
        tableModel.addRow(newRow);
        for(int i = 0; i < table.getColumnCount(); i++){
            TableColumn column = table.getColumn(table.getColumnName(i));
            column.setPreferredWidth(getWidestCellInTableColumn(table, column) + 10);
        }
    }

    static String[] HEADERS = { "id", "latitude", "longitude", "type", "contact", "hasMoved", "imageRecordLocationRelativeFilePaths"};

    public static void saveDumpingSitesToPath(String dataFolderPath) throws IOException {
        File dataFolder = new File(dataFolderPath);
        if(!dataFolder.exists()){
            dataFolder.mkdir();
        }

        File dumpingSiteFile = new File(dataFolder + "/dumpingsites.txt");
        if(!dumpingSiteFile.exists()){
            dumpingSiteFile.createNewFile();
        }

        FileWriter out = new FileWriter(dumpingSiteFile);

        File imageRecordLocationDirectory = new File((dataFolder + "/imageRecordLocations"));

        if(!imageRecordLocationDirectory.exists()){
            imageRecordLocationDirectory.mkdir();
        }

        imageRecordLocationDirectory.mkdir();

        MapViewer map = MainPanel.mapViewer;
        ArrayList<DumpingSite> dumpingSites = map.getDumpingSites();

        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(HEADERS))) {

            int imageRecordLocationID = 1;

            for (int i = 0; i < dumpingSites.size(); i++) {
                ArrayList<String> imageRecordLocationRelativeFilePaths = new ArrayList<>();
                for(ImageRecordLocation irl : dumpingSites.get(i).getImageRecordLocations()){
                    File savedImage = new File(imageRecordLocationDirectory + "/" + irl.getFileName());
                    Files.copy(irl.getFile().toPath(), savedImage.toPath());
                    imageRecordLocationRelativeFilePaths.add(irl.getFileName());
                    imageRecordLocationID++;
                }
                DumpingSite ds = dumpingSites.get(i);
                printer.printRecord(i + 1,
                        ds.getCenterCoordinate().getLatitude_dec(),
                        ds.getCenterCoordinate().getLongitude_dec_Formatted(),
                        ds.getDumpingSiteType(),
                        ds.getContact(),
                        ds.hasMoved(),
                        imageRecordLocationRelativeFilePaths);
            }
        }
    }

    public static void loadDumpingSitesFromPath(String path) {
        try {
            Reader reader = Files.newBufferedReader(Path.of(path + "/dumpingsites.txt"));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader(HEADERS)
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord csvRecord : records) {
                Main.mainFrame.setCursor(Cursor.WAIT_CURSOR);
                double latitude = Double.parseDouble(csvRecord.get("latitude"));
                double longitude = Double.parseDouble(csvRecord.get("longitude"));
                DumpingSiteType type = DumpingSiteType.valueOf(csvRecord.get("type"));
                String contact = csvRecord.get("contact");
                boolean hasMoved = Boolean.parseBoolean(csvRecord.get("hasMoved"));

                String[] imageRecordLocationRelativeFilePaths = csvRecord.get("imageRecordLocationRelativeFilePaths").
                        replaceAll("\\[", "").
                        replaceAll("\\]", "").
                        replaceAll(" ", "").
                        split(",");

                ArrayList<ImageRecordLocation> imageRecordLocations = new ArrayList<>();
                for(String s : imageRecordLocationRelativeFilePaths){
                    String filePath = path + "/imageRecordLocations/" + s;
                    imageRecordLocations.add(new ImageRecordLocation(new File(filePath)));
                }

                MapViewer map = MainPanel.mapViewer;

                DumpingSite dumpingSite = new DumpingSite();
                dumpingSite.setDumpingSiteType(type);
                dumpingSite.setContact(contact);
                dumpingSite.setHasMoved(hasMoved);

                for(ImageRecordLocation irl : imageRecordLocations){
                    dumpingSite.addImageRecordLocation(irl);
                }
                dumpingSite.setCenterCoordinate(new Coordinate(latitude, longitude));
                map.addDumpingSite(dumpingSite);
                Main.mainFrame.setCursor(Cursor.DEFAULT_CURSOR);
            }
        } catch (IOException e) {
            ErrorMessageHandler.addErrorMessage("Selected folder does not contain data about dumpingsites!");
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ImageProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getWidestCellInTableColumn(JTable table, TableColumn column){
        int maxWidth = 0;
        for(int i = 0; i < table.getRowCount(); i++){
            TableCellRenderer renderer = table.getCellRenderer(i, column.getModelIndex());
            Component cell = renderer.getTableCellRendererComponent(table, table.getValueAt(i, column.getModelIndex()), false, false, i, column.getModelIndex());
            int width = cell.getPreferredSize().width;
            maxWidth = width > maxWidth ? width : maxWidth;
        }

        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        Component header = renderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, 0);
        int headerWidth = header.getPreferredSize().width;
        return headerWidth > maxWidth ? headerWidth : maxWidth;
    }

    public static void setTableData(int row, int column, String value){
        data.get(row)[column] = value;
        table.setValueAt(value, row, column);

        TableColumn tableColumn = table.getColumn(table.getColumnName(column));
        tableColumn.setPreferredWidth(getWidestCellInTableColumn(table, tableColumn) + 10);
    }

    public static void updateTable(){
        if(table != null && MainPanel.mapViewer != null){
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);   //clear all entries
            data.clear();
            for(DumpingSite dumpingSite : MainPanel.mapViewer.getDumpingSites()){
                addRowToListViewByDumpingSite(dumpingSite);
            }
        }
    }
}
