/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.model;

import java.util.ArrayList;

/**
 *
 * @author Dan
 */
public class SchedList {
    private ArrayList<SchedEntry> schedList;

    public SchedList() {
        this.schedList = new ArrayList<>();
    }

    public ArrayList<SchedEntry> getSchedTable() {
        return schedList;
    }

    public void setSchedTable(ArrayList<SchedEntry> schedTable) {
        this.schedList = schedTable;
    }
    
    public void put(SchedEntry entry){
        this.schedList.add(entry);
    }
    
    
}
