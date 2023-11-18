package com.br.teagadev.prismamc.commons.bungee.scheduler;

import com.br.teagadev.prismamc.commons.bungee.events.BungeeUpdateEvent;
import com.br.teagadev.prismamc.commons.bungee.events.BungeeUpdateEvent.BungeeUpdateType;
import net.md_5.bungee.BungeeCord;

public class BungeeUpdateScheduler implements Runnable {
   private long seconds;

   public void run() {
      BungeeCord.getInstance().getPluginManager().callEvent(new BungeeUpdateEvent(BungeeUpdateType.SEGUNDO, this.seconds));
      if (this.seconds % 60L == 0L) {
         BungeeCord.getInstance().getPluginManager().callEvent(new BungeeUpdateEvent(BungeeUpdateType.MINUTO, this.seconds));
      }

      if (this.seconds % 3600L == 0L) {
         BungeeCord.getInstance().getPluginManager().callEvent(new BungeeUpdateEvent(BungeeUpdateType.HORA, this.seconds));
      }

      ++this.seconds;
   }
}