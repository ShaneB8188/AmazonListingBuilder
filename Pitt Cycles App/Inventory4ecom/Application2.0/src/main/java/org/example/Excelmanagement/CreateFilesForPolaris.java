package org.example.Excelmanagement;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
public class CreateFilesForPolaris {


    public class PostgresToCSV {
        public static void main(String[] args) {
            String jdbcUrl = "jdbc:postgresql://localhost:5432/your_database_name";
            String username = "your_username";
            String password = "your_password";
            String columnName = "your_column_name"; // Replace with the name of the column you want
            String query = "SELECT " + columnName + " FROM your_table_name";
            String outputFolderPath = "Output"; // Folder where CSV files will be saved
            int batchSize = 1000;
            int totalFiles = 20; // Total number of CSV files

            // Configure database connection properties
            Properties props = new Properties();
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
                    String csvFilePath = outputFolderPath + File.separator + "data" + fileCount + ".csv";

                    try (ResultSet rs = stmt.executeQuery(query);
                         FileWriter fileWriter = new FileWriter(csvFilePath);
                         CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(columnName, "Quantity", "Alternate Part"))) {

                        // Skip rows based on the offset
                        for (int i = 0; i < offset; i++) {
                            rs.next();
                        }

                        while (rs.next() && rowCount < batchSize) {
                            String columnData = rs.getString(columnName);
                            // Add default values for Quantity and Alternate Part
                            String quantity = "99";
                            String alternatePart = "No";
                            csvPrinter.printRecord(columnData, quantity, alternatePart);
                            rowCount++;
                        }

                        csvPrinter.flush();
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
    }


}
