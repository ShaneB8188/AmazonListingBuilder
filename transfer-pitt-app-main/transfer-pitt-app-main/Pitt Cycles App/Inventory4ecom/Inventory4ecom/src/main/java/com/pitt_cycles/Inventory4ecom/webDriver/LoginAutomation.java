package com.pitt_cycles.Inventory4ecom.webDriver;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class LoginAutomation {
    String dbUrl = "jdbc:postgresql://localhost:5433/postgres";
    String dbUser = "postgres";
    String dbPassword = "postgres";

        public void login(String userName, String password){
    WebDriverManager.chromedriver().setup();
    WebDriver driver = new ChromeDriver();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
    String dealerCode = "211500";

        driver.get("https://pga.polarisportal.com/Dealer/Items/Index");

        driver.findElement(By.id("F_DealerID")).sendKeys(dealerCode);
        driver.findElement(By.id("F_UserLogin")).sendKeys(userName);
        driver.findElement(By.id("F_Password")).sendKeys(password);


        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("F_Submit")));
        driver.findElement(By.id("F_Submit")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav_PG&A")));
        driver.findElement(By.id("nav_PG&A")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav_Item_Availability")));
        driver.findElement(By.id("nav_Item_Availability")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"itemDetailForm\"]/div[1]/div/div[1]/div[2]/div[1]/button")));
        driver.findElement(By.xpath("//*[@id=\"itemDetailForm\"]/div[1]/div/div[1]/div[2]/div[1]/button")).click();
}

    public void loadFiles() throws InterruptedException {
            WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));

        File file = new File("resources/Output");
        File[] directoryListing = file.listFiles();
        for (File child : directoryListing) {
            String filePath = file + "/" + child.getName();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"FileUpload_Files\"]")));
            driver.findElement(By.xpath("//*[@id=\"FileUpload_Files\"]")).sendKeys(filePath);
            driver.findElement(By.xpath("//*[@id=\"showmodal\"]/div/div/form/div[3]/button[1]")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"itemDetailForm\"]/div[1]/div/div[1]/div[2]/div[1]/button")));





            WebElement table = driver.findElement(By.id("tblItems"));

            // Find all rows in the table
            List<WebElement> rows = table.findElements(By.tagName("tr"));
            // Iterate through the rows, assuming the table has a header row
            for (int i = 1; i < rows.size(); i++) { // Start from index 1 to skip the header row
                WebElement row = rows.get(i);

                // Find the textbox in the current row (assuming it's in the first column)
                WebElement partNumber = row.findElement(By.id("txtItemNumber"));
                String textboxData = partNumber.getAttribute("value");
//                WebElement price = row.findElement(By.id("msrpValue"));
//                String priceData = price.getAttribute("msrpValue");

                // Find the second column in the current row (0-based index)
                List<WebElement> columns = row.findElements(By.tagName("td"));


                String qtyAvail = columns.get(5).getText(); // Second column (0-based index)
                if (qtyAvail.equals("")) {
                    qtyAvail = "Not Available";
                }
                insertDataIntoDatabase(dbUrl, dbUser, dbPassword, textboxData, qtyAvail);
                System.out.println("Part Number: " + textboxData);
                System.out.println("Qty Available: " + qtyAvail);
            }
            driver.findElement(By.xpath("//*[@id=\"itemDetailForm\"]/div[1]/div/div[1]/div[2]/div[1]/button")).click();
        }
    }
    private static void insertDataIntoDatabase(String dbUrl, String dbUser, String dbPassword, String partNumber, String qtyAvail) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String insertQuery = "INSERT INTO Polaris2Amazon (sku, quantity, price) VALUES (?, ?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, partNumber);
                preparedStatement.setString(2, qtyAvail);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


