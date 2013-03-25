/*
 * Juan Antonio Aldea Armenteros (0404450)
 * LUT - Object Oriented Programming Techniques
 *                  2013
 */

package server;

import misc.State;

public class ServerState extends State {
    private static final ServerState instance                = new ServerState();
    protected boolean                acceptingNewConnections = true;
    protected boolean                running                 = false;
    protected int                    listeningPort           = 27015;

    private ServerState() {
        super();
    }

    public static synchronized ServerState getInstance() {
        return instance;
    }

    public synchronized String getRandomJoke() {
        if (listJoke.size() == 0) {
            return "";
        }
        String pollaca = listJoke.get(((int) Math.floor(Math.random() * 10)) % listJoke.size());
        return pollaca;
    }

    public synchronized void startServer() {
        this.running = true;
    }

    public synchronized void stopServer() {
        this.running = false;
    }

    public synchronized boolean isRunning() {
        return this.running;
    }

    public synchronized void setAcceptingNewConnections(boolean accepting) {
        acceptingNewConnections = accepting;
    }

    public synchronized boolean getAcceptingNewConnections() {
        return acceptingNewConnections;
    }

    public synchronized int getListeningPort() {
        return listeningPort;
    }

    public synchronized void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }
}
