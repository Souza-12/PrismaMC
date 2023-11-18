package com.br.teagadev.prismamc.commons.bungee.events;

import net.md_5.bungee.api.plugin.Event;

public class BungeeUpdateEvent extends Event {
   private final BungeeUpdateEvent.BungeeUpdateType type;
   private final long seconds;

   public BungeeUpdateEvent(BungeeUpdateEvent.BungeeUpdateType type, long seconds) {
      this.type = type;
      this.seconds = seconds;
   }

   public BungeeUpdateEvent.BungeeUpdateType getType() {
      return this.type;
   }

   public long getSeconds() {
      return this.seconds;
   }

   public static enum BungeeUpdateType {
      SEGUNDO,
      MINUTO,
      HORA;
   }
}