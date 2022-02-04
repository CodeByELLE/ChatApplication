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
    private int sequence;
    private int idSender;
    private Chatter dest = null;
    private List<Chatter> chatters = null;
    
    
    


    public int getIdSender() {
		return idSender;
	}

	public void setIdSender(int idSender) {
		this.idSender = idSender;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setError(int error) {
		this.error = error;
	}

	public void setDest(Chatter dest) {
		this.dest = dest;
	}

	public void setChatters(List<Chatter> chatters) {
		this.chatters = chatters;
	}

	/*
     Class Contructor sending client/client
     */
    public Message(String message, int type, Chatter dest , Chatter sender) {
        this.message = message;
        this.type = type;
        this.dest = dest;
        this.idSender = sender.getId();
        this.sequence = 0;
    }

    /*
 Class Contructor sending client/Broadcast
 */
    public Message(String message, int type) {
        this.message = message;
        this.type = type;

    }

    /*
Class Contructor sending sever/client
*/
    public Message(List<Chatter> chatters, int type, Chatter c) {
        this.chatters = chatters;
        this.type = type;
        this.dest = c;
    }

    /*
Class Contructor sending client/Server
*/
    public Message(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public int getError() {
        return error;
    }

    public Chatter getDest() {
        return dest;
    }

    public List<Chatter> getChatters() {
        return chatters;
    }

    @Override
    public String toString() {
        return "Message{" +
                ", dest=" + dest +
                ", chatters=" + chatters +
                '}';
    }
}
