package com.pucrs.distribuida;

import java.util.ArrayList;
import java.util.List;

public class Response {
    private int status;
    private Node node;
    private ArrayList<File> files;
    private String fileRequasted;

    public Response(int status) {
        this.status = status;
        this.node = null;
    }

    public Response(int status, Node node) {
        this.status = status;
        this.node = node;
    }

    public Response(int status, ArrayList<File> files) {
        this.status = status;
        this.files = files;
    }

    public Response(int status, String fileRequasted) {
        this.status = status;
        this.fileRequasted = fileRequasted;
    }

    public int getStatus() {
        return status;
    }

    public Node getNode() {
        return node;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public String getFileRequested() {
        return fileRequasted;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", node=" + node +
                '}';
    }
}
