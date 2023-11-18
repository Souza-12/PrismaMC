package com.br.teagadev.prismamc.hardcoregames.game;

import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import com.br.teagadev.prismamc.hardcoregames.base.GameType;

public class GameManager {
   private GameStages stage;
   private GameType gameType;

   public GameManager() {
      this.stage = GameStages.LOADING;
   }

   public boolean isLoading() {
      return this.stage.equals(GameStages.LOADING);
   }

   public boolean isPreGame() {
      return this.stage.equals(GameStages.WAITING);
   }

   public boolean isInvencibilidade() {
      return this.stage.equals(GameStages.INVINCIBILITY);
   }

   public boolean isGaming() {
      return this.stage.equals(GameStages.PLAYING);
   }

   public boolean isEnd() {
      return this.stage.equals(GameStages.END);
   }

   public void setGameType(GameType gameType) {
      this.gameType = gameType;
   }

   public boolean canLogin() {
      return !this.isLoading() && !this.isEnd();
   }

   public GameStages getStage() {
      return this.stage;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public void setStage(GameStages stage) {
      this.stage = stage;
   }
}