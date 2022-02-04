package controllers;

import modules.Chatter;
import java.util.Timer; 
import java.util.TimerTask;
import protocols.*;
import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.List;
import java.util.Scanner;
import java.net.DatagramPacket;


public class SendThreads extends Thread{
	
	private Client client;

	// Class constructor
	public SendThreads(Client client) {
		this.client = client;
	}
	
// Returns the Chatter corresponding the a given ID froom the list of chatters
	public Chatter getChatterById(List<Chatter> chatters, int id){
		Chatter c = null;
		for (Chatter element : chatters)
			if (element.getId()== id){
				c=element;
				break;}
		return c;
	}
	
	public void operationChoice() throws ClassNotFoundException, InterruptedException, IOException {
		String unicast ="";
		int choice;
		Chatter destChatter;
		Scanner sc = new Scanner(System.in);
		while(true){
			do{ // loop until it gets a valid choice
				// Menu of choices	
				System.out.println("-- For private message type user ID.\n-- For broadcast type : -2."
						+ "\n-- For logout type : -1.\n >>");
				// get the user choice
				choice = Integer.parseInt(sc.next());
				destChatter = getChatterById(client.getChatters(),choice);
				
				if(choice == -1) {
					//Disconnect
					client.SendDisc();
					System.exit(0);	
				}
				else if(choice == -2) {
					//Broadcast
					this.broadcast();	
				}
				else {
					this.clientSend(destChatter);
				}
			}while (!client.getChatters().contains(destChatter));
		}
	}
	
	// generate random delay to execute ClientSend	
	public void delay(DatagramPacket dp) {
		 Random random = new Random();
		int delay = random.nextInt((GlobalConstants.MAX_DELAY - GlobalConstants.MIN_DELAY) + 1) + GlobalConstants.MIN_DELAY;
		new Timer().schedule(  
			    new TimerTask() {
			        @Override
			        public void run() {
			            try {
			            	client.getDs().send(dp);
						} catch (IOException e) {
							e.printStackTrace();
						}
			        }
			    },
			    delay
			);
	}

	
	public void clientSend(Chatter dest) throws ClassNotFoundException, InterruptedException, IOException {
		
			Scanner sc = new Scanner(System.in);
			System.out.print("Type your unicast message >> ");
			String unicast = sc.nextLine();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.flush();
				Message m = Protocol.send(unicast,dest, this.client.getChatterClient());
				// sends a message and stores the sending sequence order
				int seq = this.client.getSequenceSendOrder().get(dest.getId());
				m.setSequence(seq);
				this.client.getSequenceSendOrder().put(dest.getId(), seq+1);
				oos.writeObject(m);
			} catch (IOException e) {
					e.printStackTrace();
			}
			// get the byte array of the object
			byte[] buf= baos.toByteArray();
			DatagramPacket dp = new DatagramPacket(buf,buf.length,dest.getAddress(),dest.getPort());
			delay(dp);	
	}

	
// Sends broadcast messages
	public void broadcast() throws SocketException {
		String broadMsg ="";
		Scanner sc = new Scanner(System.in);
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

		
	@Override
	public void run() {
		try {
			operationChoice();
			 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}



