package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

public class Server implements Runnable {
	protected ServerState serverState = ServerState.getInstance();
	protected LinkedList<SocketChannel> clientList = new LinkedList<SocketChannel>();

	public Server() {

	}

	@Override
	public synchronized void run() {
		ServerSocketChannel server;
		Selector selector;
		try {
			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.socket().bind(new InetSocketAddress(serverState.getListeningPort()));
			selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT);
			while (serverState.running()) {
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey selKey = it.next();
					it.remove();
					if (selKey.isAcceptable()) {
						clientList.add(((ServerSocketChannel)selKey.channel()).accept());
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		serverState.saveJokeListIfNeed("broza.txt");
	}
}
