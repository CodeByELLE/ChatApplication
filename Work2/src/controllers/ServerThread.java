package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import modules.Chatter;
import protocols.Message;
import protocols.Protocol;

public class ServerThread extends Thread{
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Chatter client;
	private int idDisc;
	private Server server;

	public int getIdDisc() {
		return idDisc;
	}

	public void setIdDisc(int idDisc) {
		this.idDisc = idDisc;
	}

	public ServerThread(Server server, Socket socket, Chatter c) throws IOException {
		this.socket =  socket;
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
		this.client = c;
		this.server =  server;
	}
	//Setters & Getters 
	public Chatter getClient() {
		return client;
	}

	public void setClient(Chatter client) {
		this.client = client;
	}


	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void notification(Message msg) {
		try {
			oos.flush();
			oos.writeObject(msg);
			oos.reset();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendListClients(Message msg) {
		try {
			OutputStream os =  this.socket.getOutputStream();
			ObjectOutputStream oos =  new ObjectOutputStream(os);
			oos.flush();
			oos.writeObject(msg);
			System.out.println("Unicast Message Type : "+msg.getType());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void receiveFromClient() throws ClassNotFoundException {
		try {

			Message msg = (Message) ois.readObject();
			if(msg.getType() == Protocol.disconnect().getType()) {
				System.out.println("Bye Bye Chatter" + client.getId());
				//removes the client from whom it gets the disconnect Message ad closes socket
				server.removeThCh(this);
				ois.close();
				this.socket.close();
				//Then sends a nw notification
				for (ServerThread sTh : server.getLstThread()) {
					sTh.notification(Protocol.clientList(server.getChatters(), sTh.getClient()));
					}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		setIdDisc(client.getId());;	
	}

	public void run() {
		try {
			receiveFromClient();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}			

}




