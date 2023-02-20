package org.madcomputerscientists;

import com.drew.imaging.ImageProcessingException;

import javax.swing.BorderFactory;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetEvent;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.InvocationTargetException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;

public class FileDropTarget implements DropTargetListener {
    public static final int BORDER_THICKNESS = 5;
    @Override
    public void dragEnter(DropTargetDragEvent event) {
        MainPanel.mapViewer.setBorder(BorderFactory.createLineBorder(Color.red, BORDER_THICKNESS));
    }

    @Override
    public void dragOver(DropTargetDragEvent event) { }

    @Override
    public void dropActionChanged(DropTargetDragEvent event) { }

    @Override
    public void dragExit(DropTargetEvent event) {
        MainPanel.mapViewer.setBorder(BorderFactory.createEmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));
    }

    @Override
    public void drop(DropTargetDropEvent event) {       //TODO only works for files and not for folders, implement folder drag and drop support
        MainPanel.mapViewer.setBorder(BorderFactory.createEmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));
        Main.mainFrame.setCursor(Cursor.WAIT_CURSOR);
        event.acceptDrop(DnDConstants.ACTION_COPY);

        Transferable transferable = event.getTransferable();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        for (DataFlavor flavor : flavors) {
            if (flavor.isMimeTypeEqual("application/x-java-file-list")) {
                try {
                    for (File directory : (List<File>) transferable.getTransferData(flavor)) {
                        for (File file : getAllFilesFromDirectory(directory)) {
                            ArrayList<File> files = new ArrayList<>();

                            files.add(file);
                            try {
                                MainPanel.mapViewer.addImageRecordLocationsByFile(files);
                            } catch (ImageProcessingException e) {
                                ErrorMessageHandler.addErrorMessage("File could not be processed");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            } catch (NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } catch (UnsupportedFlavorException e) {
                    ErrorMessageHandler.addErrorMessage("File flavor is not supported");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                event.dropComplete(true);
            }
        }
        Main.mainFrame.setCursor(Cursor.DEFAULT_CURSOR);
    }

    private ArrayList<File> getAllFilesFromDirectory(File directory) {
        ArrayList<File> files = new ArrayList<>();
        if (!directory.isDirectory()){
            files.add(directory);
            return files;
        }

        try {
            Files.walkFileTree(Path.of(directory.getAbsolutePath()), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                    files.add(path.toFile());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path path, IOException e) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return files;
    }
}
