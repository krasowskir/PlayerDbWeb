package org.richard.home.model;

import java.util.Arrays;
import java.util.Objects;

public class Team {
    private int id;
    private String name;
    private int budget;
    private byte[] logo;
    private String owner;

    public Team() {
    }

    public Team(int id, String name, int budget, byte[] logo, String owner) {
        this.id = id;
        this.name = name;
        this.budget = budget;
        this.logo = logo;
        this.owner = owner;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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
                ", logo=" + Arrays.toString(logo) +
                ", owner='" + owner + '\'' +
                '}';
    }
}
