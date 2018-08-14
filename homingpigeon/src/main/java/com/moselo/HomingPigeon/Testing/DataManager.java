package com.moselo.HomingPigeon.Testing;

public class DataManager {

    private static DataManager manager;

    public static DataManager getInstance(){
        if (null == manager){
            manager = new DataManager();
        }

        return manager;
    }
}
