package org.richard.home.model;

import java.util.Objects;

public class Player {
    private String name;
    private int alter;

    public Player() {
    }

    public Player(String name, int alter) {
        this.alter = alter;
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return alter == player.alter &&
                Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alter, name);
    }

    @Override
    public String toString() {
        return "Player{" +
                "alter=" + alter +
                ", name='" + name + '\'' +
                '}';
    }
}
