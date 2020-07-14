package com.company.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IP {

    protected volatile List<String> IPList = new ArrayList<>();

    public IP()
    {

    }

    public synchronized void reset()
    {
        this.IPList.clear();
    }

    public synchronized int getLength()
    {
        return this.IPList.size();
    }

    public synchronized void add(String IP)
    {
        this.IPList.add(IP);
    }

    public synchronized String getAtOffset(int offset)
    {
        return this.IPList.get(offset);
    }

}
