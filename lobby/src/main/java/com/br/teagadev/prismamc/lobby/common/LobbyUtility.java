package com.br.teagadev.prismamc.lobby.common;

import org.bukkit.entity.Player;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.commons.common.serverinfo.types.NetworkServer;
import com.br.teagadev.prismamc.lobby.common.inventory.InventoryCommon;

public class LobbyUtility {
	   public static void handleInteract(Player player, ServerType serverClicked, ServerType serverConnected) {
	      if (serverClicked != ServerType.UNKNOWN) {
	         if (serverConnected != ServerType.LOBBY) {
	            if (serverClicked == ServerType.HARDCORE_GAMES) {
	               InventoryCommon.getHardcoreGamesInventory().open(player);
	            } else if (serverClicked == ServerType.LOBBY_PVP) {
	               checkAndConnect(player, serverClicked.getName());
	            } else if (serverClicked == ServerType.LOBBY_DUELS) {
	            	checkAndConnect(player, serverClicked.getName());   
	           } else if (serverClicked != ServerType.EVENTO && serverClicked != ServerType.MINIPRISMA) {
	               checkAndConnect(player, serverClicked.getName());
	            } else if (CommonsGeneral.getServersManager().getNetworkServer(serverClicked.getName()).isOnline()) {
	               BukkitServerAPI.redirectPlayer(player, serverClicked.getName());
	            } else {
	               player.sendMessage("§cNenhuma sala disponivel no momento.");
	            }
	         } else {
	            checkAndConnect(player, serverClicked.getName());
	         }
	      } else {
	         player.sendMessage("§cEm breve.");
	      }

	   }

	   private static void checkAndConnect(Player player, String serverName) {
	      NetworkServer server = CommonsGeneral.getServersManager().getNetworkServer(serverName);
	      if (server != null) {
	         if (server.isOnline()) {
	            boolean connect = true;
	            if (server.getOnlines() >= server.getMembersSlots() && !player.hasPermission("commons.entrar")) {
	               connect = false;
	               player.sendMessage("§cOs slots para membros acabaram.");
	            }

	            if (connect) {
	               BukkitServerAPI.redirectPlayer(player, serverName);
	            }
	         } else {
	            player.sendMessage("§cNenhuma sala disponivel no momento.");
	         }
	      }

	   }
	}