package com.br.teagadev.prismamc.hardcoregames.events.game;

import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStageChangeEvent extends Event {
   public static final HandlerList handlerList = new HandlerList();
   private GameStages lastStage;
   private GameStages newStage;

   public GameStageChangeEvent(GameStages lastStage, GameStages newStage) {
      this.lastStage = lastStage;
      this.newStage = newStage;
   }

   public GameStages getNewStage() {
      return this.newStage;
   }

   public GameStages getLastStage() {
      return this.lastStage;
   }

   public HandlerList getHandlers() {
      return handlerList;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }
}