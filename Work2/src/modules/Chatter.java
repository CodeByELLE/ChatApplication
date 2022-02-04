package modules;

import java.io.Serializable;
import java.net.InetAddress;




public class Chatter implements Serializable{
	 static final int GLOBAL_PORT = 3000;
     private int id;
     private String name;
     private InetAddress address;
     private int localport;

	public Chatter() {
	}

	public Chatter(int id, InetAddress address) {
    	 this.id = id;
    	 this.address = address;
    	 this.localport=id+GLOBAL_PORT;
	}
     
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() { return localport; }

	public void setPort() {	this.localport = GLOBAL_PORT+id; }


}
