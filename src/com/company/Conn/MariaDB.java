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
                    "jdbc:mariadb://" + Config.MARIADB_SERVER + "/" + Config.MARIADB_DATABASE, Config.MARIADB_USER, Config.MARIADB_PASS);
        }
        catch (Exception e) {
            TalkerHelper talkerHelper = TalkerHelper.getInstance();
            talkerHelper.talkJavaError("MariaDB", e);
            e.printStackTrace();
        }

        return conn;

    }

}
