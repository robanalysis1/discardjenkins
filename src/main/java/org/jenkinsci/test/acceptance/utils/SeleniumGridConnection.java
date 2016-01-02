package org.jenkinsci.test.acceptance.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
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
        // Saving browser sessionID to mysql database

        String dburl = "jdbc:mysql://localhost:3306/jenkins_core_sessionIDs";
        String uname = "root";
        String dropQuery = "DROP TABLE IF EXISTS test_session_ids";
        String sqlQuery = "CREATE TABLE IF NOT EXISTS test_session_ids("
                + "id int unsigned auto_increment not null,"
                + "session_id VARCHAR(60) not null,"
                + "date_created VARCHAR(100) not null,"
                + "primary key(id)"
                + ")";
        String tblQuery = "INSERT INTO test_session_ids(date_created, session_id)" + "VALUES" + "(?, ?)";

        //Get time stamp
        Calendar calendar = Calendar.getInstance();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(calendar.getTime().getTime());
        java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            //Register the JDBC Driver Class
            Class.forName("com.mysql.jdbc.Driver");
            //Create the JDBC connection

            conn = DriverManager.getConnection(dburl, uname, "");
            ps = conn.prepareStatement(sqlQuery);
            ps.execute();
            conn = DriverManager.getConnection(dburl,uname, "");
            ps = conn.prepareStatement(tblQuery);
            ps.setString(1, String.valueOf(timestamp));
            ps.setString(2, sessionId);
            ps.execute();

        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(conn != null)
                {

                    ps.close();
                    //Close the JDBC Connection
                    conn.close();

                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        // Writing to file and saving it to dropbox
        try {
            FileWriter fileWriter = new FileWriter("/home/adi/Dropbox/TestResults/Moodle/sessionIds.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("testDBsessionID: " + sessionId);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            // execption handling
        }

        return remoteWebDriver;
    }
}