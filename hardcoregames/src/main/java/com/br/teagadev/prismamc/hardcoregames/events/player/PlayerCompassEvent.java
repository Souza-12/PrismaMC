package com.br.teagadev.prismamc.hardcoregames.events.player;

import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerCompassEvent extends PlayerEvent {
   private PlayerCompassEvent.CompassAction action;
   private Player target;

   public PlayerCompassEvent(Player player, PlayerCompassEvent.CompassAction action) {
      super(player);
      this.action = action;
   }

   public PlayerCompassEvent.CompassAction getAction() {
      return this.action;
   }

   public Player getTarget() {
      return this.target;
   }

   public void setAction(PlayerCompassEvent.CompassAction action) {
      this.action = action;
   }

   public void setTarget(Player target) {
      this.target = target;
   }

   public static enum CompassAction {
      LEFT,
      RIGHT;
   }
}