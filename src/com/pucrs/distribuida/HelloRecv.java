package com.pucrs.distribuida;

import java.net.*;
import java.io.IOException;

public class HelloRecv {
        public static void main(String[] args) throws IOException {

            new Thread(() -> {
                MulticastSocket socket = null;
                try {
                    socket = new MulticastSocket(5000);
                    InetAddress grupo = InetAddress.getByName("230.0.0.1");
                    socket.joinGroup(grupo);
                    byte[] entrada = new byte[256];
                    DatagramPacket pacote = new DatagramPacket(entrada,entrada.length);
                    socket.receive(pacote);
                    String recebido = new String(pacote.getData(),0,pacote.getLength());
                    System.out.println("Received: "+recebido);
                    socket.leaveGroup(grupo);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).run();


            new Thread(() -> {
                String mens = "Alô, mundo!";
                byte[] saida = mens.getBytes();
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
            }).run();
        }
}
