package com.company.Interfaces;

import com.company.Constants.DAOTypes;

public interface DAOFactory {

    public VulnerableDAO getVulnerableDAO(DAOTypes DAOType);
}
