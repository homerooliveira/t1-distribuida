package com.pucrs.distribuida;

public class File {
    private String name;
    private String hash;
    private String ip;

    public File(String name, String hash, String ip) {
        this.name = name;
        this.hash = hash;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + name + '\'' +
                ", hash='" + hash + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}