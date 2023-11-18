package com.br.teagadev.prismamc.lobby;

import com.br.teagadev.prismamc.lobby.common.scoreboard.ScoreboardInstance;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandFramework;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.server.ServerLoadedEvent;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.teagadev.prismamc.commons.bukkit.utility.loader.BukkitListeners;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.lobby.common.hologram.HologramCommon;
import com.br.teagadev.prismamc.lobby.common.hologram.types.LobbyHGHologram;
import com.br.teagadev.prismamc.lobby.common.hologram.types.LobbyPvPHologram;
import com.br.teagadev.prismamc.lobby.common.npcs.NPCCommon;
import com.br.teagadev.prismamc.lobby.common.npcs.types.LobbyHGNPC;
import com.br.teagadev.prismamc.lobby.common.npcs.types.LobbyNPC;
import com.br.teagadev.prismamc.lobby.common.npcs.types.LobbyPvPNPC;
import com.br.teagadev.prismamc.lobby.common.scoreboard.ScoreboardCommon;
import com.br.teagadev.prismamc.lobby.common.scoreboard.types.LobbyHGScoreboard;
import com.br.teagadev.prismamc.lobby.common.scoreboard.types.LobbyPvPScoreboard;
import com.br.teagadev.prismamc.lobby.common.scoreboard.types.LobbyScoreboard;
import com.comphenix.protocol.ProtocolManager;

import lombok.Getter;
import lombok.Setter;

public class LobbyMain extends JavaPlugin {
	   private static LobbyMain instance;
	   private ProtocolManager procotolManager;
	   private static Location spawn;
	   private static ScoreboardCommon scoreBoardCommon;
	   private static LobbyScoreboard lobbyBoardCommon;
	   private static NPCCommon npcCommon;
	   private static ScoreboardInstance scoreboardInstance;
	   private static HologramCommon hologramCommon;

	   public void onLoad() {
	      setInstance(this);
	      this.saveDefaultConfig();
	   }

	   public void onEnable() {
	      if (CommonsGeneral.correctlyStarted()) {
	         BukkitServerAPI.registerServer();
	         Bukkit.setDefaultGameMode(GameMode.ADVENTURE);
	         setScoreboardInstance(new ScoreboardInstance());
	         CommonsGeneral.getServersManager().init();
	         BukkitListeners.loadListeners(getInstance(), "com.br.teagadev.prismamc.lobby.listeners");
	         BukkitCommandFramework.INSTANCE.loadCommands(getInstance(), "com.br.teagadev.prismamc.lobby.commands");
	         BukkitMain.getManager().enablePacketInjector(this);
	         BukkitMain.getManager().enableHologram(this);
	         BukkitMain.getManager().enableNPC(this);
	         setSpawn(PluginConfiguration.createLocation(getInstance(), "spawn"));
	         setScoreBoardCommon((ScoreboardCommon)(BukkitMain.getServerType() == ServerType.LOBBY ? new LobbyScoreboard() : (BukkitMain.getServerType() == ServerType.LOBBY_PVP ? new LobbyPvPScoreboard() : (BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES ? new LobbyHGScoreboard() : null))));
	         if (getScoreBoardCommon() == null) {
	            console("Nao foi encontrada o padrao da ScoreboardCommon.");
	         }

	         setNpcCommon((NPCCommon)(BukkitMain.getServerType() == ServerType.LOBBY ? new LobbyNPC() : (BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES ? new LobbyHGNPC() : (BukkitMain.getServerType() == ServerType.LOBBY_PVP ? new LobbyPvPNPC() : null))));
	         if (getNpcCommon() == null) {
	            console("Nao foi encontrado o padrao do NPCCommon.");
	         } else {
	            getNpcCommon().create();
	         }

	         setHologramCommon((HologramCommon)(BukkitMain.getServerType() == ServerType.LOBBY ? null : (BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES ? new LobbyHGHologram() : (BukkitMain.getServerType() == ServerType.LOBBY_PVP ? new LobbyPvPHologram() : null))));
	         if (getHologramCommon() == null) {
	            console("Nao foi encontrado o padrao do HologramCommon.");
	         } else {
	            getHologramCommon().create();
	         }

	         runLater(() -> {
	            this.getServer().getPluginManager().callEvent(new ServerLoadedEvent());
	         }, (long)BukkitMain.getServerType().getSecondsToStabilize());
	      } else {
	         Bukkit.shutdown();
	      }

	   }

	   public void onDisable() {
	   }

	   public static void console(String msg) {
	      Bukkit.getConsoleSender().sendMessage("[Lobby] " + msg);
	   }

	   public static void runAsync(Runnable runnable) {
	      Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);
	   }

	   public static void runLater(Runnable runnable) {
	      Bukkit.getScheduler().runTaskLater(getInstance(), runnable, 5L);
	   }

	   public static void runLater(Runnable runnable, long ticks) {
	      Bukkit.getScheduler().runTaskLater(getInstance(), runnable, ticks);
	   }

	   public static LobbyMain getInstance() {
	      return instance;
	   }

	   public static void setInstance(LobbyMain instance) {
	      LobbyMain.instance = instance;
	   }

	   public static Location getSpawn() {
	      return spawn;
	   }

	   public static void setSpawn(Location spawn) {
	      LobbyMain.spawn = spawn;
	   }

	   public static ScoreboardCommon getScoreBoardCommon() {
	      return scoreBoardCommon;
	   }

	   public static void setScoreBoardCommon(ScoreboardCommon scoreBoardCommon) {
	      LobbyMain.scoreBoardCommon = scoreBoardCommon;
	   }

	   public static LobbyScoreboard getLobbyBoardCommon() {
	      return lobbyBoardCommon;
	   }

	   public static void setLobbyBoardCommon(LobbyScoreboard lobbyBoardCommon) {
	      LobbyMain.lobbyBoardCommon = lobbyBoardCommon;
	   }

	   public static NPCCommon getNpcCommon() {
	      return npcCommon;
	   }

	   public static void setNpcCommon(NPCCommon npcCommon) {
	      LobbyMain.npcCommon = npcCommon;
	   }

	   public static ScoreboardInstance getScoreboardInstance() {
	      return scoreboardInstance;
	   }

	   public static void setScoreboardInstance(ScoreboardInstance scoreboardInstance) {
	      LobbyMain.scoreboardInstance = scoreboardInstance;
	   }

	   public static HologramCommon getHologramCommon() {
	      return hologramCommon;
	   }

	   public static void setHologramCommon(HologramCommon hologramCommon) {
	      LobbyMain.hologramCommon = hologramCommon;
	   }
	   public ProtocolManager getProcotolManager() {
		      return this.procotolManager;
		   }
	}