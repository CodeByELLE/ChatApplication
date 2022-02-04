# ChatApplication
### Work1 :
#### Project structure :
**Controllers** :
* **Server** : The main task of the server is to accept connections from clients and then create a thread (serverThread) for each client, and sends the updated list of connected clients.
* **Client** : Sends disconnection request to server and create the SendThread and ListenThread to handle communication between clients.
 * *ServerThread* : This thread is created for each client, its job is to receive disconnection requests from clients and notify others about the new list. 
 * *ListenThreads* : Receives messages from other clients either they were unicast messages or Broadcast ones. 
* *SendThreads* : Sends unicast and broadcast messages.
* *UpdateListChatters* : Update the list of chatters if a new client joined or quit the chat.

**Modules** :
* *Chatter* : Provides the state f the user and it is used by the Client class to act on its behaviour. 

**Protocols** :
* *Global Constants* : States global constants.
* *Message* : States the different forms of message used in communication either between clients or between clients and server.
* *Protocol* : represents the protocol followed in this work, it describes the different messages and functions used for communication also the errors managed.

### Work2 :
In order to create desorded messages, we added a new function delay in "sendThread" to send messages in a random delay.
to deal with the misordered messages, we identify each message by a sequence number related to each client.
At the reception side, we check for each client the sequence of the received message, and we verify if it is the expected sequence, if so, we print it, else we store the messages until we get the right sequence.

### Work3 :
To ensure the security of this application we used two methods:
* *Encryption* : To verify the message has not changed , each client creates two keys for communication (public key and private key), the sender encrypt the message with the public key of the receiver who will decrypted it with his private key.
* *Signature* : To ensure that a message is received from the right client, the signature is added to the ecrypted message(with the private key of the sender), the receiver will verify the received signed message using the public key of the sender, and decide if it is a malicious client or not.
