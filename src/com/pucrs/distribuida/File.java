package com.pucrs.distribuida;

public class File {
    private String name;
    private String hash;

    public File(String name, String hash) {
        this.name = name;
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + name + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}