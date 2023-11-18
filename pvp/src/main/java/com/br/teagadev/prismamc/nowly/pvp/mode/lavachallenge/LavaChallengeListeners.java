package com.br.teagadev.prismamc.nowly.pvp.mode.lavachallenge;

import com.br.teagadev.prismamc.commons.bukkit.api.title.TitleAPI;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.nowly.pvp.PvPMain;
import com.br.teagadev.prismamc.nowly.pvp.events.PlayerDamagePlayerEvent;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.nowly.pvp.mode.lavachallenge.LavaChallengeMode.LavaDifficulty;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;

public class LavaChallengeListeners implements Listener {
	   @EventHandler(
	      priority = EventPriority.MONITOR
	   )
	   public void onJoin(PlayerJoinEvent event) {
	      Player player = event.getPlayer();
	      player.teleport(LavaChallengeMode.getSpawn());
	      GamerManager.getGamer(player.getUniqueId()).setProtection(false);
	      LavaChallengeScoreboard.createScoreboard(player);
	      LavaChallengeMode.refreshPlayer(player);
	      if (BukkitMain.getServerType() == ServerType.PVP_LAVACHALLENGE) {
	         TitleAPI.sendTitle(player, "§f", "§b§lLAVA", 0, 0, 3);
	      }

	      player = null;
	   }

	   @EventHandler
	   public void onQuit(PlayerQuitEvent event) {
	      LavaChallengeMode.cleanPlayer(event.getPlayer());
	   }

	   @EventHandler
	   public void onRespawn(PlayerRespawnEvent event) {
	      event.setRespawnLocation(LavaChallengeMode.getSpawn());
	   }

	   @EventHandler
	   public void onAutoRespawn(PlayerRespawnEvent event) {
	      LavaChallengeMode.refreshPlayer(event.getPlayer());
	   }

	   @EventHandler
	   public void onRequest(PlayerRequestEvent event) {
	      if (event.getRequestType().equalsIgnoreCase("update-scoreboard")) {
	         LavaChallengeScoreboard.updateScoreboard(event.getPlayer());
	      } else if (event.getRequestType().equalsIgnoreCase("teleport-spawn")) {
	         LavaChallengeMode.refreshPlayer(event.getPlayer());
	         event.getPlayer().teleport(LavaChallengeMode.getSpawn());
	         event.getPlayer().sendMessage("§aVocê foi para o spawn!");
	      }

	   }

	   @EventHandler
	   public void onDamage(PlayerDamagePlayerEvent event) {
	      event.setCancelled(true);
	   }

	   @EventHandler
	   public void onVelocity(PlayerVelocityEvent event) {
	      event.setCancelled(true);
	   }

	   @EventHandler(
	      priority = EventPriority.LOWEST
	   )
	   public void onDamage(EntityDamageEvent event) {
	      if (event.getEntity() instanceof Player) {
	         if (event.getCause() != DamageCause.LAVA) {
	            event.setCancelled(true);
	         }

	      }
	   }

	   @EventHandler(
	      priority = EventPriority.MONITOR
	   )
	   public void addDamage(EntityDamageEvent event) {
	      if (event.getEntity() instanceof Player) {
	         Player player = (Player)event.getEntity();
	         if (event.getCause() == DamageCause.LAVA) {
	            if (!event.isCancelled()) {
	               this.addLavaDamage(player);
	            }

	         }
	      }
	   }

	   private void addLavaDamage(Player player) {
	      int amount = (Integer)LavaChallengeMode.lavaDamages.getOrDefault(player.getUniqueId(), 0);
	      LavaChallengeMode.lavaDamages.put(player.getUniqueId(), amount + 1);
	   }

	   @EventHandler
	   public void onInteract(PlayerInteractEvent event) {
	      if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null && (event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST))) {
	         Sign sign = (Sign)event.getClickedBlock().getState();
	         String[] lines = sign.getLines();
	         Player player = event.getPlayer();
	         int lavaDmg = (Integer)LavaChallengeMode.lavaDamages.get(player.getUniqueId());
	         if (lines[1].contains("LAVACHALLENGE")) {
	            LavaDifficulty difficulty = LavaDifficulty.FACIL;
	            if (lines[2].contains("Médio")) {
	               difficulty = LavaDifficulty.MEDIO;
	            } else if (lines[2].contains("Difícil")) {
	               difficulty = LavaDifficulty.DIFICIL;
	            } else if (lines[2].contains("Extreme")) {
	               difficulty = LavaDifficulty.EXTREMO;
	            }

	            if (lavaDmg < difficulty.getMinHits()) {
	               Bukkit.broadcastMessage("o desgraçado do " + player.getName() + " completou o lava muito rapido!");
	            } else {
	               this.handleCompleteLava(player, difficulty);
	            }

	            player.teleport(LavaChallengeMode.getSpawn());
	            LavaChallengeMode.refreshPlayer(player);
	         }
	      }

	   }

	   public void handleCompleteLava(Player player, LavaDifficulty difficulty) {
	      BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	      switch(difficulty) {
	      case FACIL:
	         bukkitPlayer.getDataHandler().getData(DataType.PVP_LAVACHALLENGE_FACIL).add();
	         bukkitPlayer.getDataHandler().getData(DataType.COINS).add(200);
	         bukkitPlayer.addXP(50);
	         player.sendMessage("§aVocê concluiu a dificuldade %dificuldade%".replace("%dificuldade%", "Fácil"));
	         break;
	      case MEDIO:
	         bukkitPlayer.getDataHandler().getData(DataType.PVP_LAVACHALLENGE_MEDIO).add();
	         bukkitPlayer.getDataHandler().getData(DataType.COINS).add(300);
	         bukkitPlayer.addXP(150);
	         player.sendMessage("§aVocê concluiu a dificuldade %dificuldade%".replace("%dificuldade%", "Médio"));
	         break;
	      case DIFICIL:
	         bukkitPlayer.getDataHandler().getData(DataType.PVP_LAVACHALLENGE_DIFICIL).add();
	         bukkitPlayer.getDataHandler().getData(DataType.COINS).add(400);
	         bukkitPlayer.addXP(250);
	         player.sendMessage("§aVocê concluiu a dificuldade %dificuldade%".replace("%dificuldade%", "Dificil"));
	         break;
	      case EXTREMO:
	         bukkitPlayer.getDataHandler().getData(DataType.PVP_LAVACHALLENGE_EXTREMO).add();
	         bukkitPlayer.getDataHandler().getData(DataType.COINS).add(500);
	         bukkitPlayer.addXP(300);
	         player.sendMessage("§aVocê concluiu a dificuldade %dificuldade%".replace("%dificuldade%", "Extremo"));
	      }

	      PvPMain.runAsync(() -> {
	         bukkitPlayer.getDataHandler().saveCategorys(new DataCategory[]{DataCategory.ACCOUNT, DataCategory.KITPVP});
	      });
	   }
	}