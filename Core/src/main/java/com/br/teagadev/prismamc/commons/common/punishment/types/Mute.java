package com.br.teagadev.prismamc.commons.common.punishment.types;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Mute {
   private final String nick;
   private final String mutedBy;
   private final String motive;
   private String mutedDate;
   private final long punishmentTime;
   private String unmutedBy;
   private String unmutedDate;
   private long unmutedTime;
   private boolean punished;

   public Mute(String nick, String mutedBy, String motive, long punishmentTime) {
      this.nick = nick;
      this.mutedBy = mutedBy;
      this.motive = motive;
      this.punishmentTime = punishmentTime;
      this.unmutedBy = "";
      this.unmutedDate = "";
      this.unmutedTime = 0L;
   }

   public Mute(String nick, String mutedBy, String motive, String mutedDate, String unmutedBy, String unmutedDate, long punishmentTime, long unmutedTime) {
      this.nick = nick;
      this.mutedBy = mutedBy;
      this.motive = motive;
      this.mutedDate = mutedDate;
      this.punishmentTime = punishmentTime;
      this.unmutedBy = unmutedBy;
      this.unmutedDate = unmutedDate;
      this.unmutedTime = unmutedTime;
   }

   public void mute() {
      if (!this.punished) {
         this.punished = true;
         this.mutedDate = DateUtils.getActualDate(false);
         CommonsGeneral.runAsync(() -> {
            try {
               Connection connection = CommonsGeneral.getMySQL().getConnection();
               Throwable var2 = null;

               try {
                  PreparedStatement insert = connection.prepareStatement("INSERT INTO mutes(nick, data) VALUES (?, ?);");
                  insert.setString(1, this.nick);
                  insert.setString(2, this.buildData());
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
               CommonsGeneral.error("mute() : Mute.java -> " + var14.getLocalizedMessage());
            }

         });
      }

   }

   private String buildData() {
      JsonObject json = new JsonObject();
      json.addProperty("mutedBy", this.mutedBy);
      json.addProperty("motive", this.motive);
      json.addProperty("mutedDate", this.mutedDate);
      json.addProperty("punishmentTime", this.punishmentTime);
      json.addProperty("unmutedBy", this.unmutedBy);
      json.addProperty("unmutedDate", this.unmutedDate);
      json.addProperty("unmutedTime", this.unmutedTime);
      return json.toString();
   }

   public void unmute(String unmutedBy) {
      if (this.punished) {
         this.punished = false;
         this.unmutedBy = unmutedBy;
         this.unmutedDate = DateUtils.getActualDate(false);
         this.unmutedTime = System.currentTimeMillis();
         CommonsGeneral.runAsync(() -> {
            try {
               Connection connection = CommonsGeneral.getMySQL().getConnection();
               Throwable var2 = null;

               try {
                  PreparedStatement insert = connection.prepareStatement("UPDATE mutes SET data=? WHERE nick='" + this.getNick() + "'");
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
               CommonsGeneral.error("unmute() : Mute.java -> " + var14.getLocalizedMessage());
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

   public String getMutedBy() {
      return this.mutedBy;
   }

   public String getMotive() {
      return this.motive;
   }

   public String getMutedDate() {
      return this.mutedDate;
   }

   public long getPunishmentTime() {
      return this.punishmentTime;
   }

   public String getUnmutedBy() {
      return this.unmutedBy;
   }

   public String getUnmutedDate() {
      return this.unmutedDate;
   }

   public long getUnmutedTime() {
      return this.unmutedTime;
   }

   public boolean isPunished() {
      return this.punished;
   }
}