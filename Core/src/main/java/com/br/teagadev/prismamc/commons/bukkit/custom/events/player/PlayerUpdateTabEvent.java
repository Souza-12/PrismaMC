package com.br.teagadev.prismamc.commons.bukkit.custom.events.player;

import org.bukkit.entity.Player;

public class PlayerUpdateTabEvent extends PlayerEvent {
   private String header;
   private String footer;

   public PlayerUpdateTabEvent(Player player) {
      super(player);
   }

   public String getHeader() {
      return this.header;
   }

   public String getFooter() {
      return this.footer;
   }

   public void setHeader(String header) {
      this.header = header;
   }

   public void setFooter(String footer) {
      this.footer = footer;
   }
}