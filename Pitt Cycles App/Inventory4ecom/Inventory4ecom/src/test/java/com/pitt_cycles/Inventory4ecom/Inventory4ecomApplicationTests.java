package com.pitt_cycles.Inventory4ecom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
@SpringBootTest
class Inventory4ecomApplicationTests {

	@Test
	void contextLoads() {
	}




	public class AppTest {

		static WebDriver browser;

		@BeforeAll
		static void setup() {

			WebDriverManager.chromedriver().setup();

			ChromeOptions options = new ChromeOptions();
			options.setHeadless(false);
			options.addArguments("start-maximized"); // open Browser in maximized mode
			options.addArguments("disable-infobars"); // disabling infobars
			options.addArguments("--disable-extensions"); // disabling extensions
			options.addArguments("--disable-gpu"); // applicable to Windows os only
			options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
			options.addArguments("--no-sandbox"); // Bypass OS security model
			options.addArguments("--disable-in-process-stack-traces");
			options.addArguments("--disable-logging");
			options.addArguments("--log-level=3");
			options.addArguments("--remote-allow-origins=*");

			browser = new ChromeDriver(options);
		}

		@Test
		@DisplayName("The google.com web site should have the correct title")
		void testProjectWebSiteShouldHaveCorrectTitle() {
			browser.get("https://google.com/");
			Assertions.assertEquals("Google", browser.getTitle());
		}
	}

}
