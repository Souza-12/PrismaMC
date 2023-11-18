package com.br.teagadev.prismamc.commons.custompackets.registry;

import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.common.packet.CommonPacket;
import com.google.gson.JsonObject;
import java.net.Socket;

public class CPacketAction extends CommonPacket {
   private String type;
   private String field;
   private String fieldValue;
   private String extraValue;
   private String extraValue2;

   public CPacketAction() {
   }

   public CPacketAction(String serverName, int serverID) {
      this.setServerName(serverName);
      this.setServerID(serverID);
      this.setJson(new JsonObject());
      this.write();
   }

   public void read(JsonObject json) {
      this.setJson(json);
      this.setServerName(json.get("serverName").getAsString());
      this.setServerID(json.get("serverID").getAsInt());
      if (json.has("type")) {
         this.type = json.get("type").getAsString();
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

   }

   public CPacketAction writeType(String value) {
      this.getJson().addProperty("type", value);
      return this;
   }

   public CPacketAction writeField(String value) {
      this.getJson().addProperty("field", value);
      return this;
   }

   public CPacketAction writeFieldValue(String value) {
      this.getJson().addProperty("fieldValue", value);
      return this;
   }

   public CPacketAction writeExtraValue(String value) {
      this.getJson().addProperty("extraValue", value);
      return this;
   }

   public CPacketAction writeExtraValue2(String value) {
      this.getJson().addProperty("extraValue2", value);
      return this;
   }

   public void write() {
      this.getJson().addProperty("packetName", this.getPacketName());
      this.getJson().addProperty("serverID", this.getServerID());
      this.getJson().addProperty("serverName", this.getServerName());
      this.getJson().addProperty("timeStamp", System.currentTimeMillis());
   }

   public void handle(Socket socket) {
      ServerCommunication.getPacketHandler().handleCPacketAction(this, socket);
   }

   public String getPacketName() {
      return this.getClass().getSimpleName();
   }

   public String getJSONString() {
      return this.getJson().toString();
   }

   public String getValues() {
      StringBuilder stringBuilder = new StringBuilder();
      if (this.getType() != null) {
         stringBuilder.append("type: ").append(this.getType());
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

   public String getType() {
      return this.type;
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

   public void setType(String type) {
      this.type = type;
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
}