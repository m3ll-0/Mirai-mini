package com.company.Factories;

import com.company.Constants.DAOTypes;
import com.company.Interfaces.VulnerableDAO;
import com.company.DAOObjects.VulnerableDAOMariaDBImpl;
import com.company.Interfaces.DAOFactory;

public class VulnerableDAOFactory implements DAOFactory {

    @Override
    public VulnerableDAO getVulnerableDAO(DAOTypes DAOType) {

        switch(DAOType)
        {
            case MARIADB:
                return new VulnerableDAOMariaDBImpl();
        }

        return null;
    }
}
