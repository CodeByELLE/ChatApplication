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

	public ListenThreads(Client client, DatagramSocket ds, ObjectOutputStream oos) {
		this.client =  client;
		this.oos = oos;
		this.ds = ds;

	}

	public Message receiveUnicast() throws Exception {
		while (true) {
			byte[] buf = new byte[2048];
			DatagramPacket dp = new DatagramPacket(buf, 2048);
			try {
				ds.receive(dp);
				ByteArrayInputStream baos = new ByteArrayInputStream(buf);
				ObjectInputStream oos = new ObjectInputStream(baos);
				Message m = (Message) oos.readObject();
				//decrypt the received message by my private key
				String decryMsg = client.decryptMessage(m.getMessage(), this.client.getPrivateKey());
				
				Boolean isSigned =  client.verify("A", m.getSignature(), m.getDest().getPk());
				/*verify signature of the received message by destination's public key
				 * if verified print msg if not it is malicious client 
				 */
				if(isSigned == true) {
					System.out.println("message from : "+ (dp.getPort()- GlobalConstants.GLOBAL_PORT)+" >> " + decryMsg);
				}
				else {
					System.out.println("Malicious client");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

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
	public void run(){
	
			try {
				receiveUnicast();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

}
