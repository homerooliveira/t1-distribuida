package com.pucrs.distribuida;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class SuperNodeServer {

    private final Map<String, Node> nodes = Collections.synchronizedMap(new HashMap());
    private final Map<String, ArrayList<File>> requests = Collections.synchronizedMap(new HashMap());

    // current machine ip address
    private String ip;
    // socket port used in communications with other supernodes
    public static final int GROUP_PORT = 5000;
    // socket port used in direct communications with nodes
    public static final int DIRECT_NODE_PORT = 6000;
    // socket port used in direct communications with super nodes
    public static final int DIRECT_SUPER_NODE_PORT = 7000;

    public static void main(String[] args) throws IOException {
        String ip = args[0];
        new SuperNodeServer(ip).run();
    }

    public SuperNodeServer(String ip) {
        this.ip = ip;
    }

    public void run() {
        new Thread(this::listenSuperNodesGroup).start();
        new Thread(this::listenNodes).start();
        new Thread(this::listenSuperNodeDirect).start();

        new Thread(() -> {
            while (true) {
                removeDeadNodes();
            }
        }).start();
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

    // listens for file requests from other super nodes
    public void listenSuperNodesGroup() {
        try {
            MulticastSocket socket = new MulticastSocket(GROUP_PORT);
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(grupo);
            while (true) {
                byte[] entrada = new byte[1024];
                DatagramPacket pacote = new DatagramPacket(entrada, entrada.length);
                socket.receive(pacote);
                String recebido = new String(pacote.getData(), 0, pacote.getLength());

                ResponseRequest response = new Gson().fromJson(recebido, ResponseRequest.class);

                if (response.getSenderIp().equals(ip)) { continue; }

                int status = response.getStatus();
                if (status == Constants.SUPER_NODE_RECEIVE_REQUEST_FROM_SUPER_NODE) {
                    sendResponseToSuperNode(response.getRequestIdentifier(), response.getFileName(), response.getSenderIp());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ArrayList<File> filesForName(String fileName) {
        ArrayList<File> files = new ArrayList<File>();
        for (Node node : nodes.values()) {
            for (File file : node.getFiles()) {
                if (file.getName().contains(fileName)) {
                    files.add(file);
                }
            }
        }
        return  files;
    }

    // answers a super node with the files related to your request
    public void sendResponseToSuperNode(String requestIdentifier, String fileName, String superNodeIp) {
        System.out.println("Ip homero = " + superNodeIp);
        try {
            System.out.println("#Sending files to super node - superNodeIp: " + superNodeIp);
            ResponseRequest request = new ResponseRequest();
            request.setRequestIdentifier(requestIdentifier);
            request.setFileName(fileName);
            request.setSenderIp(superNodeIp);
            request.setStatus(Constants.SUPER_NODE_SEND_FILES_TO_SUPER_NODE);

            ArrayList<File> files = filesForName(fileName);
            System.out.println("files = " + files);
            request.setFiles(files);

            String json = new Gson().toJson(request);
            byte[] sendData = json.getBytes();

            InetAddress address = InetAddress.getByName(superNodeIp);

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, DIRECT_SUPER_NODE_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(sendPacket);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // creates a file request to send to another super node
    ResponseRequest getFileRequest(String fileRequested) {
        ResponseRequest request = new ResponseRequest();
        request.setFileName(fileRequested);
        int status = Constants.SUPER_NODE_SEND_REQUEST_TO_SUPER_NODE;
        request.setStatus(status);
        request.setSenderIp(ip);
        request.setRequestIdentifier(UUID.randomUUID().toString());
        return request;
    }

    ResponseRequest getFilesToNode(String requestIdentifier) {
        ArrayList<File> files = requests.remove(requestIdentifier);
        ResponseRequest response = new ResponseRequest();
        response.setStatus(Constants.SUPER_NODE_SEND_FILES_TO_NODE);
        response.setSenderIp(ip);
        response.setFiles(files);
        System.out.println(files);
        return response;
    }

    // update the list of files of a request as new files pop up
    void updateRequest(String requestIdentifier, ArrayList<File> receivedFiles) {
        ArrayList<File> files = requests.get(requestIdentifier);
        if (files == null) {
            requests.put(requestIdentifier, receivedFiles);
        } else {
            files.addAll(receivedFiles);
            requests.put(requestIdentifier, files);
        }
    }

    // sends file requests to super nodes
    public void sendToSuperNodes(ResponseRequest request) {
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

    // listens for nodes actions
    public void listenNodes() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(DIRECT_NODE_PORT);
            final byte[] receiveData = new byte[1024];

            while (true) {
                final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                final String receivedMessage = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                ResponseRequest response = new Gson().fromJson(receivedMessage, ResponseRequest.class);
                int status = response.getStatus();
                if (status == Constants.SUPER_NODE_RECEIVE_FILES_FROM_NODE) {
                    System.out.println("#Receiving files from node - nodeIp: " + response.getSenderIp());
                    Node node = response.getNode();
                    nodes.put(node.getIp(), node);
                } else if (status == Constants.SUPER_NODE_RECEIVE_REQUEST_FROM_NODE) {
                    System.out.println("#Receiving request from node - nodeIp: " + response.getSenderIp());
                    ResponseRequest request = getFileRequest(response.getFileName());
                    sendToSuperNodes(request);
                    Thread.sleep(5000);
                    updateRequest(request.getRequestIdentifier(), filesForName(response.getFileName()));
                    System.out.println("#Sending files to node - nodeIp: " + response.getSenderIp());
                    sendFilesToNode(request.getRequestIdentifier(), response.getSenderIp());
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

    // listens when a super node sends a list of files
    public void listenSuperNodeDirect() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(DIRECT_SUPER_NODE_PORT);
            final byte[] receiveData = new byte[1024];

            while (true) {
                final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                final String receivedMessage = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                ResponseRequest response = new Gson().fromJson(receivedMessage, ResponseRequest.class);
                int status = response.getStatus();
                if (status == Constants.SUPER_NODE_RECEIVE_FILES_FROM_SUPER_NODE) {
                    System.out.println("#Receiving files from super node - superNodeIp: " + response.getSenderIp());
                    updateRequest(response.getRequestIdentifier(), response.getFiles());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // sends the known files to the node
    public void sendFilesToNode(String requestIdentifier, String nodeIp) {
        try {
            ResponseRequest response = getFilesToNode(requestIdentifier);
            String json = new Gson().toJson(response);
            byte[] sendData = json.getBytes();

            InetAddress address = InetAddress.getByName(nodeIp);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, NodeServer.DEFAULT_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(sendPacket);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
