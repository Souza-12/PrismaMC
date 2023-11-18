package com.br.teagadev.prismamc.lobby.common.scoreboard.types;

import org.bukkit.entity.Player;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.br.teagadev.prismamc.lobby.api.TabListAPI;
import com.br.teagadev.prismamc.lobby.common.scoreboard.ScoreboardCommon;

public class LobbyHGScoreboard extends ScoreboardCommon {
	   public void createScoreboard(Player player) {
	      BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      sidebar.setTitle("§6§lHARDCORE GAMES");
	      sidebar.addBlankLine();
	      sidebar.addLine("§eHG Mix:");
	      sidebar.addLine("hgWins", " Wins: §a" + bukkitPlayer.getIntFormatted(DataType.HG_WINS));
	      sidebar.addLine("hgKills", " Kills: §a" + bukkitPlayer.getIntFormatted(DataType.HG_KILLS));
	      sidebar.addBlankLine();
	      sidebar.addLine("§eEvento:");
	      sidebar.addLine("eventWins", " Wins: §a" + bukkitPlayer.getIntFormatted(DataType.HG_EVENT_WINS));
	      sidebar.addLine("eventKills", " Kills: §a" + bukkitPlayer.getIntFormatted(DataType.HG_EVENT_KILLS));
	      sidebar.addBlankLine();
	      sidebar.addLine("coins", "Coins: §6" + bukkitPlayer.getIntFormatted(DataType.COINS));
	      sidebar.addLine("onlines", "Players: ", "§a" + StringUtility.formatValue(CommonsGeneral.getServersManager().getAmountOnNetwork()));
	      sidebar.addBlankLine();
	      sidebar.addLine("§ecrazzymc.com");
	      sidebar.update();
	   }

	   public void updateScoreboard(Player player) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      if (sidebar != null) {
	         BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	         sidebar.updateLine("hgWins", " Wins: §a" + bukkitPlayer.getIntFormatted(DataType.HG_WINS));
	         sidebar.updateLine("hgKills", " Kills: §a" + bukkitPlayer.getIntFormatted(DataType.HG_KILLS));
	         sidebar.updateLine("eventWins", " Wins: §a" + bukkitPlayer.getIntFormatted(DataType.HG_EVENT_WINS));
	         sidebar.updateLine("eventKills", " Kills: §a" + bukkitPlayer.getIntFormatted(DataType.HG_EVENT_KILLS));
	         sidebar.updateLine("onlines", "Players: ", "§a" + StringUtility.formatValue(CommonsGeneral.getServersManager().getAmountOnNetwork()));
	      }
	   }
	   
		public void updateTab(final Player p) {
			TabListAPI.setHeaderAndFooter(p, "§f\n§6§LCRAZZY\n",
					"\n§6Loja: §floja.crazzymc.com\n§6Discord: §fdiscord.gg/crazzymc\n§f");
		}
	}