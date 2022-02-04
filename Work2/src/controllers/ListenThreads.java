package controllers;

import modules.Chatter;
import protocols.*;

import java.io.*;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.lang.Thread;


public class ListenThreads extends Thread {
	private DatagramSocket ds;
	private ObjectOutputStream oos;
	private Client client;

	// class constructor
	public ListenThreads(Client client, DatagramSocket ds, ObjectOutputStream oos) {
		this.oos = oos;
		this.ds = ds;
		this.client =  client;

	}

	public Message clientReceive() throws IOException, ClassNotFoundException {
		while (true) {
			byte[] buf = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buf, 1024);
			ds.receive(dp);
			ByteArrayInputStream baos = new ByteArrayInputStream(buf);
			ObjectInputStream oos = new ObjectInputStream(baos);
			Message m = (Message) oos.readObject();
			int idSender = m.getIdSender();
			//Mapping the sequence of the received Message to the current sender
			client.getSequenceMap().get(idSender).put(m.getSequence(), m.getMessage()); 
			//start test if message is ordered
			while( true  ) { 
				
				/*if sequence of the Message (that correspond to idSender)is equal to the expected  
				 * sequence to be received (which is stored in the expectedSequence)
				 * if so, get the message content from the received Message object and print it, 
				 * remove it from map of expectedSequence and increment the expected sequence 
				 * else go back to reception (receive and store).
				 */
				if (  client.getSequenceMap().get(idSender).containsKey(client.getSequenceReceiveOrder().get(idSender))  ) {
					String msg =  client.getSequenceMap().get(idSender).get(client.getSequenceReceiveOrder().get(idSender));
					
					System.out.println("message from : "+ (dp.getPort()- GlobalConstants.GLOBAL_PORT)+" >> " + msg);
					System.out.println("MSG SEQUENCE :"+client.getSequenceReceiveOrder().get(idSender));
					client.getSequenceMap().get(idSender).remove(client.getSequenceReceiveOrder().get(idSender));
					client.getSequenceReceiveOrder().put(idSender, client.getSequenceReceiveOrder().get(idSender)+1);	
				}
				else {
					break;
				}
			}
		}
	}
	// No desorder is applied on broadcast messages
	public Message receiveBroadcast() throws IOException, ClassNotFoundException {
		System.out.println("Broadcast message...");
		while (true) {
			byte[] buf = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buf, 1024);
			try {
				ds.receive(dp);
				ByteArrayInputStream baos = new ByteArrayInputStream(buf);
				ObjectInputStream oos = new ObjectInputStream(baos);
				Message m = (Message) oos.readObject();
				System.out.println("message from : "+ (dp.getPort()- GlobalConstants.GLOBAL_PORT)+" >> " + m.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		try {
			clientReceive();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

}
