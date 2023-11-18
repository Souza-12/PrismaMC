package com.br.teagadev.prismamc.lobby.listeners;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerChangeScoreboardEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerChangeScoreboardEvent.ScoreboardChangeType;
import com.br.teagadev.prismamc.commons.bukkit.utility.LocationUtil;
import com.br.teagadev.prismamc.lobby.LobbyMain;
import com.br.teagadev.prismamc.lobby.commands.ServerCommand;
import com.br.teagadev.prismamc.lobby.common.inventory.InventoryCommon;
import com.br.teagadev.prismamc.lobby.common.scoreboard.animating.StringAnimation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class GeneralListeners implements Listener {
   private StringAnimation animation;
   private String text = "";
   public static int MINUTES = 0;

   @EventHandler
   public void onRequest(PlayerRequestEvent event) {
      if (event.getRequestType().equalsIgnoreCase("update-scoreboard")) {
         LobbyMain.getScoreBoardCommon().createScoreboard(event.getPlayer());
      }

   }

   @EventHandler
   public void onMinute(BukkitUpdateEvent event) {
      if (event.getType() == UpdateType.MINUTO && MINUTES++ == 20) {
         if (LobbyMain.getHologramCommon() != null) {
            LobbyMain.getHologramCommon().update();
         }

         MINUTES = 0;
      }

   }

   @EventHandler
   public void onRealMove(PlayerMoveEvent event) {
      if (LocationUtil.isRealMovement(event.getFrom(), event.getTo())) {
         Material material = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
         if (material == Material.SLIME_BLOCK) {
            Player player = event.getPlayer();
            player.setVelocity(player.getLocation().getDirection().multiply(3.1D).setY(0.5D));
            player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 6.0F, 1.0F);
            player.setFallDistance(-30.0F);
         }
      }

   }

   @EventHandler
   public void onSecond(BukkitUpdateEvent event) {
      if (event.getType() == UpdateType.SEGUNDO) {
         CommonsGeneral.getServersManager().sendRequireUpdate();
         InventoryCommon.update();
         if (LobbyMain.getNpcCommon() != null) {
            LobbyMain.getNpcCommon().update();
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onUpdateScoreboard(BukkitUpdateEvent event) {
      if (event.getType() == UpdateType.TICK && event.getCurrentTick() % 40L == 0L) {
         Bukkit.getOnlinePlayers().forEach((player) -> {
            LobbyMain.getScoreBoardCommon().updateScoreboard(player);
         });
      }

   }

   @EventHandler
   public void onChangeScoreboard(PlayerChangeScoreboardEvent event) {
      if (event.getChangeType() == ScoreboardChangeType.DESATIVOU) {
         event.setCancelled(true);
      } else {
         LobbyMain.getScoreBoardCommon().createScoreboard(event.getPlayer());
      }

   }

   @EventHandler
   public void onSpread(BlockSpreadEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onFood(FoodLevelChangeEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onWeatherChange(WeatherChangeEvent event) {
      event.setCancelled(event.toWeatherState());
   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
      event.setCancelled(true);
      if (event.getCause() == DamageCause.VOID) {
         event.getEntity().teleport(LobbyMain.getSpawn());
      }

   }

   @EventHandler
   public void onLeavesDecay(LeavesDecayEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockBurn(BlockBurnEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockFade(BlockFadeEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onPortal(PlayerPortalEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockFromTo(BlockFromToEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockPhysics(BlockPhysicsEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onCraft(CraftItemEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onExplosion(ExplosionPrimeEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onDamage(EntityDamageByEntityEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockIgnite(BlockIgniteEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onEntitySpawn(CreatureSpawnEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onDrop(PlayerDropItemEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onPickup(PlayerPickupItemEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onItemSpawn(ItemSpawnEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onPhysics(BlockPhysicsEvent event) {
      event.setCancelled(true);
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onBreak(BlockBreakEvent event) {
      event.setCancelled(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE) || !ServerCommand.autorizados.contains(event.getPlayer().getUniqueId()));
   }

   @EventHandler
   public void onPlace(BlockPlaceEvent event) {
      event.setCancelled(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE) || !ServerCommand.autorizados.contains(event.getPlayer().getUniqueId()));
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.PHYSICAL) {
         event.setCancelled(true);
      } else {
         event.setCancelled(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE) || !ServerCommand.autorizados.contains(event.getPlayer().getUniqueId()));
      }

   }

   @EventHandler
   public void onRegain(EntityRegainHealthEvent event) {
      event.setCancelled(true);
   }
}