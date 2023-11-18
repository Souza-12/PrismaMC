package com.br.teagadev.prismamc.commons.common.serverinfo.types;

import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.google.gson.JsonObject;

public class NetworkServer {
   private ServerType serverType;
   private int maxPlayers;
   private int onlines;
   private int membersSlots;
   private int serverId;
   private int sequence;
   private boolean online;
   private boolean whiteList;
   private Long lastUpdate;

   public NetworkServer(ServerType serverType, int serverId) {
      this.serverId = serverId;
      this.serverType = serverType;
      this.maxPlayers = 80;
      this.onlines = 0;
      this.sequence = 0;
      this.membersSlots = 80;
      this.online = false;
      this.whiteList = false;
      this.lastUpdate = 0L;
   }

   public NetworkServer(ServerType serverType) {
      this(serverType, 1);
   }

   public void update(JsonObject json) {
      this.setOnlines(json.has("onlines") ? json.get("onlines").getAsInt() : 0);
      this.setMembersSlots(json.has("memberSlots") ? json.get("memberSlots").getAsInt() : 80);
      this.setMaxPlayers(json.has("maxPlayers") ? json.get("maxPlayers").getAsInt() : 80);
      this.setWhiteList(json.has("whiteList") && json.get("whiteList").getAsBoolean());
      this.setOnline(json.has("online") && json.get("online").getAsBoolean());
      if (json.has("lastUpdate") && this.isOnline()) {
         Long now = json.get("lastUpdate").getAsLong();
         if (now == -1L) {
            this.setOnline(false);
            this.setOnlines(0);
            this.setSequence(10);
         }

         if (now.equals(this.getLastUpdate())) {
            this.setSequence(this.getSequence() + 1);
            if (this.getSequence() > 8) {
               this.setOnline(false);
            }
         } else {
            this.setLastUpdate(now);
            this.setSequence(0);
            this.setOnline(true);
         }
      }

      if (!this.isOnline()) {
         this.setOnlines(0);
      }

   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("onlines", this.getOnlines());
      json.addProperty("maxPlayers", this.getMaxPlayers());
      json.addProperty("online", this.isOnline());
      json.addProperty("whiteList", this.isWhiteList());
      json.addProperty("lastUpdate", this.getLastUpdate());
      json.addProperty("membersSlots", this.getMembersSlots());
      return json;
   }

   public ServerType getServerType() {
      return this.serverType;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public int getOnlines() {
      return this.onlines;
   }

   public int getMembersSlots() {
      return this.membersSlots;
   }

   public int getServerId() {
      return this.serverId;
   }

   public int getSequence() {
      return this.sequence;
   }

   public boolean isOnline() {
      return this.online;
   }

   public boolean isWhiteList() {
      return this.whiteList;
   }

   public Long getLastUpdate() {
      return this.lastUpdate;
   }

   public void setServerType(ServerType serverType) {
      this.serverType = serverType;
   }

   public void setMaxPlayers(int maxPlayers) {
      this.maxPlayers = maxPlayers;
   }

   public void setOnlines(int onlines) {
      this.onlines = onlines;
   }

   public void setMembersSlots(int membersSlots) {
      this.membersSlots = membersSlots;
   }

   public void setServerId(int serverId) {
      this.serverId = serverId;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

   public void setOnline(boolean online) {
      this.online = online;
   }

   public void setWhiteList(boolean whiteList) {
      this.whiteList = whiteList;
   }

   public void setLastUpdate(Long lastUpdate) {
      this.lastUpdate = lastUpdate;
   }
}