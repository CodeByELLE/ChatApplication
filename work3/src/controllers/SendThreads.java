package controllers;

import modules.Chatter;
import java.util.Timer; 
import java.util.TimerTask;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import protocols.*;
import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;
import java.net.DatagramPacket;

public class SendThreads extends Thread{
	private Client client;

	SendThreads(Client client) {
		this.client = client;
	}

	public Chatter getChatterById(List<Chatter> chatters, int id){
		Chatter c = null;
		for (Chatter element : chatters)
			if (element.getId()== id){
				c=element;
				break;}
		return c;

	}
	
	public void operationChoice() throws Exception {
		String unicast ="";
		int dest;
		Chatter destChatter;
		Scanner sc = new Scanner(System.in);
		while(true){
			do{
				System.out.println("-- For private message type user ID.\n-- For broadcast type : -2."
						+ "\n-- For logout type : -1.\n >>");
				dest = Integer.parseInt(sc.next());
				destChatter = getChatterById(client.getChatters(),dest);
				if(dest == -1) {
					//Disconnect
					client.SendDisc();
					System.exit(0);			
				}
				else if(dest == -2) {
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    },
			    delay
			);
	}

	public void clientSend(Chatter dest) throws Exception {
		Scanner sc = new Scanner(System.in);
			System.out.print("Type your unicast message >> ");
			String unicast = sc.nextLine();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.flush();
				//encryption of the messaage to send by destination's public key
				String encryMsg = client.encryptMessage(unicast, dest.getPk());
				System.out.println("encry message sent :"+encryMsg);
				
				//sign the letter "A" by my private key
				String signed = client.sign("A", this.client.getPrivateKey());
				System.out.println("Signed msg : "+signed);
								
				this.client.getChatterClient().setPk(client.getPubKey());
				oos.writeObject(Protocol.sendEncryptedMsg(encryMsg,this.client.getChatterClient(), signed));
			} catch (IOException e) {
				e.printStackTrace();
			}
			// get the byte array of the object
			byte[] buf= baos.toByteArray();
			DatagramPacket dp = new DatagramPacket(buf,buf.length,dest.getAddress(),dest.getPort());
			delay(dp);		
	}

	

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
				//oos.close();
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

	//}
	}

	
	
	
	@Override
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
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



