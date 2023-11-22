package com.pitt_cycles.Inventory4ecom.excelmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;

public class ExcelToDatabase {
    DataFormatter formatter = new DataFormatter();
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5433/postgres";;
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "postgres";


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


    public void parseAndUploadExcel(File file) throws Exception {
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



                Cell asinValue = row.getCell(0);
                String asin = formatter.formatCellValue(asinValue);


                Cell skuValue = row.getCell(1);
                String sku = formatter.formatCellValue(skuValue);



                Cell mpnValue = row.getCell(2);
                String mpn = formatter.formatCellValue(mpnValue);


                Cell auction_titleValue = row.getCell(3);
                String auction_title = formatter.formatCellValue(auction_titleValue);


                Cell gtinValue = row.getCell(4);
                String gtin = formatter.formatCellValue(gtinValue);


                Cell brandValue = row.getCell(5);
                String brand = formatter.formatCellValue(brandValue);

                Cell reporting_brandValue = row.getCell(6);
                String reporting_brand = formatter.formatCellValue(reporting_brandValue);

                Cell parent_skuValue = row.getCell(7);
                String parent_sku = formatter.formatCellValue (parent_skuValue);

                Cell msrp_usdValue = row.getCell(8);
                String msrp_usd = formatter.formatCellValue (msrp_usdValue);


                Cell isNewValue = row.getCell(9);
                String isNew = formatter.formatCellValue (isNewValue);


                Cell msrp_canValue = row.getCell(10);
                String msrp_can = formatter.formatCellValue(msrp_canValue);


                Cell availCanValue = row.getCell(11);
                String availCan = formatter.formatCellValue(availCanValue);


                Cell ebay_listing_idValue = row.getCell(12);
                String ebay_listing_id = formatter.formatCellValue(ebay_listing_idValue);


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
}




