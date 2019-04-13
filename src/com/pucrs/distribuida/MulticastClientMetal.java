package com.pucrs.distribuida;

import java.io.*;
import java.net.*;
import java.util.*;

public class MulticastClientMetal {

    public static void main(String[] args) throws IOException {
        byte[] saida = new byte[256];
        String mens = "Alô, Homero!";
        saida = mens.getBytes();
        DatagramSocket socket = new DatagramSocket();
        InetAddress grupo = InetAddress.getByName("230.0.0.1");
        DatagramPacket pacote = new DatagramPacket(saida,saida.length,grupo,5000);
        socket.send(pacote);
        socket.close();
    }

}