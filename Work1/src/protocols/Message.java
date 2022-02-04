package protocols;

import modules.Chatter;

import java.io.Serializable;
import java.util.List;

/*
defining the data structure that will be exchanged through the network
 */

public class Message implements Serializable {

	private String message;
	private int type;
	private int error;
	private Chatter dest = null;

	private List<Chatter> chatters = null;



	// Class Constructor sending client/client
	public Message(String message, int type, Chatter dest ){
		this.message = message;
		this.type = type;
		this.dest = dest;

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public Chatter getDest() {
		return dest;
	}

	public void setDest(Chatter dest) {
		this.dest = dest;
	}

	public List<Chatter> getChatters() {
		return chatters;
	}

	public void setChatters(List<Chatter> chatters) {
		this.chatters = chatters;
	}

	// Class Constructor sending client/Broadcast
	public Message(String message, int type){
		this.message = message;
		this.type = type;

	}

	// Class Constructor sending sever/client
	 
	public Message(List<Chatter> chatters, int type, Chatter c){
		this.chatters = chatters;
		this.type = type;
		this.dest = c;
	}

	// Class Constructor disconnect
	public Message(int type){
		this.type = type;
	}

	
	// Class Constructor Error
	public Message(int type, int errorType){
		this.type = Protocol.ERROR;
		this.error = errorType;
		this.message = Protocol.getErrorMessage(errorType);
	}
}
