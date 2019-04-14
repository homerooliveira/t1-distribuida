package com.pucrs.distribuida;

import java.util.List;

public class Response {
    private int status;
    private List<File> files;

    public Response(int status, List<File> files) {
        this.status = status;
        this.files = files;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", files=" + files +
                '}';
    }
}
