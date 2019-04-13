package com.pucrs.distribuida;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServerMetal {
    public static void main(String[] args) throws IOException {
        MulticastSocket socket = new MulticastSocket(5000);
        InetAddress grupo = InetAddress.getByName("230.0.0.1");
        socket.joinGroup(grupo);
        while (true) {
            byte[] entrada = new byte[256];
            DatagramPacket pacote = new DatagramPacket(entrada, entrada.length);
            socket.receive(pacote);
            String recebido = new String(pacote.getData(), 0, pacote.getLength());
            System.out.println("Received: " + recebido);
//            socket.leaveGroup(grupo);
//            socket.close();
        }
    }
}
