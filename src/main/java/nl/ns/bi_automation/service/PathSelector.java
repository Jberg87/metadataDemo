package nl.ns.bi_automation.service;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class PathSelector extends Component {

    public File selectFile() {

            File selectedFile = null;
            File newFile = null;

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());

                try {
                    newFile = Paths.get(selectedFile.getAbsolutePath()).toRealPath().toFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        return newFile;
    }

    public String selectPath() {

        File selectedDirectory = null;
        File newDirectory = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("select folder with all metdata files...");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = fileChooser.getSelectedFile();
            System.out.println("Selected directory: " + selectedDirectory.getAbsolutePath());

            try {
                newDirectory = Paths.get(selectedDirectory.getAbsolutePath()).toRealPath().toFile();
                System.out.println("New directory: " + newDirectory.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return newDirectory.getAbsolutePath();
    }

}
