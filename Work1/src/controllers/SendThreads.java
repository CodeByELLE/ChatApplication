package controllers;

import modules.Chatter;
import protocols.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.List;
import java.util.Scanner;
import java.net.DatagramPacket;

public class SendThreads extends Thread{
	private Client client;


	// the constructor
	public SendThreads(Client client) {
		this.client = client;
	}

	// return a chatter from the list of chatters by his ID
	public Chatter getChatterById(List<Chatter> chatters, int id){
		Chatter c = null;
		for (Chatter element : chatters)
			if (element.getId()== id){
				c=element;
				break;}
		return c;

	}

	// get the choice of the client and act based on it
	public void operationChoice() throws ClassNotFoundException, InterruptedException, IOException {
		int choice= 1000;
		Chatter destChatter;
		Scanner sc = new Scanner(System.in);
		System.out.println("--------");
		System.out.println(" Choices");
		System.out.println("--------");
		while(true){
			do{
				System.out.println("-- For private message type user ID.\n-- For broadcast type : -2.\n-- For logout type : -1.\n >>");

				//read the choice of the client
				try {
					choice = Integer.parseInt(sc.next());
				}
				catch(NumberFormatException e){
					// generate an error if te client typed something other than Integer
					System.out.println(Protocol.getErrorMessage(Protocol.BAD_FORMAT));
				}

				// get the chatter by his ID
				destChatter = getChatterById(client.getChatters(),choice);

				// if choice equals -1 then call Disconnect
				if(choice == -1) {
					client.SendDisc();
					System.exit(0);

				}

				// if choice equals -2 then call Broadcast
				else if(choice == -2) {
					this.sendBroadcast();

				}

				// if the choice is another Integer (ID) then it's a communication between clients
				else if(client.getChatters().contains(destChatter)) {
					this.sendMsg(destChatter);	
				}

				// if the ID does not exist in list the generate an error message
				else {
					System.out.println(Protocol.getErrorMessage(Protocol.CLIENT_NOT_FOUND));
				}
			}while (!client.getChatters().contains(destChatter));
		}
	}

	// Communication between peer
	public void sendMsg(Chatter dest) throws ClassNotFoundException, InterruptedException, IOException {

		Scanner sc = new Scanner(System.in);

		System.out.print("Type your unicast message >> ");
		String unicast = sc.nextLine();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.flush();
			oos.writeObject(Protocol.send(unicast,dest));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// get the byte array of the object
		byte[] buf= baos.toByteArray();

		DatagramPacket dp = new DatagramPacket(buf,buf.length,dest.getAddress(),dest.getPort());

		try {
			client.getDs().send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Broadcast the message typed by a client
	public void sendBroadcast() throws SocketException {

		String broadMsg ="";
		Scanner sc = new Scanner(System.in);
		//while(true){
		System.out.print("Type your broadcast message >> ");
		broadMsg = sc.nextLine();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.flush();
			oos.writeObject(Protocol.broadcast(broadMsg));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// get the byte array of the object
		byte[] buf= baos.toByteArray();
		for(Chatter c  : client.getChatters()) {
			DatagramPacket dp = new DatagramPacket(buf,buf.length,c.getAddress(),c.getPort());
			try {
				client.getDs().send(dp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {

		try {
			operationChoice();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



