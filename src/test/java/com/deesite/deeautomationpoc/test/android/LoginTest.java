package com.deesite.deeautomationpoc.test.android;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

public class LoginTest {
	WebDriver driver;
	static Logger logger = LoggerFactory.getLogger(LoginTest.class);

	@BeforeClass
	public void setUp() throws MalformedURLException {
		// File app= new
		// File("C:\\Users\\darora\\eclipse-workspace\\dee-poc-test-automation\\apps\\android-apks\\stage\\METOpenLink-STAGE-release.apk");
		// Set up desired capabilities and pass the Android app-activity and app-package
		// to Appium
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("deviceName", "LenovoK8");
		capabilities.setCapability("platformVersion", "7.1.2");
		capabilities.setCapability("platformName", "android");
		capabilities.setCapability("appPackage", "com.milwaukeetool.mymilwaukee_STAGE");
		// Below is launcher activity of the app (we can get it from apk info app)
		capabilities.setCapability("appActivity", "com.milwaukeetool.mymilwaukee.activity.RouterActivity");																											// app (you
		// capabilities.setCapability("app", app.getAbsolutePath());
		// capabilities.setCapability("newCommandTimeout", 3000);
		// capabilities.setCapability("version", "osVersion");
		// capabilities.setCapability("udid", "deviceId");
		// capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
		driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
	}

	@Test
	public void login_ValidCredentials_Success() throws Exception {
		driver.findElement(By.id("tvSignIn")).click();
		logger.info("Successflly clicked on <b>Sign In</b> button.");
		driver.findElement(By.id("inputEmail")).sendKeys("vmalik@dminc.com");
		logger.debug("Successflly entered <b>vmalik@dminc.com</b> in Email field.");
		driver.findElement(By.id("inputPassword")).sendKeys("miP4cvma");
		logger.info("Successflly entered <b>miP4cvma</b> in Password field.");
		driver.findElement(By.id("signInButton")).click();
		logger.info("Successflly clicked on <b>Sign In</b> button.");
		
		logger.trace("This is a trace");
		logger.debug("This is a debug");
		logger.info("This is a info");
		logger.warn("This is a warning");
		logger.error("This is an error");
	}

	@AfterClass
	public void teardown() {
		// close the app
		driver.quit();
	}

}

