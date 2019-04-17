package com.pucrs.distribuida;

import java.util.ArrayList;
import java.util.List;

public class Response {
    private int status;
    private Node node;
    private ArrayList<File> files;
    private String fileRequasted;

    private String fileHash;
    private String fileName;
    private byte[] fileData;

    private String senderIp;

    // NODE_SEND_FILE_TO_NODE
    public Response(String fileName, byte[] fileData, String senderIp) {
        this.status = Constants.NODE_SEND_FILE_TO_NODE;
        this.fileName = fileName;
        this.fileData = fileData;
        this.senderIp = senderIp;
    }

    // NODE_REQUEST_FILE_TO_NODE
    public Response(String fileHash, String senderIp) {
        this.status = Constants.NODE_REQUEST_FILE_TO_NODE;
        this.fileHash = fileHash;
        this.senderIp = senderIp;
    }

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

    public String getFileHash() {
        return fileHash;
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

    public String getSenderIp() {
        return senderIp;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", node=" + node +
                '}';
    }
}
