package controllers;

import modules.Chatter;
import protocols.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
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

	public void setSocket(ServerSocket socket) {
		this.socket = socket;
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
// removes Thread from thread's list	
	public void removeThCh(ServerThread th) {
		lstThread.remove(th);
		chatters.remove(th.getClient());
	}
	//sets the Public Key for the current client
	public void setPublicKey(List<Chatter> chatters,int chatterId , PublicKey pubKey) {
		for(Chatter c : chatters) {
			if(c.getId() == chatterId) {				
				c.setPk(pubKey);
			}
		}
	}
	
	
	public void start() throws IOException, InterruptedException, ClassNotFoundException {
		int id = 0;		
		try {
			while (true) {
				System.out.println("server is waiting for connection ....");
				Socket trans = socket.accept();
				Chatter c = new Chatter(id, trans.getInetAddress());
				System.out.println("address" + trans.getInetAddress());
				chatters.add(c);
				ServerThread processes = new ServerThread(this, trans, c);
				lstThread.add(processes);
				processes.start();
				
				for (int i = 0; i < lstThread.size(); i++) {
					Thread.sleep(1000);
					lstThread.get(i).notification(Protocol.clientList(this.getChatters(), lstThread.get(i).getClient()));
					}			
				processes.join();
				System.out.println("New Client connected : " + c.getPk());
				id++;
			}
		} catch (IOException e) {
			System.out.println("exception server");
			System.out.println(e.getMessage());
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
