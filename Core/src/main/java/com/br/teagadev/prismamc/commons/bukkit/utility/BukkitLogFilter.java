package com.br.teagadev.prismamc.commons.bukkit.utility;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

public final class BukkitLogFilter extends AbstractFilter {
   public void registerFilter() {
      Logger logger = (Logger)LogManager.getRootLogger();
      logger.addFilter(this);
   }

   public Result filter(LogEvent event) {
      return event == null ? Result.NEUTRAL : this.isLoggable(event.getMessage().getFormattedMessage());
   }

   public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
      return this.isLoggable(msg.getFormattedMessage());
   }

   public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
      return this.isLoggable(msg);
   }

   public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
      return msg == null ? Result.NEUTRAL : this.isLoggable(msg.toString());
   }

   private Result isLoggable(String msg) {
      if (msg != null) {
         if (msg.contains("Commons")) {
            return Result.NEUTRAL;
         }

         if (msg.contains("PACKET")) {
            return Result.NEUTRAL;
         }

         if (msg.contains("twice")) {
            return Result.DENY;
         }

         if (msg.contains("handleDisconnection")) {
            return Result.DENY;
         }

         if (msg.contains("com.mojang.authlib.GameProfile@")) {
            return Result.DENY;
         }

         if (msg.contains("lost connection: Disconnected")) {
            return Result.DENY;
         }

         if (msg.contains("left the game.")) {
            return Result.DENY;
         }

         if (msg.contains("logged in with entity id")) {
            return Result.DENY;
         }

         if (msg.contains("lost connection: Timed out")) {
            return Result.DENY;
         }

         if (msg.contains("UUID of player")) {
            return Result.DENY;
         }

         if (msg.contains("Internal Exception")) {
            return Result.DENY;
         }

         if (msg.contains("lost connection")) {
            return Result.DENY;
         }

         if (msg.contains("has disconnected")) {
            return Result.DENY;
         }

         if (msg.contains("reason")) {
            return Result.DENY;
         }

         if (msg.contains("disconnected with")) {
            return Result.DENY;
         }
      }

      return Result.NEUTRAL;
   }
}