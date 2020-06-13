package com.example.tictactoe.model;

public class User {
    private String name;
    private int points;
    private long partidasJugadas;

    public User() {
    }

    public User(String name, int points, long partidasJugadas) {
        this.name = name;
        this.points = points;
        this.partidasJugadas = partidasJugadas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(long partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }
}
