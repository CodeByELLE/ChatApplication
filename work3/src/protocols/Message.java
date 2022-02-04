package protocols;

import modules.Chatter;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.List;

/*
defining the data structure that will be exchanged through the network
 */

public class Message implements Serializable {

    private String message;
    private int type;
    private int error;
    private Chatter dest = null;
    private PublicKey pk;
    private List<Chatter> chatters = null;
    private String signature;


    /*
     Class Contructor sending client/client
     */
    public Message(String message, int type, Chatter dest, String signature ){
        this.message = message;
        this.type = type;
        this.dest = dest;
        this.signature =signature;

    }

    /*
 Class Contructor sending client/Broadcast
 */
    public Message(String message, int type){
        this.message = message;
        this.type = type;

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

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Chatter getDest() {
		return dest;
	}

	public void setDest(Chatter dest) {
		this.dest = dest;
	}

	public PublicKey getPk() {
		return pk;
	}

	public void setPk(PublicKey pk) {
		this.pk = pk;
	}

	public List<Chatter> getChatters() {
		return chatters;
	}

	public void setChatters(List<Chatter> chatters) {
		this.chatters = chatters;
	}

	/*
Class Contructor sending sever/client
*/
    public Message(List<Chatter> chatters, int type, Chatter c){
       this.chatters = chatters;
       this.type = type;
       this.dest = c;
    }

    /*
Class Contructor disconnect
*/
    public Message(int type){
        this.type = type;
    }
    
    /*
Class Contructor Error
*/
    public Message(int type, int errorType){
        this.type = Protocol.ERROR;
        this.error = errorType;
        this.message = Protocol.getErrorMessage(errorType);
        
    }
    /*
Class Contructor to shqre public key
*/  
public Message (int type, PublicKey pk) {
	this.type = type;
	this.pk= pk;
}
}
