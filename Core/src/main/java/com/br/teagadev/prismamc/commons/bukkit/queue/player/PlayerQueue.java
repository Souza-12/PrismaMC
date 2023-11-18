package com.br.teagadev.prismamc.commons.bukkit.queue.player;

import org.bukkit.entity.Player;

public class PlayerQueue {
   public Player player;
   public String server;

   public PlayerQueue(Player player, String server) {
      this.player = player;
      this.server = server;
   }

   public PlayerQueue(Player player) {
      this(player, "");
   }

   public void destroy() {
      this.player = null;
      this.server = null;
   }

   public Player getPlayer() {
      return this.player;
   }

   public String getServer() {
      return this.server;
   }
}