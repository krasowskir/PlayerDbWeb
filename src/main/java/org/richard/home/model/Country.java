package org.richard.home.model;

public enum Country {
    GERMANY("GERMANY"), AUSTRIA("AUSTRIA"), SWISS("SWISS"), FRANCE("FRANCE"),
    POLAND("POLAND"), ENGLAND("England"), GHANA("GHANA"), SPAIN("SPAIN"), LATVIA("LATVIA");

    private String name;

    Country(String name) {
        this.name = name;
    }

    public String getValue(){
        return this.name;
    }
}
