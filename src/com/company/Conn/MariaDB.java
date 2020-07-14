package com.company.Conn;

import com.company.Constants.Config;
import com.company.Helpers.TalkerHelper;

import java.sql.Connection;
import java.sql.DriverManager;

public class MariaDB {

    /**
     * Returns a MariaDB Connection
     * @return
     */
    public static Connection getMariaDBConnection() {
        Connection conn = null;

        try {
            Class.forName("org.mariadb.jdbc.Driver");

            conn = DriverManager.getConnection(
                    Config.DB_SERVER_URL, Config.DB_USER, Config.DB_PASS);
        }
        catch (Exception e) {
            TalkerHelper talkerHelper = TalkerHelper.getInstance();
            talkerHelper.talkJavaError("MariaDB", e);
        }

        return conn;

    }

}
