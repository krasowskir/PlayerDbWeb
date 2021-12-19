package org.richard.home.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {
    private int id;

    private String name;
    private int budget;
    private byte[] logo;
    private String tla;
    private String address;
    private String phone;
    private String email;
    private String venue;
    private String website;
    private String owner;

    private JsonNode squad;

    public Team() {
    }

    public Team(int id, String name, int budget, byte[] logo, String tla, String address, String phone, String email, String venue, String website, String owner, JsonNode squad) {
        this.id = id;
        this.name = name;
        this.budget = budget;
        this.logo = logo;
        this.tla = tla;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.venue = venue;
        this.website = website;
        this.owner = owner;
        this.squad = squad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    @JsonProperty("founded")
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTla() {
        return tla;
    }

    public void setTla(String tla) {
        this.tla = tla;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public JsonNode getSquad() {
        return squad;
    }

    public void setSquad(JsonNode squad) {
        this.squad = squad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id == team.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", tla='" + tla + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", venue='" + venue + '\'' +
                ", website='" + website + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }
}
