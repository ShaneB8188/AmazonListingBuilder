package org.example.UI;

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

public class UI extends JFrame {
        private static final String DATABASE_URL = "jdbc:postgresql://localhost:5433/postgres";;
        private static final String DATABASE_USER = "postgres";
        private static final String DATABASE_PASSWORD = "postgres";

        private JLabel statusLabel;

        public void ExcelUploaderSwing() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // UI Components
            JButton uploadButton = new JButton("Upload Excel");
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
                    runWebScraper();
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
                    parseAndUploadExcel(selectedFile);
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

        private void parseAndUploadExcel(File file) throws Exception {
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = WorkbookFactory.create(fis);
                 Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {

                Sheet sheet = workbook.getSheetAt(0);
                int rowsInserted = 0;

                // Remove existing data from the table
                String deleteSql = "DELETE FROM indian_inv; DELETE FROM polaris2amazon;";
                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                    deleteStatement.executeUpdate();
                }

                for (Row row : sheet) {
                    if (row.getRowNum() <= 1) {
                        // Skip the header rows
                        continue;
                    }
                    String asinValue = row.getCell(0).getStringCellValue();
                    String asin = String.valueOf(asinValue);



                    double skuValue = row.getCell(1).getNumericCellValue();
                    String sku = String.valueOf(skuValue);



                    double mpnValue = row.getCell(2).getNumericCellValue();
                    String mpn = String.valueOf(mpnValue);


                    String auction_titleValue = row.getCell(3).getStringCellValue();
                    String auction_title = String.valueOf(auction_titleValue);


                    double gtinValue = row.getCell(4).getNumericCellValue();
                    String gtin = String.valueOf(gtinValue);


                    String brandValue = row.getCell(5).getStringCellValue();
                    String brand = String.valueOf(brandValue);

                    String reporting_brandValue = row.getCell(6).getStringCellValue();
                    String reporting_brand = String.valueOf(reporting_brandValue);

                    double parent_skuValue = row.getCell(7).getNumericCellValue();
                    String parent_sku = String.valueOf(parent_skuValue);

                    double msrp_usdValue = row.getCell(8).getNumericCellValue();
                    String msrp_usd = String.valueOf(msrp_usdValue);


                    String isNewValue = row.getCell(9).getStringCellValue();
                    String isNew = String.valueOf(isNewValue);


                    double msrp_canValue = row.getCell(10).getNumericCellValue();
                    String msrp_can = String.valueOf(msrp_canValue);


                    String availCanValue = row.getCell(11).getStringCellValue();
                    String availCan = String.valueOf(availCanValue);


                    double ebay_listing_idValue = row.getCell(12).getNumericCellValue();
                    String ebay_listing_id = String.valueOf(ebay_listing_idValue);


                    // Create SQL INSERT statement
                    String sql = "INSERT INTO indian_inv (asin, sku, mpn, auction_title, gtin, brand, reporting_brand, parent_sku, msrp_usd, new,msrp_can, avail_can, ebay_listing_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setString(1, asin);
                        preparedStatement.setString(2, sku);
                        preparedStatement.setString(3, mpn);
                        preparedStatement.setString(4, auction_title);
                        preparedStatement.setString(5, gtin);
                        preparedStatement.setString(6, brand);
                        preparedStatement.setString(7, reporting_brand);
                        preparedStatement.setString(8, parent_sku);
                        preparedStatement.setString(9, msrp_usd);
                        preparedStatement.setString(10, isNew);
                        preparedStatement.setString(11,msrp_can);
                        preparedStatement.setString(12, availCan);
                        preparedStatement.setString(13, ebay_listing_id);

                        int affectedRows = preparedStatement.executeUpdate();
                        if (affectedRows > 0) {
                            rowsInserted++;
                        }
                    }
                }

                System.out.println(rowsInserted + " rows inserted into the database.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private void runWebScraper() {
            // Implement your web scraper logic here
            statusLabel.setText("Status: Running Web Scraper");


    }

}
