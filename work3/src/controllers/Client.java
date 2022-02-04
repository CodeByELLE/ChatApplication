package controllers;

import modules.Chatter;
import protocols.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Client{

	private Chatter chatterClient;
	private List<Chatter> chatters;
	private DatagramSocket ds;
	private Socket mySocket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private PublicKey pubKey;
	private PrivateKey privateKey;
	
	private Cipher cipher;
	private KeyPairGenerator keyGen;
	private KeyPair pair;
	
 
	
	public Client() throws ClassNotFoundException, InterruptedException, IOException, NoSuchAlgorithmException {
  
		this.mySocket = new Socket("localhost", GlobalConstants.SERVER_PORT);
		this.ois = new ObjectInputStream(mySocket.getInputStream());
		this.oos = new ObjectOutputStream(mySocket.getOutputStream());

		start();

	}

	public DatagramSocket getDs() {
		return ds;
	}

	public void setDs(DatagramSocket ds) {
		this.ds = ds;
	}

	public List<Chatter> getChatters() {
		return chatters;
	}

	public void setChatters(List<Chatter> chatters) {
		this.chatters = chatters;
	}

	public PublicKey getPubKey() {
		return pubKey;
	}

	public void setPubKey(PublicKey pubKey) {
		this.pubKey = pubKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}


	public Chatter getChatterClient() {
		return chatterClient;
	}

	public void setChatterClient(Chatter chatterClient) {
		this.chatterClient = chatterClient;
	}

	public Socket getMySocket() {
		return mySocket;
	}

	public void setMySocket(Socket mySocket) {
		this.mySocket = mySocket;
	}

	
	public void generateKeys() throws NoSuchAlgorithmException {
		KeyPair keyPair = buildKeyPair();
		this.pubKey = keyPair.getPublic();
		this.privateKey = keyPair.getPrivate();
	}

	public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
		final int keySize = 1024;
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(keySize);      
		return keyPairGenerator.genKeyPair();
	}

	public static String encryptMessage(String msg, PublicKey key) 
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, IllegalBlockSizeException, 
			BadPaddingException, InvalidKeyException {
			Cipher cipher = Cipher.getInstance("RSA");
		 	cipher.init(Cipher.ENCRYPT_MODE, key);
		 	return Base64.getEncoder().encodeToString((cipher.doFinal(msg.getBytes("UTF-8"))));
	}

	public static String decryptMessage(String msg, PrivateKey key)
			throws InvalidKeyException, UnsupportedEncodingException, 
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		
			byte[] bytes = Base64.getDecoder().decode(msg);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return  new String(cipher.doFinal(bytes));
	}
	
	public static String encryptMessage(String msg, PrivateKey key) 
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, IllegalBlockSizeException, 
			BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("RSA");
		 	cipher.init(Cipher.ENCRYPT_MODE, key);
		 	return Base64.getEncoder().encodeToString((cipher.doFinal(msg.getBytes("UTF-8"))));
	}

	public static String decryptMessage(String msg, PublicKey key)
			throws InvalidKeyException, UnsupportedEncodingException, 
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		
			byte[] bytes = Base64.getDecoder().decode(msg);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return  new String(cipher.doFinal(bytes));
	}
	
	
	public static String sign(String plainText, PrivateKey privateKey) throws Exception {
	    Signature privateSignature = Signature.getInstance("SHA256withRSA");
	    privateSignature.initSign(privateKey);
	    privateSignature.update(plainText.getBytes());
	    byte[] signature = privateSignature.sign();
	    return Base64.getEncoder().encodeToString(signature);
	}
	
	
	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
	    Signature publicSignature = Signature.getInstance("SHA256withRSA");
	    publicSignature.initVerify(publicKey);
	    publicSignature.update(plainText.getBytes());
	    byte[] signatureBytes = Base64.getDecoder().decode(signature);
	    return publicSignature.verify(signatureBytes);
	}


	public void SendDisc() throws InterruptedException, ClassNotFoundException, IOException {
		try {
			oos.flush();
			System.out.println("disconnect request");
			oos.writeObject(Protocol.disconnect());
			oos.reset();
			System.out.println("Disconnect message type : " + Protocol.disconnect().getType());
		} catch (Exception e) {
			System.out.println("Can't send disconnect msg!!");
		}
	}

	//send my public key to server
	public void SendKey() throws InterruptedException, ClassNotFoundException, IOException {
		try {
			oos.flush();			
			oos.writeObject(Protocol.sendKey(this.pubKey));
			System.out.println("public key sent");
		} catch (Exception e) {
			System.out.println("Can't send public key!!");
		}
	}


	public void start()  throws InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {


		System.out.println("****************************");
		System.out.println("* Welcome to the chat room *");
		System.out.println("****************************");

		//generates (public and private keys) and sends the public key to the server
		generateKeys();
		SendKey();
		
		UpdateListChatters UpdateThread  =  new UpdateListChatters(this, ois);
		UpdateThread.start();

		Thread.sleep(2000); //wait forth updates to be made
		System.out.println("My Id is : " + this.getChatterClient().getId());

		this.ds = new DatagramSocket(this.getChatterClient().getPort());
		SendThreads p2psend = new SendThreads(this);
		p2psend.start();


		ListenThreads p2plisten = new ListenThreads(this, ds, oos);
		p2plisten.start();
	}

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException, IOException, NoSuchAlgorithmException {

		new Client();

	}
}