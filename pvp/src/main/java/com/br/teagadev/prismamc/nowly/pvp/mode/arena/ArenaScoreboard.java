package com.br.teagadev.prismamc.nowly.pvp.mode.arena;

import org.bukkit.entity.Player;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.Gamer;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.GamerManager;

public class ArenaScoreboard {
	   public static void createScoreboard(Player player) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      sidebar.setTitle("§b§lPVP : ARENA");
	      BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	      sidebar.addBlankLine();
	      sidebar.addLine("kills", "Kills: ", "§7" + bukkitPlayer.getIntFormatted(DataType.PVP_KILLS));
	      sidebar.addLine("deaths", "Deaths: ", "§7" + bukkitPlayer.getIntFormatted(DataType.PVP_DEATHS));
	      sidebar.addLine("killstreak", "Killstreak: ", "§a" + bukkitPlayer.getIntFormatted(DataType.PVP_KILLSTREAK));
	      sidebar.addBlankLine();
	      sidebar.addLine("kit1", "Kit 1: ", "§aNenhum");
	      sidebar.addLine("kit2", "Kit 2: ", "§aNenhum");
	      sidebar.addBlankLine();
	      sidebar.addLine("§6crazzymc.com");
	      sidebar.update();
	   }

	   public static void updateScoreboard(Player player) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	      Gamer gamer = GamerManager.getGamer(player.getUniqueId());
	      sidebar.updateLine("killstreak", "Killstreak: ", "§a" + bukkitPlayer.getIntFormatted(DataType.PVP_KILLSTREAK));
	      sidebar.updateLine("kills", "Kills: ", "§7" + bukkitPlayer.getIntFormatted(DataType.PVP_KILLS));
	      sidebar.updateLine("deaths", "Deaths: ", "§7" + bukkitPlayer.getIntFormatted(DataType.PVP_DEATHS));
	      sidebar.updateLine("kit1", "Kit 1: ", "§a" + gamer.getKit1());
	      sidebar.updateLine("kit2", "Kit 2: ", "§a" + gamer.getKit2());
	   }
	}