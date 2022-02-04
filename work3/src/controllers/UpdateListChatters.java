package controllers;
import modules.Chatter;
import protocols.Message;

import java.io.ObjectInputStream;


public class UpdateListChatters extends Thread{
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
	  synchronized public void ReceiveNotif() {
		   try {	           
	            while(true) {
	            	
	                myMessage = (Message) ois.readObject();
	                
	                client.setChatterClient(myMessage.getDest());
	                client.setChatters(myMessage.getChatters());
	                System.out.println("Connected Chatters IDs :");
	                for (Chatter element : client.getChatters()) {
	                    System.out.println("# Chatter " + element.getId());
	                    System.out.println("#####" + element.getPk());
	                }	                
	            }
	        } catch (Exception e) {
	            System.out.println("Can't receive update");
	            System.out.println(e.getMessage());
	        }
	    }
	  
	  public void run() {
		  ReceiveNotif();
	  }


}
