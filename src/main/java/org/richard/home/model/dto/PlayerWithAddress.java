package org.richard.home.model.dto;

public class PlayerWithAddress {

    private String name;
    private String age;
    private String city;
    private String street;
    private String plz;
    private String country;

    public PlayerWithAddress() {
    }

    public PlayerWithAddress(String name, String age, String city, String street, String plz, String country) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.street = street;
        this.plz = plz;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "PlayerWithAddress{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", plz='" + plz + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
