package com.pucrs.distribuida;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class NodeServer {
    public static final int DEFAULT_PORT = 4000;

    // current machine ip address
    private final String ip;
    // ip address of the super node this machine communicates
    private final String superNodeIp;
    // path where the files of this machine are
    private final String path;
    // files on this machine
    private List<PathData> files;

    public static void main(String[] args) {
        String ip = args[0];
        String superNodeIp = args[1];
        String path = args[2];

        new NodeServer(ip, superNodeIp, path).run();
    }

    public NodeServer(String ip, String superNodeIp, String path) {
        this.ip = ip;
        this.superNodeIp = superNodeIp;
        this.path = path;
    }

    public void run() {
        sendFiles();
        new Thread(this::listen).start();
        new Thread(this::sendSignal).start();
        new Thread(this::listenKeyboard).start();
    }

    // listens the keyboard waiting for a file request
    void listenKeyboard() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String fileName = scanner.nextLine();
            Response request = new Response();
            request.setSenderIp(ip);
            request.setStatus(Constants.NODE_SEND_REQUEST_TO_SUPER_NODE);
            request.setFileName(fileName);
            sendToSuperNode(request);
        }
    }

    // listens the direct communications with this node
    void listen() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(DEFAULT_PORT);

            final byte[] receiveData = new byte[1024];

            while (true) {
                final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                final String receivedMessage = new String(receivePacket.getData(), receivePacket.getOffset(),
                        receivePacket.getLength());

                Response response = new Gson().fromJson(receivedMessage, Response.class);

                if(response.getStatus() == Constants.NODE_RECEIVE_FILES_FROM_SUPER_NODE) {
                    System.out.println("#Receiving files from super node.");
                    System.out.println(response);
                } else if(response.getStatus() == Constants.NODE_RECEIVE_FILE_REQUEST_FROM_NODE) {
                    System.out.println("#Sending file to node - nodeIp: " + response.getSenderIp());
                    sendRequestFileToNode(response.getFileHash(), response.getSenderIp());
                } else if (response.getStatus() == Constants.NODE_RECEIVE_FILE_FROM_NODE) {
                    System.out.println("#Receiving file from node.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // takes the file that will be sent in the response to the node that made the request
    Response getFileResponseToNode(String fileHash) {
        for (PathData file : files) {
            if (file.hash.equals(fileHash)) {
                Response response = new Response();
                response.setStatus(Constants.NODE_SEND_FILE_TO_NODE);
                response.setFileData(file.data);
                return response;
            }
        }
        return  null;
    }

    // sends to the super node a list of files that this machine has available
    void sendFiles() {
        try {
            readAllFiles();

            List<File> fileList = files.stream()
                    .map(pathData -> new File(pathData.getPath().getFileName().toString()
                            , pathData.getHash(), ip))
                    .collect(Collectors.toList());

            Response response = new Response(Constants.NODE_SEND_FILES_TO_SUPER_NODE, new Node(ip, fileList));
            response.setSenderIp(ip);
            sendToSuperNode(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // sends a requested file to node
    void sendRequestFileToNode(String fileHash, String nodeIp) {
        try {
            Response response = getFileResponseToNode(fileHash);
            String json = new Gson().toJson(response);
            byte[] sendData = json.getBytes(Charset.forName("utf8"));

            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(nodeIp);
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    address,
                    DEFAULT_PORT);

            socket.send(sendPacket);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // sends actions to the super node
    void sendToSuperNode(Response response) {
        try {
            InetAddress address = InetAddress.getByName(superNodeIp);

            String json = new Gson().toJson(response);
            byte[] sendData = json.getBytes(Charset.forName("utf8"));

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, MulticastServerMetal.DIRECT_NODE_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(sendPacket);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // reads the files of this machine
    private void readAllFiles() throws IOException {
        files = Files.list(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(path1 -> {
                    try {
                        byte[] bytes = Files.lines(path1, Charset.forName("utf8"))
                                .collect(Collectors.joining())
                                .getBytes(Charset.forName("utf8"));
                        return new PathData(path1, bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new PathData(path1, new byte[0]);
                    }
                })
                .filter(pathData -> pathData.data.length > 0)
                .map(pathData -> {
                    try {
                        byte[] data = MessageDigest.getInstance("MD5").digest(pathData.data);
                        StringBuilder sb = new StringBuilder(2 * data.length);
                        for(byte b : data) {
                            sb.append(String.format("%02x", b&0xff));
                        }
                        pathData.setHash(sb.toString());
                        return pathData;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        return pathData;
                    }

                })
                .collect(Collectors.toList());
    }

    // sends a life signal to the super node
    private void sendSignal() {
        while (true) {
            Response response = new Response();
            response.setStatus(Constants.NODE_SEND_LIFE_SIGNAL_TO_SUPER_NODE);
            response.setSenderIp(ip);
            sendToSuperNode(response);
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
