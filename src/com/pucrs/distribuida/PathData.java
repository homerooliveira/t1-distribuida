package com.pucrs.distribuida;

import java.nio.file.Path;
import java.util.Arrays;

public final class PathData {
    Path path;
    byte[] data;
    String hash;

    public PathData(Path path, byte[] data) {
        this(path, data, "");
    }

    public PathData(Path path, byte[] data, String hash) {
        this.path = path;
        this.data = data;
        this.hash = hash;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "PathData{" +
                "path=" + path +
                ", data=" + Arrays.toString(data) +
                ", hash='" + hash + '\'' +
                '}';
    }
}