package com.pucrs.distribuida;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.UUID;

public class MulticastServerMetal {

    private static final String IDENTIFIER = UUID.randomUUID().toString();

    public static void main(String[] args) throws IOException {
        new Thread(() -> {
            MulticastSocket socket = null;
            try {
                socket = new MulticastSocket(5000);
                InetAddress grupo = InetAddress.getByName("230.0.0.1");
                socket.joinGroup(grupo);
                while (true) {
                    byte[] entrada = new byte[256];
                    DatagramPacket pacote = new DatagramPacket(entrada, entrada.length);
                    socket.receive(pacote);
                    String recebido = new String(pacote.getData(), 0, pacote.getLength());
                    if (recebido.contains(IDENTIFIER)) { return; }
                    System.out.println("Received: " + recebido);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        new Thread(() -> {
            Scanner input = new Scanner(System.in);
            boolean hasWork = true;

            while (hasWork) {
                byte[] saida = input.nextLine().getBytes();
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

        }).start();
    }
}
