package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import modules.Chatter;
import protocols.Message;
import protocols.Protocol;

public class ServerThread extends Thread{
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Chatter chatter;
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
		this.chatter = c;
		this.server =  server;
	}

	public Chatter getClient() {
		return chatter;
	}

	public void setClient(Chatter client) {
		this.chatter = client;
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

	public void receiveFromClient() throws ClassNotFoundException, InterruptedException {
		try {

			Message testValue = (Message) ois.readObject();
			/*checks if the message type is DISCONNECT if so it processes 
			*the client disconnection and sends notificaion for 
			*the rest of the connected clients
			*/
			if(testValue.getType() == Protocol.disconnect().getType()) {
				System.out.println("Bye Bye Chatter" + chatter.getId());
				server.removeThCh(this);
				ois.close();
				this.socket.close();
				for (ServerThread sTh : server.getLstThread()) {
					sTh.notification(Protocol.clientList(server.getChatters(), sTh.getClient()));
					}
			}
			/*
			 * checks if the message type is KEY, if so
			 * sets the Public Key for the current client
			*/
			else if(testValue.getType() == Protocol.KEY) {
				server.setPublicKey(server.getChatters(), chatter.getId(), testValue.getPk());
				this.chatter.setPk(testValue.getPk());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			receiveFromClient();
		} catch (ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
	}			

}




