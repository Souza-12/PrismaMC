package com.br.teagadev.prismamc.nowly.pvp.mode.arena;

import com.br.teagadev.prismamc.nowly.pvp.menu.KitSelector;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.PlayerInventory;

import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ActionItemStack;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ActionItemStack.InteractHandler;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.vanish.VanishAPI;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.teagadev.prismamc.nowly.pvp.PvPMain;
import com.br.teagadev.prismamc.nowly.pvp.StringUtils;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.Gamer;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.nowly.pvp.manager.kit.KitLoader;
import com.br.teagadev.prismamc.nowly.pvp.menu.enums.InventoryMode;

import lombok.Getter;
import lombok.Setter;

public class ArenaMode {
	   private static Location spawn;
	   private static final InteractHandler lobbyAction = (player, itemStack, itemAction, clickedBlock) -> {
	      if (itemAction.name().contains("LEFT")) {
	         return true;
	      } else {
	         BukkitServerAPI.redirectPlayer(player, "LobbyPvP");
	         return true;
	      }
	   };
	   private static final InteractHandler kitPrimaryAction = (player, itemStack, itemAction, clickedBlock) -> {
	      if (itemAction.name().contains("LEFT")) {
	         return true;
	      } else {
	         (new KitSelector(player, InventoryMode.KIT_PRIMARIO)).open(player);
	         return true;
	      }
	   };
	   private static final InteractHandler kitSecundaryAction = (player, itemStack, itemAction, clickedBlock) -> {
	      if (itemAction.name().contains("LEFT")) {
	         return true;
	      } else {
	         (new KitSelector(player, InventoryMode.KIT_SECUNDARIO)).open(player);
	         return true;
	      }
	   };
	   private static final InteractHandler kitShopAction = (player, itemStack, itemAction, clickedBlock) -> {
	      if (itemAction.name().contains("LEFT")) {
	         return true;
	      } else {
	         (new KitSelector(player, InventoryMode.LOJA)).open(player);
	         return true;
	      }
	   };
	   private static final InteractHandler compassAction = (player, itemStack, itemAction, clickedBlock) -> {
	      if (itemAction == Action.PHYSICAL) {
	         return true;
	      } else {
	         Player alvo = getRandomPlayer(player);
	         if (alvo == null) {
	            player.sendMessage("§eNenhum jogador por perto, bússola apontando para o spawn.");
	            player.setCompassTarget(player.getWorld().getSpawnLocation());
	         } else {
	            player.sendMessage("§eSua bússola está apontando para §b%nick%".replace("%nick%", alvo.getName()));
	            player.setCompassTarget(alvo.getLocation());
	         }

	         return true;
	      }
	   };

	   public static void init() {
	      PluginConfiguration.createLocation(PvPMain.getInstance(), "spawn");
	      setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
	      KitLoader.load();
	      ActionItemStack.register("§cRetornar ao Lobby", lobbyAction);
	      ActionItemStack.register("§aSelecionar kit 1", kitPrimaryAction);
	      ActionItemStack.register("§aSelecionar kit 2", kitSecundaryAction);
	      ActionItemStack.register("§aLoja de Kits", kitShopAction);
	      ActionItemStack.register("§aBússola", compassAction);
	      setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
	      Bukkit.getServer().getPluginManager().registerEvents(new ArenaListeners(), PvPMain.getInstance());
	   }

	   public static void refreshPlayer(Player player) {
	      Gamer gamer = GamerManager.getGamer(player.getUniqueId());
	      gamer.setProtection(true);
	      if (player.getPassenger() != null) {
	         player.getPassenger().leaveVehicle();
	      }

	      if (player.isInsideVehicle()) {
	         player.leaveVehicle();
	      }

	      player.setNoDamageTicks(20);
	      player.setFallDistance(-5.0F);
	      player.getInventory().clear();
	      player.getInventory().setArmorContents(null);
	      player.updateInventory();
	      player.setFireTicks(0);
	      player.setLevel(0);
	      player.setExp(0.0F);
	      player.setHealth(20.0D);
	      PlayerAPI.clearEffects(player);
	      if (player.getGameMode() != GameMode.SURVIVAL) {
	         player.setGameMode(GameMode.SURVIVAL);
	      }

	      ItemBuilder itemBuilder = new ItemBuilder();
	      PlayerInventory playerInventory = player.getInventory();
	      playerInventory.setHeldItemSlot(0);
	      playerInventory.setItem(0, itemBuilder.type(Material.CHEST).name("§aSelecionar kit 1").build());
	      playerInventory.setItem(1, itemBuilder.type(Material.CHEST).amount(2).name("§aSelecionar kit 2").build());
	      playerInventory.setItem(2, itemBuilder.type(Material.EMERALD).name("§aLoja de Kits").build());
	      playerInventory.setItem(8, itemBuilder.type(Material.BED).name("§cRetornar ao Lobby").build());
	      player.updateInventory();
	      ArenaScoreboard.updateScoreboard(player);
	      playerInventory = null;
	      itemBuilder = null;
	      gamer = null;
	      player = null;
	   }

	   private static Player getRandomPlayer(Player player) {
	      Player target = null;
	      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

	      while(var2.hasNext()) {
	         Player inWarp = (Player)var2.next();
	         if (inWarp != player && !VanishAPI.isInvisible(inWarp) && inWarp.getLocation().distance(player.getLocation()) >= 15.0D) {
	            if (target == null) {
	               target = inWarp;
	            } else {
	               double distanciaAtual = target.getLocation().distance(player.getLocation());
	               double novaDistancia = inWarp.getLocation().distance(player.getLocation());
	               if (novaDistancia < distanciaAtual) {
	                  target = inWarp;
	                  if (novaDistancia <= 30.0D) {
	                     break;
	                  }
	               }
	            }
	         }
	      }

	      return target;
	   }

	   public static Location getSpawn() {
	      return spawn;
	   }

	   public static void setSpawn(Location spawn) {
	      ArenaMode.spawn = spawn;
	   }
	}