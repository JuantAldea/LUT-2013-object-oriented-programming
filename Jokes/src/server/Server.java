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
		ServerSocketChannel server = null;
		Selector selector;
		try {
			server = ServerSocketChannel.open();
			
			
			server.socket().bind(
					new InetSocketAddress(serverState.getListeningPort()));
			server.configureBlocking(false);
			server.socket().setReuseAddress(true);
			selector = Selector.open();

			while (serverState.running()) {
				server.register(selector, SelectionKey.OP_ACCEPT);

				for (SocketChannel socket : clientList) {
					socket.register(selector, SelectionKey.OP_READ);
				}

				selector.select(1000);
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey selKey = it.next();
					it.remove();
					if (selKey.isAcceptable()) {
						SocketChannel newClient = ((ServerSocketChannel) selKey.channel()).accept();
						newClient.configureBlocking(false);
						clientList.add(newClient);
					} else if (selKey.isReadable()) {
						((SocketChannel) selKey.channel()).socket();
						System.out.println("POLLACA2");
					}
				}
			}
			System.out.println("POLLACA");

			
			for (SocketChannel socket : clientList) {
				socket.close();
			}
			server.close();
			server.socket().close();
			selector.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		serverState.saveJokeListIfNeed("broza.txt");
	}
}
