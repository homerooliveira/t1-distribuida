package com.pucrs.distribuida;

import java.util.ArrayList;

public class ResponseRequest {
    private int status;
    private Node node;
    private ArrayList<Node> nodes;
    private ArrayList<File> files;
    private String fileHash;
    private String fileName;
    private byte[] fileData;
    private String senderIp;
    private String requestIdentifier;

    public ResponseRequest(){}

    public int getStatus() { return status; }
    public void setStatus(int status) {
        this.status = status;
    }

    public Node getNode() { return node; }
    public void setNode(Node node) {
        this.node = node;
    }

    public ArrayList<Node> getNodes() { return nodes; }
    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<File> getFiles() {  return files; }
    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getFileName() { return this.fileName; }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() { return this.fileData; }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getSenderIp() { return senderIp; }
    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }

    public String getRequestIdentifier() { return requestIdentifier; }
    public void setRequestIdentifier(String requestIdentifier) {
        this.requestIdentifier = requestIdentifier;
    }

    public ResponseRequest(int status, Node node) {
        this.status = status;
        this.node = node;
    }

    public ResponseRequest(int status, ArrayList<File> files) {
        this.status = status;
        this.files = files;
    }

    @Override
    public String toString() {
        return "ResponseRequest{" +
                "status=" + status +
                ", node=" + node +
                ", nodes=" + nodes +
                ", files=" + files +
                ", fileHash=" + fileHash +
                ", fileName=" + fileName +
                ", fileData=" + fileData +
                ", senderIp=" + senderIp +
                '}';
    }
}