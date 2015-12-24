package org.jenkinsci.test.acceptance.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by adini121 on 24/11/15.
 * This class enables us to create a remotewebdriver instance
 */
public class SeleniumGridConnection {
    public WebDriver createWebDriver (DesiredCapabilities cap) throws MalformedURLException {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/seleniumGrid.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = prop.getProperty("gridHubURL");
        System.out.print("grid URL is :" + url);
        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(new URL(url), cap);
        String sessionId = remoteWebDriver.getSessionId().toString();
        System.out.print("___________________________________________________________________________________");
        System.out.print("_________Session ID is_______________:" + sessionId);
        System.out.print("___________________________________________________________________________________");
        return remoteWebDriver;
    }
}