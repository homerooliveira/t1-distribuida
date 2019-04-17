package com.pucrs.distribuida;

import java.util.Collections;
import java.util.List;

public final class Node {
    private final String ip;
    private final List<File> files;
    private int lifeCount = 2;

    public Node(String ip, List<File> files) {
        this.ip = ip;
        this.files = files;
    }

    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public String getIp() {
        return ip;
    }

    public int getLifeCount() {
        return lifeCount;
    }

    public void decreaseLifeCount() {
        lifeCount -= 1;
    }

    public boolean isAlive() {
        return lifeCount > 0;
    }

    public void keepAlive() {
        lifeCount = 2;
    }

    @Override
    public String toString() {
        return "Node{" +
                "ip='" + ip + '\'' +
                ", files=" + files +
                '}';
    }
}
