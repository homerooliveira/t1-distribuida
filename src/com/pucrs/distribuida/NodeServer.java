package com.pucrs.distribuida;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.ExportException;

public class NodeServer {

    public static final int SUPER_NODE_MESSAGE = 1;
    public static final int NODE_MESSAGE = 2;
    public static final int DEFAULT_PORT = 4000;

    private final String ip;
    private final String superNodeIp;
    private final int superNodePort;
    private final boolean isDebug;

    public static void main(String[] args) {
        String ip = args[0];
        String superNodeIp = args[1];
        int superNodePort = Integer.parseInt(args[2]);
        boolean isDebug = Boolean.parseBoolean(args[3]);

        new NodeServer(ip, superNodeIp, superNodePort, isDebug).run();
    }

    public  NodeServer(String ip, String superNodeIp, int superNodePort, boolean isDebug) {
        this.ip = ip;
        this.superNodeIp = superNodeIp;
        this.superNodePort = superNodePort;
        this.isDebug = isDebug;
    }


    public void run() {
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

                if(receivedMessage.charAt(0) == '1') {
                    System.out.println("Parse da lista");
                } else if(receivedMessage.charAt(0) == '2') {
                    System.out.println("recebe ip para enviar o arquivo");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendFiles() {
            try (DatagramSocket clientSocket = new DatagramSocket()) {
                InetAddress address;
                if (isDebug) {
                    address = InetAddress.getLocalHost();
                } else {
                    address = InetAddress.getByName(superNodeIp);
                }

                // TODO: Fazer parse dos arquivos
                byte[] sendData = "arquivos".getBytes();

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
}
