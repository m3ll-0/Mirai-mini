package com.company.DAOObjects;

import com.company.Conn.MariaDB;
import com.company.Helpers.TalkerHelper;
import com.company.Interfaces.VulnerableDAO;
import com.company.Models.Vulnerable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class VulnerableDAOMariaDBImpl implements VulnerableDAO {

    private TalkerHelper talkerHelper;
    private String className;

    public VulnerableDAOMariaDBImpl()
    {
        this.talkerHelper = TalkerHelper.getInstance();
        this.className = VulnerableDAOMariaDBImpl.class.getSimpleName();
    }

    @Override
    public void insert(Vulnerable vulnerable) {

        // Concrete MariaDB implementation
        Connection conn = MariaDB.getMariaDBConnection();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            String sql = "INSERT INTO Vulnerable VALUES (" +
                    "'"+vulnerable.getServer() + "'," +
                    "'"+vulnerable.getUser() + "'," +
                    "'"+vulnerable.getPassword() + "'," +
                    "'"+vulnerable.getProtocol_type() + "'," +
                    "'"+vulnerable.getTime_discovered() + "');";

            stmt.execute(sql);
            talkerHelper.talkGreatSuccess(className, "Inserted record into database for server " + vulnerable.getServer() +"!");

        } catch (SQLException e) {
            talkerHelper.talkJavaError("MariaDB", e);
        } finally {
            if (stmt != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    talkerHelper.talkJavaError("MariaDB", e);
                }
            }
        }
    }
}
