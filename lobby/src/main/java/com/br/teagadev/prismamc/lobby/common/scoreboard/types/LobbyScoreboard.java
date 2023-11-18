package com.br.teagadev.prismamc.lobby.common.scoreboard.types;

import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.teagadev.prismamc.commons.common.data.DataHandler;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.lobby.LobbyMain;
import com.br.teagadev.prismamc.lobby.api.TabListAPI;
import com.br.teagadev.prismamc.lobby.common.scoreboard.animating.StringAnimation;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.br.teagadev.prismamc.lobby.common.scoreboard.ScoreboardCommon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class LobbyScoreboard extends ScoreboardCommon {
	   private StringAnimation animation;
	   private String text = "";

	   public void init() {
	      this.animation = new StringAnimation(" CRAZZY ", "§f§l", "§b§l", "§e§l", 3);
	      this.text = this.animation.next();
	      this.registerListener();
	      Bukkit.getScheduler().runTaskTimer(LobbyMain.getInstance(), new Runnable() {
	         public void run() {
	            LobbyScoreboard.this.text = LobbyScoreboard.this.animation.next();
	            Iterator var1 = Bukkit.getOnlinePlayers().iterator();

	            while(var1.hasNext()) {
	               Player onlines = (Player)var1.next();
	               if (onlines != null && onlines.isOnline() && !onlines.isDead()) {
	                  Scoreboard score = onlines.getScoreboard();
	                  if (score != null) {
	                     Objective objective = score.getObjective(DisplaySlot.SIDEBAR);
	                     if (objective != null) {
	                        objective.setDisplayName(LobbyScoreboard.this.text);
	                     }
	                  }
	               }
	            }

	         }
	      }, 40L, 2L);
	   }

	   public void createScoreboard(Player player) {
	      Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
	      sidebar.setTitle("§6§lCRAZZY");
	      BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	      Groups group = bukkitPlayer.getGroup();
	      sidebar.addBlankLine();
	      sidebar.addLine("rank", "Cargo: ", group.getColor() + (group.getLevel() == Groups.MEMBRO.getLevel() ? "Membro" : group.getTag().getColor() + group.getTag().getName()));
	      sidebar.addBlankLine();
	      sidebar.addLine("lobbyId", "Lobby: ", "§7#" + BukkitMain.getServerID());
	      sidebar.addLine("onlines", "Players: ", "§a" + StringUtility.formatValue(CommonsGeneral.getServersManager().getAmountOnNetwork()));
	      sidebar.addBlankLine();
	      sidebar.addLine("§ecrazzymc.com");
	      sidebar.update();
	      group = null;
	      bukkitPlayer = null;
	      sidebar = null;
	   }

	   public void updateScoreboard(Player player) {
	      BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	      Groups group = bukkitPlayer.getGroup();
	      SidebarManager.getSidebar(player.getUniqueId()).updateLine("rank", "Cargo: ", group.getColor() + (group.getLevel() == Groups.MEMBRO.getLevel() ? "Membro" : group.getTag().getColor() + group.getTag().getName()));
	      SidebarManager.getSidebar(player.getUniqueId()).updateLine("onlines", "Players: ", "§a" + StringUtility.formatValue(CommonsGeneral.getServersManager().getAmountOnNetwork()));
	   }

	   private void registerListener() {
	      Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
	         @EventHandler
	         public void update(BukkitUpdateEvent event) {
	            if (event.getType() == UpdateType.SEGUNDO) {
	               Iterator var2 = Bukkit.getOnlinePlayers().iterator();

	               while(var2.hasNext()) {
	                  Player onlines = (Player)var2.next();
	                  LobbyScoreboard.this.updateScoreboard(onlines);
	                  LobbyScoreboard.this.updateTab(onlines);
	               }

	            }
	         }
	      }, LobbyMain.getInstance());
	   }
	   
		public void updateTab(final Player p) {
			TabListAPI.setHeaderAndFooter(p, "§f\n§6§LCRAZZY\n",
					"\n§6Loja: §floja.crazzymc.com\n§6Discord: §fdiscord.gg/crazzymc\n§f");
		}
	}