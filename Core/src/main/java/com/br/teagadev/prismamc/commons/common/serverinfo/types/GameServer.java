package com.br.teagadev.prismamc.commons.common.serverinfo.types;

import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameType;
import com.google.gson.JsonObject;

public class GameServer extends NetworkServer {
   private int playersGaming = 0;
   private int tempo = 0;
   private GameStages stage;
   private String mapName;
   private GameType gameType;

   public GameServer(ServerType serverType, int serverId) {
      super(serverType, serverId);
      this.stage = GameStages.OFFLINE;
      this.mapName = "Unknown";
      this.gameType = GameType.UNKNOWN;
   }

   public void updateData(JsonObject json) {
      this.update(json);
      this.setGameType(json.has("gameType") ? (json.get("gameType").getAsString().equalsIgnoreCase("SOLO") ? GameType.SOLO : GameType.DUO) : GameType.UNKNOWN);
      this.setMapName(json.has("mapName") ? json.get("mapName").getAsString() : "Unknown");
      this.setPlayersGaming(json.has("playersGaming") ? json.get("playersGaming").getAsInt() : 0);
      this.setTempo(json.has("tempo") ? json.get("tempo").getAsInt() : 0);
      this.setStage(json.has("stage") ? GameStages.getStage(json.get("stage").getAsString()) : GameStages.OFFLINE);
   }

   public JsonObject toJsonGame() {
      JsonObject json = this.toJson();
      json.addProperty("playersGaming", this.getPlayersGaming());
      json.addProperty("tempo", this.getTempo());
      json.addProperty("stage", this.getStage().getNome());
      json.addProperty("mapName", this.getMapName());
      json.addProperty("gameType", this.getGameType().getName());
      return json;
   }

   public int getPlayersGaming() {
      return this.playersGaming;
   }

   public int getTempo() {
      return this.tempo;
   }

   public GameStages getStage() {
      return this.stage;
   }

   public String getMapName() {
      return this.mapName;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public void setPlayersGaming(int playersGaming) {
      this.playersGaming = playersGaming;
   }

   public void setTempo(int tempo) {
      this.tempo = tempo;
   }

   public void setStage(GameStages stage) {
      this.stage = stage;
   }

   public void setMapName(String mapName) {
      this.mapName = mapName;
   }

   public void setGameType(GameType gameType) {
      this.gameType = gameType;
   }
}