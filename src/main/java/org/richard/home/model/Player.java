package org.richard.home.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {
    private int id;
    private String name;
    private int alter;
    private String position;
    private LocalDate dateOfBirth;
    private Country countryOfBirth;

    public Player() {
    }

    public Player(int id, String name, int alter, String position, LocalDate dateOfBirth, Country countryOfBirth) {
        this.id = id;
        this.name = name;
        this.alter = alter;
        this.position = position;
        this.dateOfBirth = dateOfBirth;
        this.countryOfBirth = countryOfBirth;
    }

    public int getAlter() {
        return alter;
    }

    public void setAlter(int alter) {
        this.alter = alter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        //1995-07-14T00:00:00Z
        this.dateOfBirth = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Period diff = Period.between(this.dateOfBirth, LocalDate.now());
        this.setAlter(diff.getYears());
    }

    public Country getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = Country.valueOf(countryOfBirth.toUpperCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", countryOfBirth=" + countryOfBirth +
                ", alter=" + alter +
                '}';
    }
}
