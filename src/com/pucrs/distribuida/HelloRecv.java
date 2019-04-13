package com.pucrs.distribuida;

import java.net.*;
import java.io.IOException;

public class HelloRecv {

    public static void main(String[] args) {

//        new Thread(HelloRecv::listenGroup).start();

        new Thread(() -> {

            DatagramSocket socket = null;

            try {
                System.out.println("send");
                socket = new DatagramSocket();
                InetAddress grupo = InetAddress.getByName("230.0.0.1");
                while (true) {
                    String mens = "Alô, mundo!";
                    byte[] saida = mens.getBytes();

                    DatagramPacket pacote = new DatagramPacket(saida, saida.length, grupo, 5000);
                    socket.send(pacote);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void listenGroup() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(5000);
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(grupo);
            while (true) {
                System.out.println("receive");
                byte[] entrada = new byte[256];
                DatagramPacket pacote = new DatagramPacket(entrada, entrada.length, grupo, 5000);
                socket.receive(pacote);
                System.out.println("receive pos");
                String recebido = new String(pacote.getData(), 0, pacote.getLength());
                System.out.println("Received: " + recebido);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
