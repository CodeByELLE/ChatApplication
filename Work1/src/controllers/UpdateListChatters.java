package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

import modules.Chatter;
import protocols.Message;

public class UpdateListChatters extends Thread{

	private ObjectInputStream ois;
	private  Message myMessage;
	private Client client;

	// The constructor
	public UpdateListChatters(Client client, ObjectInputStream ois) {
		this.ois = ois;
		this.client = client;

	}

	public Message getM() {
		return myMessage;
	}

	public void setM(Message m) {
		this.myMessage = m;
	}

	// Receive the notification about new connected client
	synchronized public void ReceiveNotif() {
		try {	           
			while(true) {

				myMessage = (Message) ois.readObject();
				client.setChatterClient(myMessage.getDest());
				client.setChatters(myMessage.getChatters());
				System.out.println("Connected Chatters IDs :");
				for (Chatter element : client.getChatters()) {
					System.out.println("# Chatter " + element.getId());
				}
			}
		} catch (Exception e) {
			System.out.println("Can't receive update");
		}
	}

	public void run() {
		ReceiveNotif();
	}
}
