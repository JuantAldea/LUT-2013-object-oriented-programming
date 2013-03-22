package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ServerState {
	private static final ServerState instance = new ServerState();
	protected boolean running = false;
	protected int servingQueueSize = 10;
	protected boolean jokeListTainted = false;
	protected int listeningPort = 27015;
	protected ArrayList<String> listJoke = new ArrayList<String>();

	private ServerState() {

	}

	public synchronized void readJokesFile(String filePath) {
		FileInputStream fileInputStream = null;
		InputStreamReader inputStremReader = null;
		BufferedReader bufferedReader = null;

		try {
			fileInputStream = new FileInputStream(filePath);
			inputStremReader = new InputStreamReader(fileInputStream,
					Charset.defaultCharset());
			bufferedReader = new BufferedReader(inputStremReader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				listJoke.add(line);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (inputStremReader != null) {
			try {
				inputStremReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (fileInputStream != null) {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void saveJokeList(String filePath) {
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter bufferedWriter = null;

		try {
			fileOutputStream = new FileOutputStream(filePath);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream,
					Charset.defaultCharset());
			bufferedWriter = new BufferedWriter(outputStreamWriter);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		for (String joke : listJoke) {
			try {
				bufferedWriter.write(joke);
				bufferedWriter.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bufferedWriter.close();
			outputStreamWriter.close();
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.jokeListTainted = false;

	}

	public synchronized void saveJokeListIfNeed(String filepath) {
		if (this.jokeListTainted) {
			this.saveJokeList(filepath);
		}
	}

	public synchronized boolean addJoke(String newJoke) {
		boolean found = listJoke.contains(newJoke);
		if (!found) {
			jokeListTainted = true;
			listJoke.add(newJoke);
		}
		return found;
	}

	public synchronized String getRandomJoke() {
		return listJoke.get((int) Math.floor(Math.random() * 10));
	}

	public static ServerState getInstance() {
		return instance;
	}

	public synchronized void startServer() {
		this.running = true;
	}

	public synchronized void stopServer() {
		this.running = false;
	}

	public synchronized boolean running() {
		return this.running;
	}

	public synchronized void setWaitQueueSize(int size) {
		this.servingQueueSize = size;
	}

	public synchronized int getWaitQueueSize() {
		return this.servingQueueSize;
	}

	public synchronized int getListeningPort() {
		return listeningPort;
	}
	
	public synchronized void setListeningPort(int listeningPort) {
		this.listeningPort = listeningPort;
	}
	

	public synchronized boolean serving() {
		return this.servingQueueSize > 0;
	}
}
