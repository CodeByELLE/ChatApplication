package controllers;

import protocols.*;

import java.io.*;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.lang.Thread;
public class ListenThreads extends Thread {
	private DatagramSocket ds;
	private ObjectOutputStream oos;

	// Constructor
	public ListenThreads(DatagramSocket ds, ObjectOutputStream oos) {
		this.oos = oos;
		this.ds = ds;
	}

	// Receive message from peer client
	public Message receiveUnicastMsg() throws IOException, ClassNotFoundException {
		while (true) {
			byte[] buf = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buf, 1024);
			try {

				ds.receive(dp);
				ByteArrayInputStream baos = new ByteArrayInputStream(buf);
				ObjectInputStream oos = new ObjectInputStream(baos);
				Message m = (Message) oos.readObject();
				System.out.println("message from : "+ (dp.getPort()- GlobalConstants.GLOBAL_PORT)+" >> " + m.getMessage());

			} catch (StreamCorruptedException e) {
				// Generate an error message id the message length is larger that the buffer size
				System.out.println(Protocol.getErrorMessage(Protocol.BUFFER_SIZE_EXCEEDED));
			}
		}
	}

	// Receive broadcasted message 
	public Message receiveBroadcastMsg() throws IOException, ClassNotFoundException {
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
			receiveUnicastMsg();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

}
