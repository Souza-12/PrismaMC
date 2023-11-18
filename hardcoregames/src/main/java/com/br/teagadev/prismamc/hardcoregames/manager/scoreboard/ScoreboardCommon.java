package com.br.teagadev.prismamc.hardcoregames.manager.scoreboard;

import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.br.teagadev.prismamc.commons.bukkit.api.vanish.VanishAPI;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;

public abstract class ScoreboardCommon {
	   public void create(Gamer gamer) {
	      int playerMode = this.getPlayerMode(gamer.getPlayer(), gamer.isPlaying());
	      if (playerMode == 1) {
	         this.createGamerScoreboard(gamer.getPlayer(), gamer, SidebarManager.getSidebar(gamer.getUniqueId()));
	      } else {
	         this.createSpecScoreboard(gamer.getPlayer(), gamer, SidebarManager.getSidebar(gamer.getUniqueId()), playerMode);
	      }

	   }

	   public void updateGaming(Player player) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      sidebar.updateLine("gaming", "Players: ", "§7" + this.getGaming() + "§7/" + Bukkit.getMaxPlayers());
	   }

	   public void updateTime(Player player) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      sidebar.updateLine("time", this.getMessageTime(), "§7" + HardcoreGamesMain.getTimerManager().getLastFormatted());
	   }

	   public void updateKit1(Player player, String kitName) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      Gamer gamerkit = GamerManager.getGamer(player.getUniqueId());
	      sidebar.updateLine("kit1", "§fKit 1: ", "§a" + kitName);
	   }

	   public void updateKit2(Player player, String kitName) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      Gamer gamerkit = GamerManager.getGamer(player.getUniqueId());
	      sidebar.updateLine("kit2", "§fKit 2: ", "§a" + kitName);
	   }

	   public void updateKills(Player player, int kills) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      sidebar.updateLine("kills", "§fKills: ", "§a" + kills);
	   }

	   public void updatePlayerMode(Gamer gamer) {
	      int playerMode = this.getPlayerMode(gamer.getPlayer(), gamer.isPlaying());
	      Sidebar sidebar = SidebarManager.getSidebar(gamer.getUniqueId());
	      sidebar.updateLine("playerMode", "", this.getPlayerMode(playerMode));
	   }

	   public abstract void createGamerScoreboard(Player var1, Gamer var2, Sidebar var3);

	   public abstract void createSpecScoreboard(Player var1, Gamer var2, Sidebar var3, int var4);

	   public void createSpecScoreboard(Player player, Gamer gamer, Sidebar sidebar) {
	      this.createSpecScoreboard(player, gamer, sidebar, this.getPlayerMode(player, gamer.isPlaying()));
	   }

	   private int getPlayerMode(Player player, boolean playing) {
	      return playing ? 1 : (VanishAPI.inAdmin(player) ? 2 : 0);
	   }

	   public String getMessageTime() {
	      return HardcoreGamesMain.getGameManager().isPreGame() ? "Iniciando em: " : (HardcoreGamesMain.getGameManager().isInvencibilidade() ? "Invencível por: " : "Tempo: ");
	   }

	   public String getGaming() {
	      return "" + HardcoreGamesMain.getTimerManager().getLastAlive();
	   }

	   public String getPlayerMode(int playerMode) {
	      return playerMode == 2 ? "§cMODO VANISH" : "§8SPECTATOR";
	   }
	}