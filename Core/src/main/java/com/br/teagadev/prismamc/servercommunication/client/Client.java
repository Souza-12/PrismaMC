package com.br.teagadev.servercommunication.client;

import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.ServerCommunicationInstance;

public class Client {
   private static Client instance;
   private String clientName;
   private int clientID;
   private ClientConnection clientConnection;

   public Client(String hostName, String clientName, int clientID) {
      instance = this;
      ServerCommunication.INSTANCE = ServerCommunicationInstance.CLIENT;
      this.setClientName(clientName);
      this.setClientID(clientID);
      this.setClientConnection(new ClientConnection(hostName));
   }

   public static boolean isInstanced() {
      return instance != null;
   }

   public String getClientName() {
      return this.clientName;
   }

   public int getClientID() {
      return this.clientID;
   }

   public ClientConnection getClientConnection() {
      return this.clientConnection;
   }

   public void setClientName(String clientName) {
      this.clientName = clientName;
   }

   public void setClientID(int clientID) {
      this.clientID = clientID;
   }

   public void setClientConnection(ClientConnection clientConnection) {
      this.clientConnection = clientConnection;
   }

   public static Client getInstance() {
      return instance;
   }
}