package com.company.Threads;

import com.company.Constants.DAOTypes;
import com.company.Interfaces.VulnerableDAO;
import com.company.Interfaces.DAOFactory;
import com.company.Factories.VulnerableDAOFactory;
import com.company.Helpers.TalkerHelper;
import com.company.Models.Vulnerable;

public class ReporterThread implements Runnable {

    private Vulnerable vulnerable;
    private DAOFactory daoFactory;
    private TalkerHelper talkerHelper;
    private String className;

    public ReporterThread(Vulnerable vulnerable)
    {
        this.vulnerable = vulnerable;
        this.daoFactory = new VulnerableDAOFactory();
        this.className = ReporterThread.class.getSimpleName();
        this.talkerHelper = TalkerHelper.getInstance();
    }

    @Override
    public void run() {
        process();
    }

    private void process()
    {
        this.insertVulnerableIntoDB();
    }

    private void insertVulnerableIntoDB()
    {
        this.talkerHelper.talkDebug(className, "Trying to save record into database for server " + vulnerable.getServer());

        VulnerableDAO vulnerableDAO = this.daoFactory.getVulnerableDAO(DAOTypes.MARIADB);
        vulnerableDAO.insert(vulnerable);
    }
}
