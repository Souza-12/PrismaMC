package com.br.teagadev.prismamc.commons.common.profile;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.common.data.Data;
import com.br.teagadev.prismamc.commons.common.data.DataHandler;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.profile.token.AcessToken;
import com.br.teagadev.prismamc.commons.common.profile.token.AcessTokenListener;
import com.br.teagadev.prismamc.commons.common.punishment.PunishmentHistoric;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.br.teagadev.servercommunication.client.Client;
import com.br.teagadev.servercommunication.common.packet.CommonPacket;
import java.util.UUID;

public class GamingProfile {
   private String nick;
   private UUID uniqueId;
   private String address;
   private DataHandler dataHandler;
   private AcessToken acessToken;
   private AcessTokenListener tokenListener;
   private PunishmentHistoric punishmentHistoric;

   public GamingProfile(String nick, String address, UUID uniqueId) {
      this.nick = nick;
      this.uniqueId = uniqueId;
      this.address = address;
      this.dataHandler = new DataHandler(nick, uniqueId);
   }

   public GamingProfile(String nick, UUID uniqueId) {
      this(nick, "Unknown", uniqueId);
   }

   public Groups getGroup() {
      return Groups.getGroup(this.getString(DataType.GROUP));
   }

   public boolean isStaffer() {
      return this.getGroup().getLevel() >= Groups.YOUTUBER_PLUS.getLevel();
   }

   public boolean isVIP() {
      return this.getGroup().getLevel() > Groups.MEMBRO.getLevel();
   }

   public void sendPacket(CommonPacket packet) {
      if (CommonsGeneral.getPluginInstance().isBukkit()) {
         Client.getInstance().getClientConnection().sendPacket(packet);
      }
   }

   public PunishmentHistoric getPunishmentHistoric() {
      if (this.punishmentHistoric == null) {
         this.punishmentHistoric = new PunishmentHistoric(this.getNick(), this.getAddress());
      }

      return this.punishmentHistoric;
   }

   public boolean isExpired(DataType dataType) {
      return this.isExpired(false, dataType);
   }

   public boolean isExpired(boolean update, DataType dataType) {
      if (this.getLong(dataType) != 0L && System.currentTimeMillis() > this.getLong(dataType)) {
         if (update) {
            this.getData(dataType).setValue(0L);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean containsFake() {
      return !this.getString(DataType.FAKE).equals("");
   }

   public Data getData(DataType dataType) {
      return this.getDataHandler().getData(dataType);
   }

   public int getInt(DataType dataType) {
      return this.getDataHandler().getInt(dataType);
   }

   public String getIntFormatted(DataType dataType) {
      return StringUtility.formatValue(this.getDataHandler().getInt(dataType));
   }

   public String getString(DataType dataType) {
      return this.getDataHandler().getString(dataType);
   }

   public Boolean getBoolean(DataType dataType) {
      return this.getDataHandler().getBoolean(dataType);
   }

   public Long getLong(DataType dataType) {
      return this.getDataHandler().getLong(dataType);
   }

   public void remove(DataType dataType) {
      this.remove(dataType, 1);
   }

   public void remove(DataType dataType, int value) {
      this.getDataHandler().getData(dataType).remove(value);
   }

   public void add(DataType dataType) {
      this.add(dataType, 1);
   }

   public void add(DataType dataType, int value) {
      this.getDataHandler().getData(dataType).add(value);
   }

   public void set(DataType dataType, Object value) {
      this.getDataHandler().getData(dataType).setValue(value);
   }

   public String getNick() {
      return this.nick;
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public String getAddress() {
      return this.address;
   }

   public DataHandler getDataHandler() {
      return this.dataHandler;
   }

   public AcessToken getAcessToken() {
      return this.acessToken;
   }

   public AcessTokenListener getTokenListener() {
      return this.tokenListener;
   }

   public void setNick(String nick) {
      this.nick = nick;
   }

   public void setUniqueId(UUID uniqueId) {
      this.uniqueId = uniqueId;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public void setDataHandler(DataHandler dataHandler) {
      this.dataHandler = dataHandler;
   }

   public void setAcessToken(AcessToken acessToken) {
      this.acessToken = acessToken;
   }

   public void setTokenListener(AcessTokenListener tokenListener) {
      this.tokenListener = tokenListener;
   }

   public void setPunishmentHistoric(PunishmentHistoric punishmentHistoric) {
      this.punishmentHistoric = punishmentHistoric;
   }
}