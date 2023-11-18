package com.br.teagadev.prismamc.commons.bungee.manager.premium;

import java.util.UUID;

public class PremiumMap {
   private boolean premium;
   private String nick;
   private UUID uniqueId;

   public PremiumMap(UUID uniqueId, String nick, boolean premium) {
      this.nick = nick;
      this.premium = premium;
      this.uniqueId = uniqueId;
   }

   public boolean isPremium() {
      return this.premium;
   }

   public String getNick() {
      return this.nick;
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public void setPremium(boolean premium) {
      this.premium = premium;
   }

   public void setNick(String nick) {
      this.nick = nick;
   }

   public void setUniqueId(UUID uniqueId) {
      this.uniqueId = uniqueId;
   }
}