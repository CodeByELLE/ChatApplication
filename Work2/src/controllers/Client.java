package controllers;

import modules.Chatter;
import protocols.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Client{

	private Chatter chatterClient;
	private List<Chatter> chatters;
	private DatagramSocket ds;
	private Socket mySocket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	//Sequence variables
	private Map<Integer, HashMap<Integer, String>> sequenceMap;
	private Map<Integer, Integer> expectedSequence;
	private Map<Integer, Integer> sequenceSendOrder;

	/*
	 *  Client constructor initialises socket, ois, oos and sequence variables
	 *  Lanches the start method on the created object
	 */
	
	public Client() throws ClassNotFoundException, InterruptedException, IOException {
		sequenceMap = new HashMap<Integer,HashMap<Integer, String>>();
		expectedSequence = new HashMap<Integer, Integer>();
		sequenceSendOrder = new HashMap<Integer, Integer>();


		this.mySocket = new Socket("localhost", GlobalConstants.SERVER_PORT);
		this.ois = new ObjectInputStream(mySocket.getInputStream());
		this.oos = new ObjectOutputStream(mySocket.getOutputStream());

		start();
	}

	// Getters & Setters	
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

	public Map<Integer, HashMap<Integer, String>> getSequenceMap() {
		return sequenceMap;
	}
	public void setSequenceMap(Map<Integer, HashMap<Integer, String>> sequenceMap) {
		this.sequenceMap = sequenceMap;
	}
	public Map<Integer, Integer> getSequenceReceiveOrder() {
		return expectedSequence;
	}
	public void setSequenceReceiveOrder(Map<Integer, Integer> sequenceReceiveOrder) {
		this.expectedSequence = sequenceReceiveOrder;
	}
	public Map<Integer, Integer> getSequenceSendOrder() {
		return sequenceSendOrder;
	}
	public void setSequenceSendOrder(Map<Integer, Integer> sequenceSendOrder) {
		this.sequenceSendOrder = sequenceSendOrder;
	}



	/*Sends the disconnection message*/
	 
	
	public void SendDisc() throws InterruptedException, ClassNotFoundException, IOException {
		try {

			oos.flush();
			System.out.println("I'm leaving the chatroom!!");
			oos.writeObject(Protocol.disconnect());
			oos.reset();
		} catch (Exception e) {
			System.out.println("Can't send disconnect msg!!");
		}
	}

	/*intialises the sequence variables for a new client*/
	
	
	public void initSequences( Client client) {
		for(Chatter c : client.chatters ) {
			if( !( client.expectedSequence.containsKey( c.getId() ) ) ) {
				client.expectedSequence.put(c.getId(), 0);
				client.sequenceMap.put(c.getId(), new HashMap<Integer, String>());
				client.sequenceSendOrder.put(c.getId(), 0);
			}
		}
	}

	/**/
	public void start()  throws InterruptedException, IOException, ClassNotFoundException {
		/// Welcoming message
		System.out.println("****************************");
		System.out.println("* Welcome to the chat room *");
		System.out.println("****************************");
		
		// Launch thread that receives list updates from server
		UpdateListChatters UpdateThread  =  new UpdateListChatters(this, ois);
		UpdateThread.start();
		
		Thread.sleep(200); // waits for the updates to be made
		
		// current chatter ID
		System.out.println("My Id is : " + this.getChatterClient().getId()); 

		//Initiates datagramSock for communication with peers
		this.ds = new DatagramSocket(this.getChatterClient().getPort());

		// Launch thread that sends messages to chatters
		SendThreads p2psend = new SendThreads(this);
		p2psend.start();

		// Launch thread that receives messages from the other chaatters
		ListenThreads p2plisten = new ListenThreads(this,ds, oos);
		p2plisten.start();
	}

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException, IOException {

		new Client();

	}
}