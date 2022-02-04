package controllers;

import modules.Chatter;
import protocols.GlobalConstants;
import protocols.Message;
import protocols.Protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server {

	private ServerSocket socket;
	private List<Chatter> chatters;
	private List<ServerThread> lstThread;


	// constructor
	public Server() throws IOException, InterruptedException, ClassNotFoundException {
		this.socket = new ServerSocket(GlobalConstants.SERVER_PORT);
		this.chatters = new ArrayList<>();
		this.lstThread = new ArrayList<ServerThread>();;
		start();
	}

	public List<Chatter> getChatters() {
		return chatters;
	}

	public void setChatters(List<Chatter> chatters) {
		this.chatters = chatters;
	}

	public ServerSocket getSocket() {
		return socket;
	}

	public List<ServerThread> getLstThread() {
		return lstThread;
	}

	public void setLstThread(List<ServerThread> lstThread) {
		this.lstThread = lstThread;
	}


	// remove a chatter from the the list of chatters
	public void removeThCh(ServerThread th) {
		lstThread.remove(th);
		chatters.remove(th.getClient());
	}

	public void start() throws IOException, ClassNotFoundException {
		int id = 0;
		try {
			while (true) {
				System.out.println("Server is waiting for connection ....");
				Socket trans = socket.accept();

				// create a chatter and add it to the list of chatters
				Chatter c = new Chatter(id, trans.getInetAddress());
				chatters.add(c);

				// launch a thread for each new client ans add it to the list of threads
				ServerThread processes = new ServerThread(this, trans, c);
				lstThread.add(processes);

				System.out.println("New Client connected : " + c.getId());

				// notify the existing clients of the new client
				for (int i = 0; i < lstThread.size(); i++) {
					lstThread.get(i).notification(Protocol.clientList(this.getChatters(), lstThread.get(i).getClient()));
				}
				processes.start();

				id++;
			}
		} catch (SocketException e) {
			// generate an error message in case of connection reset
			System.out.println(Protocol.getErrorMessage(Protocol.CONX_REST));
		}


	}

	public static void main(String[] args) throws ClassNotFoundException {
		try {
			Server s = new Server();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
