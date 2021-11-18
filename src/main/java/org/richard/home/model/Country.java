package org.richard.home.model;

public enum Country {
    GERMANY("GERMANY"), AUSTRIA("AUSTRIA"), SWISS("SWISS");

    private String name;

    Country(String name) {
        this.name = name;
    }

    public String getValue(){
        return this.name;
    }
}
