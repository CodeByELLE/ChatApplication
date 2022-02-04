package controllers;
import modules.Chatter;
import protocols.Message;

import java.io.ObjectInputStream;


public class UpdateListChatters extends Thread{
	/*
	 * Receives connected clients updates
	 */
	
    private ObjectInputStream ois;
    private  Message myMessage;
    private Client client;
    

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
	public void ReceiveNotif() {
		   try {	           
	            while(true) {	            	
	                myMessage = (Message) ois.readObject();
	                client.setChatterClient(myMessage.getDest());
	                client.setChatters(myMessage.getChatters());
	                //for each client newly added to the list initiaates the sequences variables
	                client.initSequences(client);
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
