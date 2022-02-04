package controllers;

import modules.Chatter;
import protocols.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.List;

public class Client {

	private Chatter chatterClient;
	private List<Chatter> chatters;
	private DatagramSocket ds;
	private Socket mySocket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;


	public Chatter getChatterClient() {
		return chatterClient;
	}

	public void setChatterClient(Chatter chatterClient) {
		this.chatterClient = chatterClient;
	}

	public Socket getMySocket() {
		return mySocket;
	}

	public void setMySocket(Socket mySocket) {
		this.mySocket = mySocket;
	}

	public DatagramSocket getDs() {
		return ds;
	}

	public void setDs(DatagramSocket ds) {
		this.ds = ds;
	}

	public List<Chatter> getChatters() {
		return chatters;
	}

	public void setChatters(List<Chatter> chatters) {
		this.chatters = chatters;
	}

	// The constructor
	public Client() throws ClassNotFoundException, InterruptedException, IOException {
		try {
			this.mySocket = new Socket("localhost", GlobalConstants.SERVER_PORT);

		} catch (ConnectException e) {
			System.out.println(Protocol.getErrorMessage(Protocol.CONNECTION_REFUSED));
		}
		this.ois = new ObjectInputStream(mySocket.getInputStream());
		this.oos = new ObjectOutputStream(mySocket.getOutputStream());

		start();
	}


	// Send disconnection request
	public void SendDisc() throws InterruptedException, ClassNotFoundException, IOException {
		try {
			oos.flush();
			System.out.println("disconnect request");
			oos.writeObject(Protocol.disconnect());
			oos.reset();
			System.out.println("Disconnect message type : " + Protocol.disconnect().getType());
		} catch (Exception e) {
			System.out.println("Can't send disconnect msg!!");
		}
	}


	public void start()  throws InterruptedException, IOException, ClassNotFoundException {

		System.out.println("       ****************************");
		System.out.println("       * Welcome to the chat room *");
		System.out.println("       ****************************");

		// update the list of clients when a new client is connected
		UpdateListChatters UpdateThread  =  new UpdateListChatters(this, ois);
		UpdateThread.start();

		// wait until the list is updated
		Thread.sleep(1000);

		//print all connected clients
		System.out.println("My Id is : " + this.getChatterClient().getId());
		this.ds = new DatagramSocket(this.getChatterClient().getPort());

		// launch the thread in charge of the sending messages
		SendThreads p2psend = new SendThreads(this);
		p2psend.start();

		// launch the thread in charge of listening 
		ListenThreads p2plisten = new ListenThreads(ds, oos);
		p2plisten.start();
	}

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException, IOException {

		new Client();

	}
}