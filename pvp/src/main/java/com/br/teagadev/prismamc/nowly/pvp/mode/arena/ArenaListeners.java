package com.br.teagadev.prismamc.nowly.pvp.mode.arena;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.title.TitleAPI;
import com.br.teagadev.prismamc.commons.bukkit.utility.LocationUtil;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import com.br.teagadev.prismamc.nowly.pvp.events.GamerDeathEvent;
import com.br.teagadev.prismamc.nowly.pvp.events.PlayerDamagePlayerEvent;
import com.br.teagadev.prismamc.nowly.pvp.listeners.CombatLogListener;
import com.br.teagadev.prismamc.nowly.pvp.utility.PvPUtility;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.br.teagadev.prismamc.commons.bukkit.api.bossbar.BossBarAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.teagadev.prismamc.nowly.pvp.PvPMain;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.Gamer;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.nowly.pvp.manager.kit.KitManager;

public class ArenaListeners implements Listener {
	   private static final String NOFALL_TAG = "nofall";
	   private static final String NOFALL_TAG_TIME = "nofall.time";

	   @EventHandler(
	      priority = EventPriority.MONITOR
	   )
	   public void onJoin(PlayerJoinEvent event) {
	      Player player = event.getPlayer();
	      player.teleport(ArenaMode.getSpawn());
	      ArenaScoreboard.createScoreboard(player);
	      ArenaMode.refreshPlayer(player);
	      if (BukkitMain.getServerType() == ServerType.PVP_ARENA) {
	         TitleAPI.sendTitle(player, "§f", "§b§lARENA", 0, 0, 2);
	      }

	      player = null;
	   }

	   @EventHandler
	   public void onWarpDeath(GamerDeathEvent event) {
	      Player died = event.getPlayer();
	      PvPUtility.dropItens(died, died.getLocation());
	      Gamer gamer = GamerManager.getGamer(died.getUniqueId());
	      Kit kit1 = KitManager.getKitInfo(gamer.getKit1());
	      Kit kit2 = KitManager.getKitInfo(gamer.getKit2());
	      if (kit1 != null) {
	         kit1.unregisterPlayer(died);
	         kit1 = null;
	      }

	      if (kit2 != null) {
	         kit2.unregisterPlayer(died);
	         kit2 = null;
	      }

	      PvPUtility.handleStats(event.getKiller(), died);
	      if (event.hasKiller()) {
	         ArenaScoreboard.updateScoreboard(event.getKiller());
	      }

	      died = null;
	      gamer = null;
	   }

