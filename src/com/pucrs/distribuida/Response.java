package com.pucrs.distribuida;

import java.util.List;

public class Response {
    private int status;
    private Node node;

    public Response(int status) {
        this.status = status;
        this.node = null;
    }

    public Response(int status, Node node) {
        this.status = status;
        this.node = node;
    }

    public int getStatus() {
        return status;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", node=" + node +
                '}';
    }
}
