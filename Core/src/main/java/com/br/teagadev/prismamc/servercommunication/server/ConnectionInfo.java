package com.br.teagadev.servercommunication.server;

public class ConnectionInfo {
   private String address;
   private int connectionsPerSecond;

   public ConnectionInfo(String address) {
      this.address = address;
   }

   public void addConnection() {
      ++this.connectionsPerSecond;
      if (this.connectionsPerSecond >= 6) {
      }

   }

   public String getAddress() {
      return this.address;
   }

   public int getConnectionsPerSecond() {
      return this.connectionsPerSecond;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public void setConnectionsPerSecond(int connectionsPerSecond) {
      this.connectionsPerSecond = connectionsPerSecond;
   }
}