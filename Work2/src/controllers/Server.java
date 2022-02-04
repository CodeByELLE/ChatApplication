package controllers;

import modules.Chatter;
import protocols.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server {

	private ServerSocket socket;
	private List<Chatter> chatters;
	private List<ServerThread> lstThread;



	public Server() throws IOException, InterruptedException, ClassNotFoundException {
		this.socket = new ServerSocket(9065);
		this.chatters = new ArrayList<>();
		this.lstThread = new ArrayList<ServerThread>();

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
	// removes Thread from list of threads
	public void removeThCh(ServerThread th) {
		lstThread.remove(th);
		chatters.remove(th.getClient());
	}

	public void start() throws IOException, InterruptedException, ClassNotFoundException {
		//initiate chatters ID and increment it at every connection
		int id = 0;
		try {
			while (true) {
				System.out.println("server is waiting for connection ....");
				Socket trans = socket.accept();
				Chatter c = new Chatter(id, trans.getInetAddress());
				System.out.println("address" + trans.getInetAddress());
				chatters.add(c);
				// lanches the erver thread that 
				ServerThread processes = new ServerThread(this, trans, c);
				lstThread.add(processes);
				System.out.println("New Client connected : " + c.getId());
				for (int i = 0; i < lstThread.size(); i++) {
					lstThread.get(i).notification(Protocol.clientList(this.getChatters(), lstThread.get(i).getClient()));
					}
				processes.start();
				 //increment chatter ID at every connection
				id++;
			}
		} catch (IOException e) {
			System.out.println("Exception server");
		}
	}



	public static void main(String[] args) throws ClassNotFoundException {
		try {
			Server s = new Server();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
