package com.br.teagadev.prismamc.hardcoregames.events.player;

import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerCancellableEvent;
import org.bukkit.entity.Player;

public class PlayerDamagePlayerEvent extends PlayerCancellableEvent {
   private Player damaged;
   private double damage;
   private boolean cancelled;

   public PlayerDamagePlayerEvent(Player damager, Player damaged, double damage) {
      super(damager);
      this.damaged = damaged;
      this.damage = damage;
   }

   public Player getDamaged() {
      return this.damaged;
   }

   public double getDamage() {
      return this.damage;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setDamage(double damage) {
      this.damage = damage;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}