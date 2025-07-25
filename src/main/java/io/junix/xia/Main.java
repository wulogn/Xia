package io.junix.xia;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author wulogn
 */
public class Main {

    public static void main(String[] args) {
        try (ServerSocketChannel channel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

            channel.bind(new InetSocketAddress(8080));
            channel.configureBlocking(false);

            SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server channel returned: "
                    + selectionKey.channel().getClass().getCanonicalName() + " "
                    + selectionKey.channel().hashCode());

            while (true) {
                int i = selector.select();
                System.out.println("Some operation occur: " + i);

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey1 = iterator.next();
                    iterator.remove();

                    System.out.println("Current interest ops: " + selectionKey1.interestOps());

                    if (selectionKey1.interestOps() == SelectionKey.OP_ACCEPT) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey1.channel();
                        try {
                            SocketChannel clientChannel = serverChannel.accept();
                            clientChannel.configureBlocking(false);

                            System.out.println("Selection key returned: "
                                    + clientChannel.getClass().getCanonicalName() + " "
                                    + clientChannel.hashCode());

                            if (selectionKey1.isAcceptable()) {

                                try {
                                    clientChannel.register(selector, SelectionKey.OP_READ);
                                } catch (ClosedChannelException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    } else if (selectionKey1.interestOps() == SelectionKey.OP_READ) {
                        SocketChannel clientChannel = (SocketChannel) selectionKey1.channel();

                        long bytes = clientChannel.read(ByteBuffer.allocate(1024));
                        if (bytes == -1) {
                            System.out.println("Remote is closed.");
                            clientChannel.close();
                        }
                    } else {
                        System.out.println("other");

                    }

                    System.out.println("next loop");

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
