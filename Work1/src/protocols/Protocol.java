package protocols;


import modules.Chatter;
import java.util.List;

public abstract class Protocol {

     // global states
      public static final int SEND = 1;
      public static final int BROADCAST = 2;
      public static final int CLIENT_LIST = 3;
      public static final int DISCONNECT = 4;
      public static final int ERROR = 5;
      
      // Global Errors
      public static final int BAD_FORMAT = 1;
      public static final int UNKNOWN_TYPE = 2;
      public static final int CONNECTION_REFUSED = 3;
      public static final int MAX_CLIENTS_EXCEEDED = 4;
      public static final int BUFFER_SIZE_EXCEEDED = 5;
      public static final int CLIENT_NOT_FOUND = 6;
      public static final int CONX_REST = 7;
      
    		  
/*
 the send between clients (chatters)
 */
 public static Message send(String message, Chatter c){
  return new Message(message, SEND, c );
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

public static String getErrorMessage(int error) {
	switch(error) {
	case BAD_FORMAT:
		return "**ERROR**\nBAD_FORMAT "+ BAD_FORMAT +" : Message format is not supported";
	case UNKNOWN_TYPE:
		return "**ERROR**\nUNKNOWN_TYPE "+ UNKNOWN_TYPE +" : Operation not supported";
	case CONNECTION_REFUSED:
		return "**ERROR**\nCONNECTION_REFUSED "+ CONNECTION_REFUSED +" : Connection refused";
	case MAX_CLIENTS_EXCEEDED:
		return "**ERROR**\nMAX_CLIENTS_EXCEEDED "+ MAX_CLIENTS_EXCEEDED +" : Server can not handle more clients";
	case BUFFER_SIZE_EXCEEDED:
		return "**ERROR**\nBUFFER_SIZE_EXCEEDED "+ BUFFER_SIZE_EXCEEDED +" : Too long message";
	case CLIENT_NOT_FOUND:
		return "**ERROR**\nCLIENT_NOT_FOUND "+ CLIENT_NOT_FOUND +" : Client not found";
	case CONX_REST:
		return "**ERROR**\nCONX_REST "+ CONX_REST +" : Connection with client is interrupted";
	default:
		return "Unknown error";
	}
	
}






}
