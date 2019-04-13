package com.pucrs.distribuida;

import java.io.*;
import java.net.*;
import java.util.*;

public class MulticastClientMetal {

    public static void main(String[] args) throws IOException {

        InetAddress grupo = InetAddress.getByName("230.0.0.1");
        byte[] saida =  "Alô, Homero!".getBytes();
        DatagramPacket pacote = new DatagramPacket(saida,saida.length,grupo,5000);

    }

}