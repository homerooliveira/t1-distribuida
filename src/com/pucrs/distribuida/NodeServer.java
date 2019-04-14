package com.pucrs.distribuida;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.tools.classfile.ConstantPool;

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
import java.util.stream.Collectors;


public class NodeServer {

    public static final int DEFAULT_PORT = 4000;

    private final String ip;
    private final String superNodeIp;
    private final int superNodePort;
    private final boolean isDebug;
    private final String path;
    private List<PathData> files;

    public static void main(String[] args) {
        String ip = args[0];
        String superNodeIp = args[1];
        int superNodePort = Integer.parseInt(args[2]);
        boolean isDebug = Boolean.parseBoolean(args[3]);
        String path = args[4];

        new NodeServer(ip, superNodeIp, superNodePort, isDebug, path).run();
    }

    public NodeServer(String ip, String superNodeIp, int superNodePort, boolean isDebug, String path) {
        this.ip = ip;
        this.superNodeIp = superNodeIp;
        this.superNodePort = superNodePort;
        this.isDebug = isDebug;
        this.path = path;
    }


    public void run() {
        sendFiles();
        new Thread(this::listen).start();
    }

    void listen() {
        DatagramSocket serverSocket;
        try {
            if (isDebug) {
                serverSocket = new DatagramSocket(Integer.parseInt(ip));
            } else {
                serverSocket = new DatagramSocket(DEFAULT_PORT);
            }

            final byte[] receiveData = new byte[1024];

            while (true) {
                final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                final String receivedMessage = new String(receivePacket.getData(), receivePacket.getOffset(),
                        receivePacket.getLength());

                Response response = new Gson().fromJson(receivedMessage, Response.class);

                if(response.getStatus() == Constants.NODE_RECEIVE_FILES_FROM_SUPER_NODE) {
                    System.out.println("Parse da lista");
                } else if(response.getStatus() == Constants.NODE_REQUEST_FILE_TO_NODE) {
                    System.out.println("recebe ip para enviar o arquivo");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendFiles() {
        try {
            readAllFiles();

            List<File> fileList = files.stream()
                    .map(pathData -> new File(pathData.getPath().getFileName().toString()
                            , pathData.getHash(), ip))
                    .collect(Collectors.toList());

            Response response = new Response(1, new Node(ip, fileList));
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendResponse(Response response) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress address;
            if (isDebug) {
                address = InetAddress.getLocalHost();
            } else {
                address = InetAddress.getByName(superNodeIp);
            }

            final Gson gson = new Gson();
            String json = gson.toJson(response);

            byte[] sendData = json.getBytes(Charset.forName("utf8"));

            System.out.println(superNodeIp);
            System.out.println(superNodePort);

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    address,
                    superNodePort);

            clientSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private void sendSignal() {
        
    }
}
