package org.richard.home.model;

import java.util.Objects;

public class Address {

    private int id;
    private String city;
    private String street;
    private String plz;
    private Country country;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Address(String city, String street, String plz, Country country) {
        this(Double.valueOf(Math.random()).intValue(), city, street, plz, country);
    }

    public Address(int id, String city, String street, String plz, Country country) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.plz = plz;
        this.country = country;
    }

    public Address() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return id == address.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", plz='" + plz + '\'' +
                ", country=" + country +
                '}';
    }
}
