/*
 * Juan Antonio Aldea Armenteros (0404450)
 * LUT - Object Oriented Programming Techniques
 *                  2013
 */

package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import gui.GUI;

public class Client implements Runnable {
    protected SocketChannel socket      = null;
    protected ClientState   clientState = ClientState.getInstance();
    protected GUI           gui         = null;
    protected Selector      selector    = null;
    protected String        jokeFilePath    = "JokesClient0404405.txt";

    public Client(GUI gui) {
        this.gui = gui;
        clientState.readJokesFile(jokeFilePath);
    }

    public void wakeUp() {
        if (selector != null) {
            // unlocks threads that are waiting on the selector
            selector.wakeup();
        }
    }

    @Override
    public void run() {
        ByteBuffer buffer = null;
        try {
            socket = SocketChannel.open(clientState.getServerAddress());
            socket.configureBlocking(false);
            buffer = ByteBuffer.allocate(socket.socket().getReceiveBufferSize()).order(ByteOrder.BIG_ENDIAN);
            selector = Selector.open();
            gui.requestSetGUIClientConnectedState();
        } catch (IOException e) {
            clientState.stopClient();
            gui.requestSetGUIClientDisconnectedState();
            gui.requestClientPrint("Unable to connect");
            return;
        }

        // on successful connection, clear text
        gui.requestClientClear();
        while (clientState.isRunning()) {
            try {
                socket.register(selector, SelectionKey.OP_READ);
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                // iterate the list of ready descriptors
                while (it.hasNext()) {
                    SelectionKey selKey = it.next();
                    it.remove();
                    if (selKey.isReadable()) {
                        SocketChannel client = (SocketChannel) selKey.channel();
                        int bytesReaded = client.read(buffer);
                        if (bytesReaded == -1) {
                            // got a disconnection disconnection
                            clientState.stopClient();
                            gui.requestClientPrint("Connection closed by the server");
                            gui.requestSetGUIClientDisconnectedState();
                        } else {
                            if (bytesReaded >= (Integer.SIZE / 8)) {
                                // got enough bytes to read an integer size
                                ByteBuffer aux = buffer.duplicate();
                                aux.flip();
                                int msgSize = aux.getInt();
                                // do it has enough bytes for the given msg
                                // size?
                                if (msgSize <= buffer.remaining()) {
                                    int payloadSize = msgSize - (Integer.SIZE / 8);
                                    buffer.flip();
                                    // consume and ignore the size of the
                                    // message since it is already read
                                    buffer.getInt();
                                    byte[] byteArray = new byte[payloadSize];
                                    buffer.get(byteArray, 0, payloadSize);
                                    buffer.clear();
                                    String joke = new String(byteArray);
                                    gui.requestClientPrint(joke);
                                    clientState.addJoke(joke);
                                    //write through
                                    clientState.saveJokeListIfNeed(jokeFilePath);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // clear and exit
        try {
            socket.close();
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void requestJoke() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate((Integer.SIZE / 8)).order(ByteOrder.BIG_ENDIAN);
        buffer.clear();
        buffer.putInt(new Integer(123).byteValue());
        buffer.flip();
        socket.write(buffer);
    }
}
