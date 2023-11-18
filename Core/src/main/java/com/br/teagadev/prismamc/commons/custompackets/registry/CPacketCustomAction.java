package com.br.teagadev.prismamc.commons.custompackets.registry;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.common.PluginInstance;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.common.packet.CommonPacket;
import com.google.gson.JsonObject;
import java.net.Socket;
import java.util.UUID;

public class CPacketCustomAction extends CommonPacket {
   private UUID uniqueId;
   private String nick;
   private String field;
   private String fieldValue;
   private String extraValue;
   private String extraValue2;
   private PacketType packetType;
   private ServerType serverType;

   public CPacketCustomAction() {
   }

   public CPacketCustomAction(String nick, UUID uniqueId) {
      this.nick = nick;
      this.uniqueId = uniqueId;
      this.setJson(new JsonObject());
      this.getJson().addProperty("uniqueId", uniqueId == null ? "INVALID UUID" : uniqueId.toString());
      this.getJson().addProperty("nick", nick == null ? "INVALID NICK" : nick);
      this.write();
   }

   public CPacketCustomAction(String nick) {
      this(nick, (UUID)null);
   }

   public CPacketCustomAction(UUID uniqueId) {
      this((String)null, uniqueId);
   }

   public CPacketCustomAction(ServerType type, int serverId) {
      this.serverType = type;
      this.setServerID(serverId);
      this.setServerName(type.getName());
      this.setJson(new JsonObject());
      this.getJson().addProperty("uniqueId", this.uniqueId == null ? "INVALID UUID" : this.uniqueId.toString());
      this.getJson().addProperty("nick", this.nick == null ? "INVALID NICK" : this.nick);
      this.write();
   }

   public void read(JsonObject json) {
      this.setJson(json);
      this.uniqueId = null;
      this.nick = null;
      this.setServerName(json.get("serverName").getAsString());
      this.setServerID(json.get("serverID").getAsInt());
      if (json.has("nick") && !json.get("nick").getAsString().equalsIgnoreCase("INVALID NICK")) {
         this.nick = json.get("nick").getAsString();
      }

      if (json.has("uniqueId")) {
         String uuidString = json.get("uniqueId").getAsString();
         if (!uuidString.equalsIgnoreCase("INVALID UUID")) {
            this.uniqueId = UUID.fromString(uuidString);
         }
      }

      if (json.has("packetType")) {
         this.packetType = PacketType.getType(json.get("packetType").getAsString());
      }

      if (json.has("field")) {
         this.field = json.get("field").getAsString();
      }

      if (json.has("fieldValue")) {
         this.fieldValue = json.get("fieldValue").getAsString();
      }

      if (json.has("extraValue")) {
         this.extraValue = json.get("extraValue").getAsString();
      }

      if (json.has("extraValue2")) {
         this.extraValue2 = json.get("extraValue2").getAsString();
      }

      if (json.has("timeStamp")) {
         this.setTimestamp(json.get("timeStamp").getAsLong());
      }

      if (json.has("serverType")) {
         this.serverType = ServerType.getServer(json.get("serverType").getAsString());
      }

   }

   public CPacketCustomAction type(PacketType type) {
      this.check();
      this.getJson().addProperty("packetType", type.name().toUpperCase());
      return this;
   }

   private CPacketCustomAction check() {
      if (this.getJson() == null) {
         this.setJson(new JsonObject());
      }

      if (this.serverType == null) {
         if (CommonsGeneral.getPluginInstance() == PluginInstance.BUNGEECORD) {
            this.setServerID(1);
            this.serverType = ServerType.BUNGEECORD;
         } else {
            this.serverType = BukkitMain.getServerType();
            this.setServerID(BukkitMain.getServerID());
         }
      }

      this.setServerName(this.getServerType().getName());
      if (!this.getJson().has("serverName")) {
         this.getJson().addProperty("serverName", this.getServerName());
      }

      if (!this.getJson().has("serverID")) {
         this.getJson().addProperty("serverID", this.getServerID());
      }

      if (!this.getJson().has("packetName")) {
         this.getJson().addProperty("packetName", this.getPacketName());
      }

      if (!this.getJson().has("timeStamp")) {
         this.getJson().addProperty("timeStamp", System.currentTimeMillis());
      }

      if (!this.getJson().has("serverType")) {
         this.getJson().addProperty("serverType", this.getServerType().getName());
      }

      return this;
   }

   public CPacketCustomAction field(String value) {
      this.check();
      this.getJson().addProperty("field", value);
      return this;
   }

   public CPacketCustomAction fieldValue(String value) {
      this.getJson().addProperty("fieldValue", value);
      return this;
   }

   public CPacketCustomAction extraValue(String value) {
      this.getJson().addProperty("extraValue", value);
      return this;
   }

   public CPacketCustomAction extraValue2(String value) {
      this.getJson().addProperty("extraValue2", value);
      return this;
   }

   public void write() {
      this.check();
   }

   public void handle(Socket socket) {
      ServerCommunication.getPacketHandler().handleCPacketPlayerAction(this, socket);
   }

   public String getPacketName() {
      return "CPacketCustomAction";
   }

   public String getJSONString() {
      return this.getJson().toString();
   }

   public String getValues() {
      StringBuilder stringBuilder = new StringBuilder();
      if (this.getPacketType() != null) {
         stringBuilder.append("packetType: ").append(this.getPacketType().name().toUpperCase());
      }

      if (this.getField() != null) {
         stringBuilder.append(", field: ").append(this.getField());
      }

      if (this.getFieldValue() != null) {
         stringBuilder.append(", fieldValue: ").append(this.getFieldValue());
      }

      if (this.getExtraValue() != null) {
         stringBuilder.append(", extraValue: ").append(this.getExtraValue());
      }

      if (this.getExtraValue2() != null) {
         stringBuilder.append(", extraValue2: ").append(this.getExtraValue2());
      }

      return stringBuilder.toString();
   }

   public String getDebugReceived() {
      String from = this.getServerName() + "-" + this.getServerID();
      long time = System.currentTimeMillis() - this.getTimestamp();
      return "(" + this.getPacketName() + ") has been received! (" + time + " ms) from: (" + from + ") with values: (" + this.getValues() + ")";
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public String getNick() {
      return this.nick;
   }

   public String getField() {
      return this.field;
   }

   public String getFieldValue() {
      return this.fieldValue;
   }

   public String getExtraValue() {
      return this.extraValue;
   }

   public String getExtraValue2() {
      return this.extraValue2;
   }

   public PacketType getPacketType() {
      return this.packetType;
   }

   public ServerType getServerType() {
      return this.serverType;
   }

   public void setUniqueId(UUID uniqueId) {
      this.uniqueId = uniqueId;
   }

   public void setNick(String nick) {
      this.nick = nick;
   }

   public void setField(String field) {
      this.field = field;
   }

   public void setFieldValue(String fieldValue) {
      this.fieldValue = fieldValue;
   }

   public void setExtraValue(String extraValue) {
      this.extraValue = extraValue;
   }

   public void setExtraValue2(String extraValue2) {
      this.extraValue2 = extraValue2;
   }

   public void setPacketType(PacketType packetType) {
      this.packetType = packetType;
   }

   public void setServerType(ServerType serverType) {
      this.serverType = serverType;
   }
}