package com.pucrs.distribuida;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.UUID;

public class MulticastServerMetal {

    private ArrayList<Node> nodes = new ArrayList<Node>();

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
    }

    public MulticastServerMetal(String ip) {
        this.ip = ip;
    }

    public void listenSuperNodes() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(GROUP_PORT);
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(grupo);
            while (true) {
                byte[] entrada = new byte[512];
                DatagramPacket pacote = new DatagramPacket(entrada, entrada.length);
                socket.receive(pacote);
                String recebido = new String(pacote.getData(), 0, pacote.getLength());
                if (recebido.contains(IDENTIFIER)) { return; }

                Gson gson = new Gson();
                Response response = gson.fromJson(recebido, Response.class);

                int status = response.getStatus();

                if (status == Constants.SUPER_NODE_RECEIVE_REQUEST_FROM_SUPER_NODE) {
                    sendMoviesToSuperNodes();
                } else if (status == Constants.SUPER_NODE_RECEIVE_FILES_FROM_SUPER_NODE) {
                    // atualiza meus filmes
                }
                System.out.println("Received: " + recebido);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMoviesToSuperNodes() {
        byte[] saida = "Toma os filmes".getBytes();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            DatagramPacket pacote = new DatagramPacket(saida,saida.length,grupo,5000);
            socket.send(pacote);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Response getFileRequest(String fileHash) {
        Response request = new Response(fileHash, ip);
        request.setSenderIp(ip);
        return request;
    }
    public void sendToSuperNodes(Response request) {
        byte[] saida = "Toma os filmes".getBytes();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            DatagramPacket pacote = new DatagramPacket(saida,saida.length,grupo,5000);
            socket.send(pacote);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenNodes() {
        DatagramSocket serverSocket;
        try {
            serverSocket = new DatagramSocket(6000);
            final byte[] receiveData = new byte[1024];

            while (true) {
                final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                final String receivedMessage = new String(receivePacket.getData(), receivePacket.getOffset(),
                        receivePacket.getLength());
//                System.out.println("Received message from node: " + receivedMessage);

                Gson gson = new Gson();
                Response response = gson.fromJson(receivedMessage, Response.class);
                int status = response.getStatus();
                if (status == Constants.SUPER_NODE_RECEIVE_FILES_FROM_NODE) {
                    System.out.println("SUPER_NODE_RECEIVE_FILES_FROM_NODE");
                    nodes.add(response.getNode());
                    response.getNode().getFiles();
                    System.out.println(nodes);
                    // Recebendo filmes que o nodo possui
                } else if (status == Constants.SUPER_NODE_RECEIVE_REQUEST_FROM_NODE) {
                    Response request = getFileRequest(response.getFileHash());
                    sendToSuperNodes(request);
                    // recebendo requisição de filme do nodo
                    Thread.sleep(5000);
                    System.out.println("Respondendo nodo.");
                    sendToNode("192.168.0.19", 400, response.getFileRequested());
                } else if (status == Constants.SUPER_NODE_RECEIVE_LIFE_SIGNAL_FROM_NODE) {
                    // nodo esta vivo, atualisa a data dele
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToNode(String clientIp, int clientPort, String fileRequested) {
        try {
            ArrayList<File> files = new ArrayList<File>();
            for (Node node : nodes) {
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
