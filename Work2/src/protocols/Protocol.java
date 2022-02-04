package protocols;


import modules.Chatter;

import java.util.List;

public abstract class Protocol {

    // global states
    public static final int SEND = 1;
    public static final int BROADCAST = 2;
    public static final int CLIENT_LIST = 3;
    public static final int DISCONNECT = 4;

    /*
     the send between clients (chatters)
     */
    public static Message send(String message, Chatter c , Chatter sender ) {
        return new Message(message, SEND, c , sender);
    }

    /*
    broadcast sending
     */
    public static Message broadcast(String message) {
        return new Message(message, BROADCAST);
    }

    /*
    clients list sending
     */
    public static Message clientList(List<Chatter> chatters, Chatter c) {
        return new Message(chatters, CLIENT_LIST, c);
    }

    /*
    disconnect request
     */
    public static Message disconnect() {
        return new Message(DISCONNECT);
    }


}