	   @EventHandler(
	      priority = EventPriority.MONITOR,
	      ignoreCancelled = true
	   )
	   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
	      Player player = event.getPlayer();
	      Player damaged = event.getDamaged();
	      BossBarAPI.send(player, damaged.getName() + " - " + GamerManager.getGamer(damaged.getUniqueId()).getKits(), 3);
	      player = null;
	      damaged = null;
	   }

	   @EventHandler
	   public void onRespawn(PlayerRespawnEvent event) {
	      event.setRespawnLocation(ArenaMode.getSpawn());
	   }

	   @EventHandler
	   public void onAutoRespawn(PlayerRespawnEvent event) {
	      ArenaMode.refreshPlayer(event.getPlayer());
	   }

	   @EventHandler
	   public void onRequest(PlayerRequestEvent event) {
	      if (event.getRequestType().equalsIgnoreCase("update-scoreboard")) {
	         ArenaScoreboard.updateScoreboard(event.getPlayer());
	      } else if (event.getRequestType().equalsIgnoreCase("teleport-spawn")) {
	         if (CombatLogListener.isProtected(event.getPlayer())) {
	            event.getPlayer().sendMessage("§cVocê ja está no spawn!");
	            return;
	         }

	         ArenaMode.refreshPlayer(event.getPlayer());
	         event.getPlayer().teleport(ArenaMode.getSpawn());
	         event.getPlayer().sendMessage("§aVocê foi para o spawn!");
	      }

	   }

	   @EventHandler
	   public void onVoidDamage(EntityDamageEvent event) {
	      if (event.getEntity() instanceof Player) {
	         if (event.getCause() == DamageCause.VOID) {
	            Player player = (Player)event.getEntity();
	            if (GamerManager.getGamer(player).isProtection()) {
	               player.teleport(ArenaMode.getSpawn());
	            } else {
	               CombatLogListener.checkCombatLog(player);
	               ArenaMode.refreshPlayer(player);
	               player.teleport(ArenaMode.getSpawn());
	            }

	            player = null;
	         }
	      }
	   }

	   @EventHandler
	   public void onDamage(EntityDamageEvent event) {
	      if (event.getCause() == DamageCause.FALL) {
	         if (event.getEntity() instanceof Player) {
	            Player player = (Player)event.getEntity();
	            if (player.hasMetadata(NOFALL_TAG)) {
	            	player.removeMetadata(NOFALL_TAG, PvPMain.getInstance());
	               if (!player.hasMetadata(NOFALL_TAG_TIME)) {
	                  event.setCancelled(true);
	                  return;
	               }

	               Long time = player.getMetadata(NOFALL_TAG_TIME).get(0).asLong();
	               if (time + 6000L > System.currentTimeMillis()) {
	                  event.setCancelled(true);
	               }

	               player.removeMetadata(NOFALL_TAG_TIME, PvPMain.getInstance());
	            }

	            player = null;
	         }
	      }
	   }

	   @EventHandler
	   public void onRealMove(PlayerMoveEvent event) {
	      if (LocationUtil.isRealMovement(event.getFrom(), event.getTo())) {
	         if (!event.isCancelled()) {
	            Material material = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
	            Player player = event.getPlayer();
	            if (material.equals(Material.SPONGE)) {
	               double xvel = 0.0D;
	               double yvel = 3.7D;
	               double zvel = 0.0D;
	               player.setVelocity(new Vector(xvel, yvel, zvel));
	               material = null;
	            } else if (material.equals(Material.EMERALD_BLOCK)) {
	               player.setVelocity(player.getLocation().getDirection().multiply(1.3D).setY(0.6D));
	               material = null;
	            }
	            
	            if (LocationUtil.isRealMovement(event.getFrom(), event.getTo())) {
	                Material material1 = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
	                if (material1 == Material.SLIME_BLOCK) {
	                   Player player1 = event.getPlayer();
	                   player1.setVelocity(player1.getLocation().getDirection().multiply(3.1D).setY(0.5D));
	                   player1.playSound(player1.getLocation(), Sound.FIREWORK_LAUNCH, 6.0F, 1.0F);
	                   player1.setFallDistance(-30.0F);
	                }
	            } 

	            if (material == null) {
	               player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 6.0F, 1.0F);
	               player.setMetadata("nofall", new FixedMetadataValue(PvPMain.getInstance(), true));
	               if (player.hasMetadata("nofall.time")) {
	                  player.removeMetadata("nofall.time", PvPMain.getInstance());
	               }

	               player.setMetadata("nofall.time", new FixedMetadataValue(PvPMain.getInstance(), System.currentTimeMillis()));
	               player = null;
	            } else {
	               material = null;
	               Gamer gamer = GamerManager.getGamer(player);
	               if (gamer.isProtection() && player.getLocation().getBlockY() < 95) {
	                  gamer.joinArena();
	                  player.setMetadata("nofall", new FixedMetadataValue(PvPMain.getInstance(), true));
	                  player.setFallDistance(-100.0F);
	                  player.setNoDamageTicks(20);
	                  ItemBuilder itemBuilder = new ItemBuilder();
	                  PlayerInventory playerInventory = player.getInventory();
	                  playerInventory.clear();
	                  playerInventory.setHeldItemSlot(0);
	                  if (gamer.containsKit("PvP")) {
	                     playerInventory.setItem(0, itemBuilder.type(Material.STONE_SWORD).name("§7Espada de Pedra").unbreakable().enchantment(Enchantment.DAMAGE_ALL).build());
	                  } else {
	                     playerInventory.setItem(0, itemBuilder.type(Material.STONE_SWORD).name("§7Espada de Pedra").unbreakable().build());
	                  }

	                  KitManager.giveKitToPlayer(player, gamer.getKit1());
	                  KitManager.giveKitToPlayer(player, gamer.getKit2());
	                  playerInventory.setItem(8, itemBuilder.type(Material.COMPASS).name("§aBússola").build());
	                  playerInventory.setItem(13, itemBuilder.type(Material.BOWL).amount(27).build());
	                  playerInventory.setItem(14, itemBuilder.type(Material.RED_MUSHROOM).amount(27).build());
	                  playerInventory.setItem(15, itemBuilder.type(Material.BROWN_MUSHROOM).amount(27).build());
	                  ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
	                  ItemStack[] var14 = playerInventory.getContents();
	                  int var9 = var14.length;

	                  for(int var10 = 0; var10 < var9; ++var10) {
	                     ItemStack is = var14[var10];
	                     if (is == null) {
	                        playerInventory.addItem(new ItemStack[]{soup});
	                     }
	                  }

	                  player.updateInventory();
	                  playerInventory = null;
	                  soup = null;
	                  itemBuilder = null;
	               }

	               player = null;
	               gamer = null;
	            }
	         }
	      }
	   }
	}