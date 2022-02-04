package protocols;


import modules.Chatter;

import java.security.PublicKey;
import java.util.List;

public abstract class Protocol {

	// global states
	public static final int ENCRYPTED_MSG = 1;
	public static final int BROADCAST = 2;
	public static final int CLIENT_LIST = 3;
	public static final int DISCONNECT = 4;
	public static final int ERROR = 5;
	public static final int KEY = 6;

	// Global Errors
	public static final int BAD_FORMAT = 1;
	public static final int UNKNOWN_TYPE = 2;
	public static final int CONNECTION_REFUSED = 3;
	public static final int MAX_CLIENTS_EXCEEDED = 4;
	public static final int BUFFER_SIZE_EXCEEDED = 5;
	public static final int CLIENT_NOT_FOUND = 6;

	/*
      Send encrypted message
	 */
	public static Message sendEncryptedMsg(String message, Chatter c, String signature){
		return new Message(message, ENCRYPTED_MSG, c, signature );
	}
	/*
 broadcast sending
	 */
	public static Message broadcast(String message){
		return new Message(message, BROADCAST);
	}
	/*
 clients list sending
	 */
	public static Message clientList(List<Chatter> chatters, Chatter c){
		return new Message(chatters, CLIENT_LIST, c);
	}
	/*
 disconnect request
	 */
	public static Message disconnect(){
		return new Message(DISCONNECT);
	}

	/*
 Error message
	 */
	public static Message error(int errorType){
		return new Message(ERROR, errorType );
	}


	/* 
	 * Public Key message
	 */
	public static Message sendKey(PublicKey pk){
		return new Message(KEY, pk );
	}

	public static String getErrorMessage(int error) {
		switch(error) {
		case BAD_FORMAT:
			return "BAD_FORMAT"+ BAD_FORMAT +"\nMessage format is not supported";
		case UNKNOWN_TYPE:
			return "UNKNOWN_TYPE"+ UNKNOWN_TYPE +"\nOperation not supported";
		case CONNECTION_REFUSED:
			return "CONNECTION_REFUSED"+ CONNECTION_REFUSED +"\nConnection refused";
		case MAX_CLIENTS_EXCEEDED:
			return "MAX_CLIENTS_EXCEEDED"+ MAX_CLIENTS_EXCEEDED +"\nServer can not handle more clients";
		case BUFFER_SIZE_EXCEEDED:
			return "BUFFER_SIZE_EXCEEDED"+ BUFFER_SIZE_EXCEEDED +"\nToo long message";
		case CLIENT_NOT_FOUND:
			return "CLIENT_NOT_FOUND"+ CLIENT_NOT_FOUND +"\nClient not found";
		default:
			return "Unknown error";
		}

	}






}
