package com.br.teagadev.prismamc.commons.common.punishment;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.common.punishment.types.Ban;
import com.br.teagadev.prismamc.commons.common.punishment.types.Mute;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PunishmentHistoric {
   private final String nick;
   private final String address;
   private final List<Ban> banHistory;
   private final List<Mute> muteHistory;

   public PunishmentHistoric(String nick, String address) {
      this.banHistory = new ArrayList();
      this.muteHistory = new ArrayList();
      this.nick = nick;
      this.address = address;
   }

   public PunishmentHistoric(String nick) {
      this(nick, "");
   }

   public void loadMutes() throws SQLException {
      this.muteHistory.clear();
      Connection connection = CommonsGeneral.getMySQL().getConnection();
      Throwable var2 = null;

      try {
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM mutes WHERE nick='" + this.getNick() + "'");

         ResultSet result;
         JsonObject json;
         for(result = preparedStatement.executeQuery(); result.next(); json = null) {
            json = CommonsConst.PARSER.parse(result.getString("data")).getAsJsonObject();
            this.muteHistory.add(new Mute(result.getString("nick"), json.get("mutedBy").getAsString(), json.get("motive").getAsString(), json.get("mutedDate").getAsString(), json.get("unmutedBy").getAsString(), json.get("unmutedDate").getAsString(), json.get("punishmentTime").getAsLong(), json.get("unmutedTime").getAsLong()));
         }

         result.close();
         preparedStatement.close();
         result = null;
         preparedStatement = null;
      } catch (Throwable var13) {
         var2 = var13;
         throw var13;
      } finally {
         if (connection != null) {
            if (var2 != null) {
               try {
                  connection.close();
               } catch (Throwable var12) {
                  var2.addSuppressed(var12);
               }
            } else {
               connection.close();
            }
         }

      }
   }

   public void loadBans() throws SQLException {
      this.loadBans(false);
   }

   public void loadBans(boolean fromAddress) throws SQLException {
      this.banHistory.clear();
      Connection connection = CommonsGeneral.getMySQL().getConnection();
      Throwable var3 = null;

      try {
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bans WHERE " + (fromAddress ? "address='" + this.getAddress() : "nick='" + this.getNick()) + "'");
         ResultSet result = preparedStatement.executeQuery();

         int FINDEDS;
         for(FINDEDS = 0; result.next(); ++FINDEDS) {
            JsonObject json = CommonsConst.PARSER.parse(result.getString("data")).getAsJsonObject();
            this.banHistory.add(new Ban(result.getString("nick"), result.getString("address"), json.get("bannedBy").getAsString(), json.get("motive").getAsString(), json.get("bannedDate").getAsString(), json.get("unbannedBy").getAsString(), json.get("unbannedDate").getAsString(), json.get("punishmentTime").getAsLong(), json.get("unbannedTime").getAsLong()));
            json = null;
         }

         preparedStatement.close();
         result.close();
         preparedStatement = null;
         result = null;
         if (FINDEDS == 0 && !fromAddress) {
            this.loadBans(true);
         }
      } catch (Throwable var15) {
         var3 = var15;
         throw var15;
      } finally {
         if (connection != null) {
            if (var3 != null) {
               try {
                  connection.close();
               } catch (Throwable var14) {
                  var3.addSuppressed(var14);
               }
            } else {
               connection.close();
            }
         }

      }

   }

   public Ban getActualBan() {
      Iterator var1 = this.banHistory.iterator();

      Ban ban;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         ban = (Ban)var1.next();
      } while(!ban.isPunished() || ban.isExpired());

      return ban;
   }

   public Mute getActualMute() {
      Iterator var1 = this.muteHistory.iterator();

      Mute mute;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         mute = (Mute)var1.next();
      } while(!mute.isPunished() || mute.isExpired());

      return mute;
   }

   public List<Ban> getBanHistory() {
      return this.banHistory;
   }

   public List<Mute> getMuteHistory() {
      return this.muteHistory;
   }

   public String getNick() {
      return this.nick;
   }

   public String getAddress() {
      return this.address;
   }
}