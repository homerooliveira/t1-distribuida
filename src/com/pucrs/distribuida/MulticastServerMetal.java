package com.pucrs.distribuida;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class MulticastServerMetal {

    private final Map<String, Node> nodes = Collections.synchronizedMap(new HashMap<>());

    private final String IDENTIFIER = UUID.randomUUID().toString();

    public static final int GROUP_PORT = 5000;
    public static final int DIRECT_PORT = 6000;
    private String ip;

    public static void main(String[] args) throws IOException {
        String ip = args[0];
        MulticastServerMetal multicastServerMetal = new MulticastServerMetal(ip);

        new Thread(() -> {
            multicastServerMetal.listenSuperNodes();
        }).start();

        new Thread(() -> {
            multicastServerMetal.listenNodes();
        }).start();

        new Thread(() -> {
            while (true) {
                multicastServerMetal.removeDeadNodes();
            }
        }).start();
    }

    public MulticastServerMetal(String ip) {
        this.ip = ip;
    }

    private synchronized void removeDeadNodes() {
        try {
            Thread.sleep(5 * 1000);
            synchronized (nodes) {
                final Set<String> nodesToDelete = nodes.values()
                        .stream()
                        .peek(node -> {
                            if (node.isAlive()) {
                                node.decreaseLifeCount();
//                                System.out.println("life count " + node.getLifeCount() + " of node - " + node.getIp());
                            }
                        })
                        .filter(node -> !node.isAlive())
                        .map(Node::getIp)
                        .collect(Collectors.toSet());
                nodes.keySet().removeAll(nodesToDelete);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void listenSuperNodes() {
        try {
            MulticastSocket socket = new MulticastSocket(GROUP_PORT);
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(grupo);
            while (true) {
                byte[] entrada = new byte[1024];
                DatagramPacket pacote = new DatagramPacket(entrada, entrada.length);
                socket.receive(pacote);
                String recebido = new String(pacote.getData(), 0, pacote.getLength());

                Response response = new Gson().fromJson(recebido, Response.class);
                if (response.getidentifier().equals(this.IDENTIFIER)) { return; }

                int status = response.getStatus();
                if (status == Constants.SUPER_NODE_RECEIVE_REQUEST_FROM_SUPER_NODE) {
                    System.out.println("Sending files to super node - fileName: " + response.getFileName() + " - senderIp: " + response.getSenderIp());
                    sendResponseToSuperNode(response.getFileName(), response.getSenderIp());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendResponseToSuperNode(String fileName, String superNodeIp) {
        try {
            Response request = new Response();
            request.setFileName(fileName);
            request.setSenderIp(superNodeIp);
            request.setStatus(Constants.SUPER_NODE_SEND_FILES_TO_SUPER_NODE);


            ArrayList<File> files = new ArrayList<File>();
            for (Node node : nodes.values()) {
                for (File file : node.getFiles()) {
                    if (file.getName().contains(fileName)) {
                        files.add(file);
                    }
                }
            }

            request.setFiles(files);

            String json = new Gson().toJson(request);
            byte[] sendData = json.getBytes();

            InetAddress address = InetAddress.getByName(superNodeIp);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, DIRECT_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(sendPacket);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Response getFileRequest(String fileRequested) {
        Response request = new Response();
        request.setFileName(fileRequested);
        int status = Constants.SUPER_NODE_SEND_REQUEST_TO_SUPER_NODE;
        request.setStatus(status);
        request.setSenderIp(ip);
        return request;
    }

    public void sendToSuperNodes(Response request) {
        String json = new Gson().toJson(request);
        byte[] saida = json.getBytes();

        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            DatagramPacket pacote = new DatagramPacket(saida, saida.length, grupo, GROUP_PORT);
            socket.send(pacote);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenNodes() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(DIRECT_PORT);
            final byte[] receiveData = new byte[1024];

            while (true) {
                final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                final String receivedMessage = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                Response response = new Gson().fromJson(receivedMessage, Response.class);
                int status = response.getStatus();
                if (status == Constants.SUPER_NODE_RECEIVE_FILES_FROM_NODE) {
                    System.out.println("#Receiving files from node.");
                    Node node = response.getNode();
                    nodes.put(node.getIp(), node);
                } else if (status == Constants.SUPER_NODE_RECEIVE_REQUEST_FROM_NODE) {
                    System.out.println("#Receiving request from node - fileRequested: " + response.getFileName());
                    Response request = getFileRequest(response.getFileName());
                    sendToSuperNodes(request);
                    Thread.sleep(5000);
                    System.out.println("#Sending file to node.");
                    sendToNode(response.getSenderIp(), NodeServer.DEFAULT_PORT, response.getFileName());
                } else if (status == Constants.SUPER_NODE_RECEIVE_LIFE_SIGNAL_FROM_NODE) {
                    final String senderIp = response.getSenderIp();
                    final Node node = nodes.get(senderIp);
                    if (node != null) {
                        node.keepAlive();
//                        System.out.println("reset count of " + node.getIp());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToNode(String clientIp, int clientPort, String fileRequested) {
        try {
            ArrayList<File> files = new ArrayList<>();
            for (Node node : nodes.values()) {
                for (File file : node.getFiles()) {
                    if (file.getName().toLowerCase().contains(fileRequested.toLowerCase())) {
                        files.add(file);
                    }
                }
            }
            Response request = new Response(Constants.SUPER_NODE_SEND_FILES_TO_NODE,files);
            String json = new Gson().toJson(request);
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(clientIp);
            byte[] sendData = "Toma os filmes!".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, clientPort);
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
