package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
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

	public ServerThread(Server server, Socket socket, Chatter c) throws IOException {
		this.socket =  socket;
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
		this.client = c;
		this.server =  server;
	}

	public int getIdDisc() {
		return idDisc;
	}

	public void setIdDisc(int idDisc) {
		this.idDisc = idDisc;
	}

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

	
	/* Receive disconnect request from client and remove it from the list of clients  
	 * and sent notification to all connected clients
	 */
	public void receiveDisc() throws ClassNotFoundException, IOException {
		try {

			Message discValue = (Message) ois.readObject();
			if(discValue.getType() == Protocol.disconnect().getType()) {
				System.out.println("Bye Bye Chatter" + client.getId());
				server.removeThCh(this);
				ois.close();
				this.socket.close();
				for (ServerThread sTh : server.getLstThread()) {
					sTh.notification(Protocol.clientList(server.getChatters(), sTh.getClient()));
				}
			}
		} catch (SocketException e) {
			// generate an error if a client quit the chat without requesting it
			System.out.println(Protocol.getErrorMessage(Protocol.CONX_REST));
		}
		setIdDisc(client.getId());;

	}

	public void run() {
		try {
			receiveDisc();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}			

}




