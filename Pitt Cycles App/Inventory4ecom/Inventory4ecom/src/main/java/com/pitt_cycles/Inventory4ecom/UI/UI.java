package com.pitt_cycles.Inventory4ecom.UI;


import com.pitt_cycles.Inventory4ecom.excelmanagement.CreateFiles;
import com.pitt_cycles.Inventory4ecom.excelmanagement.ExcelToDatabase;
import com.pitt_cycles.Inventory4ecom.webDriver.LoginAutomation;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UI extends JFrame {
    ExcelToDatabase parseAndUploadExcel = new ExcelToDatabase();

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5433/postgres";;
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "postgres";

    private JLabel statusLabel;

    public void ExcelUploaderSwing() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // UI Components
        JTextField usernameButton = new JTextField();
        JTextField passwordButton = new JTextField();
        JButton uploadButton = new JButton("1)Upload Excel");
        JButton scrapeButton = new JButton("Run Web Scraper");
        JButton create = new JButton("Create Files for Polaris");
        statusLabel = new JLabel("Status: ");

        // Event handling
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadExcel();
            }
        });

        scrapeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    runWebScraper();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        );

        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // Layout
        setLayout(new FlowLayout());
        add(uploadButton);
        add(create);
        add(scrapeButton);
        add(statusLabel);

        // Set frame properties
        setSize(600, 600);
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }

    private void uploadExcel() {
        JFileChooser fileChooser = getjFileChooser();

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Parse Excel and upload to PostgreSQL
                parseAndUploadExcel.parseAndUploadExcel(selectedFile);
                statusLabel.setText("Status: Excel uploaded successfully!");
            } catch (Exception e) {
                statusLabel.setText("Status: Error uploading Excel");
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Status: Excel upload canceled");
        }
    }

    private static JFileChooser getjFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().toLowerCase().endsWith(".xlsx") || file.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Excel Files (*.xlsx)";
            }
        });
        return fileChooser;
    }


    private void runWebScraper() throws SQLException, InterruptedException {
        CreateFiles files = new CreateFiles();
        LoginAutomation login = new LoginAutomation();

        login.login();
        login.loadFiles();



        files.PostgresToCSV();
        statusLabel.setText("Status: Running Web Scraper");


    }

}
