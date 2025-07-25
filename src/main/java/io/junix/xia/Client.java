package io.junix.xia;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel client = SocketChannel.open();
//        client.configureBlocking(false);
        client.connect(new InetSocketAddress("localhost", 8080));
        System.out.println(client.getLocalAddress());
        System.out.println("Connect to server...");
        if (client.isConnected()) {
            System.out.println("Connected.");
            client.close();
        }
        System.out.println("Over.");
    }

}
