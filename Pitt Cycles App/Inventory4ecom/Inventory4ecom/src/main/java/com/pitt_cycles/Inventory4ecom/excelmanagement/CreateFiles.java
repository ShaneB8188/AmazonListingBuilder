package com.pitt_cycles.Inventory4ecom.excelmanagement;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class CreateFiles {
    String jdbcUrl = "jdbc:postgresql://localhost:5433/postgres";
    String username = "postgres";
    String password = "postgres";
    Properties props = new Properties();
    String outputFolderPath = "Amazon";

    public void PostgresToCSV() {

        String columnName = "sku";
        String query = "SELECT " + columnName + " FROM indian_inv where brand = 'Indian Motorcycle' and reporting_brand = 'IND';";
        String outputFolderPath = "Output"; // Folder where CSV files will be saved


        int batchSize = 15;//changes amount of items per csv file
        int totalFiles = 683;//number of csv files
        //totalFiles x batchSize should be 10,245
        //Polaris Item Availability can only handle so many items per upload

        // Configure database connection properties
        props.setProperty("user", username);
        props.setProperty("password", password);

        int rowCount = 0;
        int fileCount = 1;
        int offset = 0;

        try (Connection conn = DriverManager.getConnection(jdbcUrl, props);
             Statement stmt = conn.createStatement()) {

            // Create the output folder if it doesn't exist
            File outputFolder = new File(outputFolderPath);
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }

            while (fileCount <= totalFiles) {
                String csvFilePath = outputFolderPath + File.separator + "4Polaris" + fileCount + ".csv";

                try (ResultSet rs = stmt.executeQuery(query);
                     CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath))) {

                    // Skip rows based on the offset
                    for (int i = 0; i < offset; i++) {
                        rs.next();
                    }

                    while (rs.next() && rowCount < batchSize) {
                        DataFormatter formatter = new DataFormatter();
                        String columnData = rs.getString(columnName);
                        // Add default values for Quantity and Alternate Part
                        String quantity = "99";
                        String alternatePart = "No";
                        csvWriter.writeNext(new String[]{columnData, quantity, alternatePart});
                        rowCount++;
                    }

                    csvWriter.flush();
                    System.out.println("Data from column '" + columnName + "' exported to " + csvFilePath);

                    // Reset rowCount and increment the fileCount
                    rowCount = 0;
                    fileCount++;
                    offset += batchSize; // Update the offset for the next file

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toAmazon() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, props);
             Statement stmt = conn.createStatement()) {

            // Create the output folder if it doesn't exist
            File outputFolder = new File(outputFolderPath);
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }

            String csvFilePath = outputFolderPath + File.separator + "combined_data.csv";

            try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath))) {
                // Write header row
                csvWriter.writeNext(new String[]{"SKU", "Price", "Quantity", "Product_ID", "Product-ID-Type", "Condition-Type", "Condition-Note"});

                // Query and retrieve data from the joined tables
                String query = "select sku, msrp_usd, quantity, asin from indian_inv join polaris2amazon using (sku) where cast(msrp_usd as decimal) > 20 and quantity != 'Not Available' and cast (quantity as int) > 10;";

                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        String sku = rs.getString("sku");
                        String price = rs.getString("msrp_usd");
                        String quantity = rs.getString("quantity");
                        String productId = rs.getString("asin");

                        // Write data with default values for additional columns
                        csvWriter.writeNext(new String[]{sku, price, quantity, productId, "ASIN", "new", "new"});
                    }
                }

                csvWriter.flush();
                System.out.println("Data exported to " + csvFilePath);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}






