package modules;

import java.io.Serializable;
import java.net.InetAddress;
import java.security.PublicKey;


public class Chatter implements Serializable{
	 static final int GLOBAL_PORT = 3000;
     private int id;
     private String name;
     private InetAddress address;
     private int localport;
     private PublicKey pk;

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

	public PublicKey getPk() {
		return pk;
	}

	public void setPk(PublicKey pk) {
		this.pk = pk;
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
