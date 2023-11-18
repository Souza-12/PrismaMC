package com.br.teagadev.prismamc.lobby.common.scoreboard.types;

import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import org.bukkit.entity.Player;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.br.teagadev.prismamc.lobby.api.TabListAPI;
import com.br.teagadev.prismamc.lobby.common.scoreboard.ScoreboardCommon;

public class LobbyPvPScoreboard extends ScoreboardCommon {
	   public void createScoreboard(Player player) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	      Groups group = bukkitPlayer.getGroup();
	      sidebar.setTitle("§b§lPVP");
	      sidebar.addBlankLine();
	      sidebar.addLine("§7Bem-vindo ao PvP");
	      sidebar.addLine("§7Selecione um modo!");
	      sidebar.addBlankLine();
	      sidebar.addLine("rank", "Cargo: ", group.getColor() + (group.getLevel() == Groups.MEMBRO.getLevel() ? "Membro" : group.getTag().getColor() + group.getTag().getName()));
	      sidebar.addBlankLine();
	      sidebar.addLine("lobbyId", "Lobby: ", "§7#" + BukkitMain.getServerID());
	      sidebar.addLine("onlines", "Players: ", "§a" + StringUtility.formatValue(CommonsGeneral.getServersManager().getAmountOnNetwork()));
	      sidebar.addBlankLine();
	      sidebar.addLine("§ecrazzymc.com");
	      sidebar.update();
	      sidebar = null;
	   }

	   public void updateScoreboard(Player player) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	      Groups group = bukkitPlayer.getGroup();
	      sidebar.updateLine("rank", "Cargo: ", group.getColor() + (group.getLevel() == Groups.MEMBRO.getLevel() ? "Membro" : group.getTag().getColor() + group.getTag().getName()));
	      sidebar.updateLine("onlines", "Players: ", "§a" + StringUtility.formatValue(CommonsGeneral.getServersManager().getAmountOnNetwork()));
	   }
	   
		public void updateTab(final Player p) {
			TabListAPI.setHeaderAndFooter(p, "§f\n§6§LCRAZZY\n",
					"\n§6Loja: §floja.crazzymc.com\n§6Discord: §fdiscord.gg/crazzymc\n§f");
		}
	}