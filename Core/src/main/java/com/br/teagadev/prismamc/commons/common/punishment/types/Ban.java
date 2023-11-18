package com.br.teagadev.prismamc.commons.common.punishment.types;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Ban {
   private final String nick;
   private final String address;
   private final String bannedBy;
   private final String motive;
   private String bannedDate;
   private final long punishmentTime;
   private String unbannedBy;
   private String unbannedDate;
   private long unbannedTime;
   private boolean punished;

   public Ban(String nick, String address, String bannedBy, String motive, long punishmentTime) {
      this.nick = nick;
      this.address = address;
      this.bannedBy = bannedBy;
      this.motive = motive;
      this.punishmentTime = punishmentTime;
      this.unbannedBy = "";
      this.unbannedDate = "";
      this.unbannedTime = 0L;
   }

   public Ban(String nick, String address, String bannedBy, String motive, String bannedDate, String unbannedBy, String unbannedDate, long punishmentTime, long unbannedTime) {
      this.nick = nick;
      this.address = address;
      this.bannedBy = bannedBy;
      this.motive = motive;
      this.bannedDate = bannedDate;
      this.punishmentTime = punishmentTime;
      this.unbannedBy = unbannedBy;
      this.unbannedDate = unbannedDate;
      this.unbannedTime = unbannedTime;
   }

   public void ban() {
      if (!this.punished) {
         this.punished = true;
         this.bannedDate = DateUtils.getActualDate(false);
         CommonsGeneral.runAsync(() -> {
            try {
               Connection connection = CommonsGeneral.getMySQL().getConnection();
               Throwable var2 = null;

               try {
                  PreparedStatement insert = connection.prepareStatement("INSERT INTO bans(nick, address, data) VALUES (?, ?, ?);");
                  insert.setString(1, this.nick);
                  insert.setString(2, this.address);
                  insert.setString(3, this.buildData());
                  insert.execute();
                  insert.close();
                  insert = null;
               } catch (Throwable var12) {
                  var2 = var12;
                  throw var12;
               } finally {
                  if (connection != null) {
                     if (var2 != null) {
                        try {
                           connection.close();
                        } catch (Throwable var11) {
                           var2.addSuppressed(var11);
                        }
                     } else {
                        connection.close();
                     }
                  }

               }
            } catch (SQLException var14) {
               CommonsGeneral.error("ban() : Ban.java -> " + var14.getLocalizedMessage());
            }

         });
      }

   }

   private String buildData() {
      JsonObject json = new JsonObject();
      json.addProperty("bannedBy", this.bannedBy);
      json.addProperty("motive", this.motive);
      json.addProperty("bannedDate", this.bannedDate);
      json.addProperty("punishmentTime", this.punishmentTime);
      json.addProperty("unbannedBy", this.unbannedBy);
      json.addProperty("unbannedDate", this.unbannedDate);
      json.addProperty("unbannedTime", this.unbannedTime);
      return json.toString();
   }

   public void unban(String unbannedBy) {
      if (this.punished) {
         this.punished = false;
         this.unbannedBy = unbannedBy;
         this.unbannedDate = DateUtils.getActualDate(false);
         this.unbannedTime = System.currentTimeMillis();
         CommonsGeneral.runAsync(() -> {
            try {
               Connection connection = CommonsGeneral.getMySQL().getConnection();
               Throwable var2 = null;

               try {
                  PreparedStatement insert = connection.prepareStatement("UPDATE bans SET data=? WHERE nick='" + this.getNick() + "'");
                  insert.setString(1, this.buildData());
                  insert.execute();
                  insert.close();
                  insert = null;
               } catch (Throwable var12) {
                  var2 = var12;
                  throw var12;
               } finally {
                  if (connection != null) {
                     if (var2 != null) {
                        try {
                           connection.close();
                        } catch (Throwable var11) {
                           var2.addSuppressed(var11);
                        }
                     } else {
                        connection.close();
                     }
                  }

               }
            } catch (SQLException var14) {
               CommonsGeneral.error("unban() : Ban.java -> " + var14.getLocalizedMessage());
            }

         });
      }

   }

   public boolean isExpired() {
      return !this.isPermanent() && this.getPunishmentTime() < System.currentTimeMillis();
   }

   public boolean isPermanent() {
      return this.getPunishmentTime() == 0L;
   }

   public String getNick() {
      return this.nick;
   }

   public String getAddress() {
      return this.address;
   }

   public String getBannedBy() {
      return this.bannedBy;
   }

   public String getMotive() {
      return this.motive;
   }

   public String getBannedDate() {
      return this.bannedDate;
   }

   public long getPunishmentTime() {
      return this.punishmentTime;
   }

   public String getUnbannedBy() {
      return this.unbannedBy;
   }

   public String getUnbannedDate() {
      return this.unbannedDate;
   }

   public long getUnbannedTime() {
      return this.unbannedTime;
   }

   public boolean isPunished() {
      return this.punished;
   }
}