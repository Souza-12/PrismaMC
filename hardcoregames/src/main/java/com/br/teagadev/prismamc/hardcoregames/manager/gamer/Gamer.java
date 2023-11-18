package com.br.teagadev.prismamc.hardcoregames.manager.gamer;

import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesOptions;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Gamer {
   private UUID uniqueId;
   private String kit1;
   private String kit2;
   private boolean eliminado;
   private boolean playing;
   private boolean online;
   private boolean relogar;
   private int kills;
   private int taskID;
   private Player player;

   public Gamer(UUID uniqueId) {
      this.setUniqueId(uniqueId);
      this.kit1 = "Nenhum";
      this.kit2 = "Nenhum";
      this.setKills(0);
      this.setTaskID(-1);
      this.setEliminado(false);
      this.setPlaying(true);
      this.setOnline(true);
      this.setRelogar(false);
   }

   public boolean containsKit(String name) {
      return this.getKit1().equalsIgnoreCase(name) || HardcoreGamesOptions.DOUBLE_KIT && this.getKit2().equalsIgnoreCase(name);
   }

   public void addKill() {
      this.setKills(this.getKills() + 1);
   }

   public Player getPlayer() {
      if (this.player == null) {
         this.player = Bukkit.getPlayer(this.uniqueId);
      }

      return this.player;
   }

   public String getKits() {
      return !this.getKit2().equalsIgnoreCase("Nenhum") ? this.getKit1() + " e " + this.getKit2() : this.getKit1();
   }

   public void setKit1(String kit) {
      this.kit1 = kit;
      HardcoreGamesScoreboard.getScoreBoardCommon().updateKit1(this.getPlayer(), kit);
   }

   public void setKit2(String kit) {
      this.kit2 = kit;
      HardcoreGamesScoreboard.getScoreBoardCommon().updateKit2(this.getPlayer(), kit);
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public String getKit1() {
      return this.kit1;
   }

   public String getKit2() {
      return this.kit2;
   }

   public boolean isEliminado() {
      return this.eliminado;
   }

   public boolean isPlaying() {
      return this.playing;
   }

   public boolean isOnline() {
      return this.online;
   }

   public boolean isRelogar() {
      return this.relogar;
   }

   public int getKills() {
      return this.kills;
   }

   public int getTaskID() {
      return this.taskID;
   }

   public void setUniqueId(UUID uniqueId) {
      this.uniqueId = uniqueId;
   }

   public void setEliminado(boolean eliminado) {
      this.eliminado = eliminado;
   }

   public void setPlaying(boolean playing) {
      this.playing = playing;
   }

   public void setOnline(boolean online) {
      this.online = online;
   }

   public void setRelogar(boolean relogar) {
      this.relogar = relogar;
   }

   public void setKills(int kills) {
      this.kills = kills;
   }

   public void setTaskID(int taskID) {
      this.taskID = taskID;
   }

   public void setPlayer(Player player) {
      this.player = player;
   }
}