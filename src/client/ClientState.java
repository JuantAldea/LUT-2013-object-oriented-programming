/*
 * Juan Antonio Aldea Armenteros (0404450)
 * LUT - Object Oriented Programming Techniques
 *                  2013
 */

package client;

import java.net.InetSocketAddress;

import misc.State;

public class ClientState extends State {
    private static final ClientState instance      = new ClientState();
    private InetSocketAddress        remoteAddress = null;
    private boolean                  connected     = false;

    private ClientState() {
        super();
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static synchronized ClientState getInstance() {
        return instance;
    }

    public synchronized void setServerAddress(String ip, int port) {
        remoteAddress = new InetSocketAddress(ip, port);
    }

    public synchronized InetSocketAddress getServerAddress() {
        return remoteAddress;
    }

    public synchronized boolean isRunning() {
        return connected;
    }

    public synchronized void startClient() {
        connected = true;
    }

    public synchronized void stopClient() {
        connected = false;
    }
}
