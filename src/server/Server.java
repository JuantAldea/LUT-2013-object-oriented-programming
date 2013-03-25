/*
 * Juan Antonio Aldea Armenteros (0404450)
 * LUT - Object Oriented Programming Techniques
 *                  2013
 */

package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

public class Server implements Runnable {
    protected ServerState               serverState = ServerState.getInstance();
    protected LinkedList<SocketChannel> clientList  = new LinkedList<SocketChannel>();
    protected Selector                  selector    = null;
    protected ServerSocketChannel       server      = null;

    public Server() {

    }

    public void wakeUp() {
        if (selector != null) {
            // unlocks threads that are waiting on the selector
            selector.wakeup();
        }
    }

    @Override
    public void run() {
        serverState.readJokesFile("Jokes0404450.txt");
        try {
            server = ServerSocketChannel.open();
            server.socket().bind(new InetSocketAddress(serverState.getListeningPort()));
            server.configureBlocking(false);
            server.socket().setReuseAddress(true);
            selector = Selector.open();

            while (serverState.isRunning()) {
                // register everyone in the selector
                server.register(selector, SelectionKey.OP_ACCEPT);
                for (SocketChannel socket : clientList) {
                    socket.register(selector, SelectionKey.OP_READ);
                }
                // wait for activity
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey selKey = it.next();
                    it.remove();
                    if (selKey.isAcceptable()) {
                        // Activity on the server socket means one new client
                        // arrived
                        SocketChannel newClient = ((ServerSocketChannel) selKey.channel()).accept();
                        if (serverState.getAcceptingNewConnections()) {
                            newClient.configureBlocking(false);
                            clientList.add(newClient);
                            String joke = serverState.getRandomJoke();
                            if (joke.length() > 0) {
                                sendJoke(newClient, joke);
                            }
                        } else {
                            newClient.close();
                        }
                    } else if (selKey.isReadable()) {
                        // activity on some client socket
                        SocketChannel client = (SocketChannel) selKey.channel();
                        ByteBuffer buf = ByteBuffer.allocate(client.socket().getReceiveBufferSize()).order(
                                ByteOrder.BIG_ENDIAN);
                        if (client.isConnected()) {
                            int received_bytes = client.read(buf);
                            if (received_bytes >= (Integer.SIZE / 8)) {
                                buf.flip();
                                int broza = buf.getInt();
                                // 123 means joke request
                                if (broza == 123) {
                                    String joke = serverState.getRandomJoke();
                                    if (joke.length() > 0) {
                                        sendJoke(client, joke);
                                    }
                                }
                            }

                        } else {
                            clientList.remove(client);
                        }
                    }
                }
            }
            // shutting down the server, so clean up everything
            for (SocketChannel socketChannel : clientList) {
                socketChannel.close();
            }
            clientList.clear();
            server.close();
            selector.close();

        } catch (IOException e1) {

            e1.printStackTrace();
        }

        serverState.saveJokeListIfNeed("Jokes0404450.txt");
    }

    protected void sendJoke(SocketChannel client, String joke) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(joke.length() + (Integer.SIZE / 8)).order(ByteOrder.BIG_ENDIAN);
        buf.clear();
        buf.putInt(joke.length() + (Integer.SIZE / 8));
        buf.put(joke.getBytes());
        buf.flip();
        client.write(buf);
    }
}
