package com.company.Helpers;

import com.company.Models.Vulnerable;

import java.util.ArrayList;
import java.util.List;

public class SSHVulnerableValidationHelper {

    private static volatile List<Vulnerable> SharedVulnerableSSHList = new ArrayList<>();

    public static synchronized void insert(Vulnerable vulnerable)
    {
        SharedVulnerableSSHList.add(vulnerable);
    }

    public static synchronized List<Vulnerable> getSharedVulnerableSSHList() {
        return SharedVulnerableSSHList;
    }

    public static synchronized void resetList() {
        SharedVulnerableSSHList.clear();
    }

}
