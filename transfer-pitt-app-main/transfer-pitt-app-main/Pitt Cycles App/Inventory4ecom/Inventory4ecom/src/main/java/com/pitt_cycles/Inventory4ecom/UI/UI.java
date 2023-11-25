package com.pitt_cycles.Inventory4ecom.UI;

import com.pitt_cycles.Inventory4ecom.excelmanagement.CreateFiles;
import com.pitt_cycles.Inventory4ecom.excelmanagement.ExcelToDatabase;
import com.pitt_cycles.Inventory4ecom.webDriver.LoginAutomation;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

public class UI extends JFrame {
    ExcelToDatabase parseAndUploadExcel = new ExcelToDatabase();

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5433/postgres";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "postgres";

    private JTextArea dataTextArea;
    private JScrollPane dataScrollPane;
    private JComboBox<String> tableComboBox;

    public void ExcelUploaderSwing() {
        SwingUtilities.invokeLater(() -> {
            try {
                initComponents();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    private void initComponents() throws SQLException {
        // UI Components
        JTextField usernameButton = new JTextField();
        JTextField passwordButton = new JTextField();
        JButton uploadButton = new JButton("Upload Excel Spreadsheet");
        JButton scrapeButton = new JButton("Run Web Scraper");
        JButton create = new JButton("Create Files for Polaris Dex Upload");
        JButton amazon = new JButton("Create Amazon Listing File");

        // Database ComboBox
        String[] tableOptions = {"indian_inv", "polaris2amazon"};
        tableComboBox = new JComboBox<>(tableOptions);
        tableComboBox.setSelectedIndex(0);

        System.out.println("DEBUG: tableComboBox = " + tableComboBox);

        dataTextArea = new JTextArea(10, 30);
        dataScrollPane = new JScrollPane(dataTextArea);
        dataScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Event handling
        uploadButton.addActionListener(e -> uploadExcel());

        scrapeButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(UI.this, "Enter Polaris Username:");
            String password = JOptionPane.showInputDialog(UI.this, "Enter Polaris Password:");

            if (username != null && password != null) {
                try {
                    runWebScraper(username, password);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        create.addActionListener(e -> {
            CreateFiles createFiles = new CreateFiles();
            createFiles.PostgresToCSV();
        });

        amazon.addActionListener(e -> {
            CreateFiles createFiles = new CreateFiles();
            createFiles.toAmazon();
        });

        // Database ComboBox event handling
        tableComboBox.addActionListener(e -> getSelectedTable());

        tableComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the status label or perform any other actions based on the selected database
                try {
                    refreshData();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Layout
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(uploadButton);
        buttonPanel.add(create);
        buttonPanel.add(scrapeButton);

        setLayout(new BorderLayout());
        add(tableComboBox, BorderLayout.NORTH);
        add(dataScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set frame properties
        setTitle("Database Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);

        refreshData();
    }

    private void refreshData() throws SQLException {
        if (tableComboBox != null) {
            String selectedTable = getSelectedTable();
            String tableData = getDataFromTable(selectedTable);
            dataTextArea.setText(tableData);
        } else {
            System.err.println("ERROR: tableComboBox is null");
        }
    }

    private String getDataFromTable(String tableName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            String sql = "SELECT * FROM " + tableName;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    StringBuilder result = new StringBuilder();

                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        result.append(metaData.getColumnName(i)).append("\t");
                    }
                    result.append("\n");

                    while (resultSet.next()) {
                        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            result.append(resultSet.getString(i)).append("\t");
                        }
                        result.append("\n");
                    }
                    return result.toString();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error retrieving data from the database";
        }
    }

    private void uploadExcel() {
        JFileChooser fileChooser = getjFileChooser();

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Parse Excel and upload to PostgreSQL
                parseAndUploadExcel.parseAndUploadExcel(selectedFile);

            } catch (Exception e) {

                e.printStackTrace();
            }
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

    private void runWebScraper(String username, String password) throws SQLException, InterruptedException {
        CreateFiles files = new CreateFiles();
        LoginAutomation login = new LoginAutomation();
            login.login(username, password);
            login.loadFiles();
            files.toAmazon();
    }


    private String getSelectedTable() {
        return (String) tableComboBox.getSelectedItem();
    }
}