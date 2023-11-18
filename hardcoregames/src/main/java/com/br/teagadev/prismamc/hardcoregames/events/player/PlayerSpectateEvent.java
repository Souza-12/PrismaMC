package com.br.teagadev.prismamc.hardcoregames.events.player;

import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerSpectateEvent extends PlayerEvent {
   private PlayerSpectateEvent.SpectateAction action;
   private Player target;

   public PlayerSpectateEvent(Player player, PlayerSpectateEvent.SpectateAction action) {
      super(player);
      this.action = action;
   }

   public PlayerSpectateEvent.SpectateAction getAction() {
      return this.action;
   }

   public Player getTarget() {
      return this.target;
   }

   public void setAction(PlayerSpectateEvent.SpectateAction action) {
      this.action = action;
   }

   public void setTarget(Player target) {
      this.target = target;
   }

   public static enum SpectateAction {
      SAIU,
      ENTROU;
   }
}