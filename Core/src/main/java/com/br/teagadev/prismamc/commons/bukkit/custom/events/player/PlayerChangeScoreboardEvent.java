package com.br.teagadev.prismamc.commons.bukkit.custom.events.player;

import org.bukkit.entity.Player;

public class PlayerChangeScoreboardEvent extends PlayerCancellableEvent {
   private final PlayerChangeScoreboardEvent.ScoreboardChangeType changeType;

   public PlayerChangeScoreboardEvent(Player player, PlayerChangeScoreboardEvent.ScoreboardChangeType changeType) {
      super(player);
      this.changeType = changeType;
   }

   public PlayerChangeScoreboardEvent.ScoreboardChangeType getChangeType() {
      return this.changeType;
   }

   public static enum ScoreboardChangeType {
      ATIVOU,
      DESATIVOU;
   }
}