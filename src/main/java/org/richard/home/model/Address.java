package org.richard.home.model;

import java.util.Objects;

public class Address {

    private long id;
    private String city;
    private String street;
    private String plz;
    private Country country;

    public long getId() {
        return id;
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
        this(Double.valueOf(Math.random()).longValue(), city, street, plz, country);
    }

    public Address(long id, String city, String street, String plz, Country country) {
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
        return Objects.equals(city, address.city) &&
                Objects.equals(street, address.street) &&
                Objects.equals(plz, address.plz) &&
                country == address.country;
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, plz, country);
    }

    @Override
    public String toString() {
        return "Address{" +
                "city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", plz='" + plz + '\'' +
                ", country=" + country +
                '}';
    }
}
