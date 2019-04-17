package com.pucrs.distribuida;

import java.util.ArrayList;

public class Response {
    private int status;
    private Node node;
    private ArrayList<Node> nodes;
    private ArrayList<File> files;
    private String fileHash;
    private String fileName;
    private byte[] fileData;
    private String senderIp;

    public Response(){}

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
    public void setFileData(byte[] fileName) {
        this.fileData = fileData;
    }

    public String getSenderIp() { return senderIp; }
    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }




    // NODE_SEND_FILE_TO_NODE
//    public Response(String fileName, byte[] fileData, String senderIp) {
//        this.status = Constants.NODE_SEND_FILE_TO_NODE;
//        this.fileName = fileName;
//        this.fileData = fileData;
//        this.senderIp = senderIp;
//    }



    public Response(int status, Node node) {
        this.status = status;
        this.node = node;
    }

    public Response(int status, ArrayList<File> files) {
        this.status = status;
        this.files = files;
    }

    @Override
    public String toString() {
        return "Response{" +
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