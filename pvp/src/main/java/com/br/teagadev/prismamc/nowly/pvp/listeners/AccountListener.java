package com.br.teagadev.prismamc.nowly.pvp.listeners;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.nowly.pvp.commands.ServerCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMessages;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.server.ServerStatusUpdateEvent;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.Gamer;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.GamerManager;

import java.sql.SQLException;

public class AccountListener implements Listener {
	   private final int MEMBERS_SLOTS = 80;

	   @EventHandler
	   public void onPre(AsyncPlayerPreLoginEvent e) throws SQLException {
	      BukkitMain.getBukkitPlayer(e.getUniqueId()).getDataHandler().load(new DataCategory[]{DataCategory.KITPVP});
	   }

	   @EventHandler
	   public void onLogin(PlayerLoginEvent event) {
	      if (event.getResult() == Result.ALLOWED) {
	         if (Bukkit.getOnlinePlayers().size() >= 80) {
	            if (!event.getPlayer().hasPermission("commons.entrar")) {
	               event.disallow(Result.KICK_OTHER, "§cO servidor está lotado!");
	            } else {
	               event.allow();
	               Gamer randomGamer = (Gamer)GamerManager.getGamers().values().stream().filter(Gamer::isProtection).filter((check) -> {
	                  return !check.getPlayer().hasPermission("commons.entrar");
	               }).findAny().orElse((Gamer)null);
	               if (randomGamer != null) {
	                  randomGamer.getPlayer().sendMessage("§cO slot que você possuia foi liberado para um jogador VIP!");
	                  BukkitServerAPI.redirectPlayer(randomGamer.getPlayer(), "LobbyPvP", true);
	               }
	            }
	         }

	         if (event.getResult() == Result.ALLOWED) {
	            GamerManager.addGamer(event.getPlayer().getUniqueId());
	         }

	      }
	   }

	   @EventHandler
	   public void onQuit(PlayerQuitEvent event) {
	      GamerManager.removeGamer(event.getPlayer().getUniqueId());
	      ServerCommand.autorizados.remove(event.getPlayer().getUniqueId());
	   }

	   @EventHandler
	   public void onUpdateServer(ServerStatusUpdateEvent event) {
	      event.writeMemberSlots(80);
	   }
	}