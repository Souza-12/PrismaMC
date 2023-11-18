package com.br.teagadev.prismamc.nowly.pvp.mode.lavachallenge;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.teagadev.prismamc.nowly.pvp.PvPMain;


public class LavaChallengeMode {
	   private static Location spawn;
	   public static HashMap<UUID, Integer> lavaDamages;

	   public static void init() {
	      lavaDamages = new HashMap();
	      setSpawn(PluginConfiguration.createLocation(PvPMain.getInstance(), "spawn"));
	      Bukkit.getServer().getPluginManager().registerEvents(new LavaChallengeListeners(), PvPMain.getInstance());
	   }

	   public static void refreshPlayer(Player player) {
	      if (player.getPassenger() != null) {
	         player.getPassenger().leaveVehicle();
	      }

	      if (player.isInsideVehicle()) {
	         player.leaveVehicle();
	      }

	      player.setNoDamageTicks(20);
	      player.setFallDistance(-5.0F);
	      player.getInventory().clear();
	      player.getInventory().setArmorContents((ItemStack[])null);
	      player.updateInventory();
	      player.setFireTicks(0);
	      player.setLevel(0);
	      player.setExp(0.0F);
	      player.setHealth(20.0D);
	      PlayerAPI.clearEffects(player);
	      if (player.getGameMode() != GameMode.SURVIVAL) {
	         player.setGameMode(GameMode.SURVIVAL);
	      }

	      PlayerInventory playerInventory = player.getInventory();
	      ItemBuilder itemBuilder = new ItemBuilder();
	      playerInventory.setItem(13, itemBuilder.type(Material.BOWL).amount(64).build());
	      playerInventory.setItem(14, itemBuilder.type(Material.RED_MUSHROOM).amount(64).build());
	      playerInventory.setItem(15, itemBuilder.type(Material.BROWN_MUSHROOM).amount(64).build());
	      ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
	      ItemStack[] var4 = playerInventory.getContents();
	      int var5 = var4.length;

	      for(int var6 = 0; var6 < var5; ++var6) {
	         ItemStack is = var4[var6];
	         if (is == null) {
	            playerInventory.addItem(new ItemStack[]{soup});
	         }
	      }

	      player.updateInventory();
	      lavaDamages.put(player.getUniqueId(), 0);
	      LavaChallengeScoreboard.updateScoreboard(player);
	   }

	   public static void updateDifficulty(int minDamageFacil, int minDamageMedio, int minDamageDificil, int minDamageExtremo) {
	      LavaChallengeMode.LavaDifficulty.FACIL.setMinHits(minDamageFacil);
	      LavaChallengeMode.LavaDifficulty.MEDIO.setMinHits(minDamageMedio);
	      LavaChallengeMode.LavaDifficulty.DIFICIL.setMinHits(minDamageDificil);
	      LavaChallengeMode.LavaDifficulty.EXTREMO.setMinHits(minDamageExtremo);
	   }

	   public static void cleanPlayer(Player player) {
	      if (lavaDamages.containsKey(player.getUniqueId())) {
	         lavaDamages.remove(player.getUniqueId());
	      }

	   }

	   public static Location getSpawn() {
	      return spawn;
	   }

	   public static void setSpawn(Location spawn) {
	      LavaChallengeMode.spawn = spawn;
	   }

	   public static enum LavaDifficulty {
	      FACIL(20),
	      MEDIO(20),
	      DIFICIL(20),
	      EXTREMO(20);

	      int minHits;

	      private LavaDifficulty(int minHits) {
	         this.minHits = minHits;
	      }

	      public int getMinHits() {
	         return this.minHits;
	      }

	      public void setMinHits(int minHits) {
	         this.minHits = minHits;
	      }
	   }
	}