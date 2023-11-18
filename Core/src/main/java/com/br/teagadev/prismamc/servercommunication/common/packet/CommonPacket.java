package com.br.teagadev.servercommunication.common.packet;

import com.google.gson.JsonObject;
import java.net.Socket;

public abstract class CommonPacket {
   private JsonObject json;
   private String serverName;
   private int serverID;
   private Long timestamp;

   public abstract void read(JsonObject var1);

   public abstract void write();

   public abstract void handle(Socket var1);

   public abstract String getJSONString();

   public abstract String getPacketName();

   public abstract String getDebugReceived();

   public JsonObject getJson() {
      return this.json;
   }

   public String getServerName() {
      return this.serverName;
   }

   public int getServerID() {
      return this.serverID;
   }

   public Long getTimestamp() {
      return this.timestamp;
   }

   public void setJson(JsonObject json) {
      this.json = json;
   }

   public void setServerName(String serverName) {
      this.serverName = serverName;
   }

   public void setServerID(int serverID) {
      this.serverID = serverID;
   }

   public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
   }
}