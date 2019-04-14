package com.pucrs.distribuida;

import java.util.Collections;
import java.util.List;

public final class Node {
    private final String ip;
    private final List<File> files;

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

    @Override
    public String toString() {
        return "Node{" +
                "ip='" + ip + '\'' +
                ", files=" + files +
                '}';
    }
}
