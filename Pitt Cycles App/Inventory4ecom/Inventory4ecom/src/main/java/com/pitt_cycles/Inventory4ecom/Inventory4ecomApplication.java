package com.pitt_cycles.Inventory4ecom;

import com.pitt_cycles.Inventory4ecom.UI.UI;
import com.pitt_cycles.Inventory4ecom.excelmanagement.ExcelToDatabase;
import com.pitt_cycles.Inventory4ecom.excelmanagement.CreateFiles;
import com.pitt_cycles.Inventory4ecom.webDriver.LoginAutomation;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.*;
import java.sql.*;

@SpringBootApplication
public class Inventory4ecomApplication {

    public static void main(String[] args) throws SQLException, AWTException, InterruptedException {
        ExcelToDatabase db = new ExcelToDatabase();
        LoginAutomation polaris = new LoginAutomation();
        CreateFiles create = new CreateFiles();
        //db.runDataBase();
        polaris.login();
        create.PostgresToCSV();
        polaris.loadFiles();
//        create.toAmazon();
// UI ui = new UI();
//        ui.ExcelUploaderSwing();
//
    }

}

