package com.br.teagadev.prismamc.nowly.pvp.utility;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.nowly.pvp.PvPMain;

public class PvPUtility {
	   public static void dropItens(Player player, Location loc) {
	      ArrayList<ItemStack> itens = new ArrayList();
	      ItemStack[] var3 = player.getPlayer().getInventory().getContents();
	      int var4 = var3.length;

	      for(int var5 = 0; var5 < var4; ++var5) {
	         ItemStack item = var3[var5];
	         if (item != null && item.getType() != Material.AIR && (item.getType() == Material.BROWN_MUSHROOM || item.getType() == Material.RED_MUSHROOM || item.getType() == Material.BOWL || item.getType() == Material.MUSHROOM_SOUP)) {
	            itens.add(item);
	         }
	      }

	      PlayerAPI.dropItems(player, itens, loc);
	      itens.clear();
	   }

	   public static void handleStats(Player killer, Player died) {
	      BukkitPlayer bukkitPlayerDied = BukkitMain.getBukkitPlayer(died.getUniqueId());
	      if (killer != null) {
	         died.sendMessage("§cVocê morreu para %nick%!".replace("%nick%", killer.getName()));
	         checkStreakLose(killer.getName(), died.getName(), "§c%loser% perdeu sua taxa de killstreak de %valor% para %killer%", bukkitPlayerDied.getInt(DataType.PVP_KILLSTREAK));
	      } else {
	         died.sendMessage("§cVocê morreu!");
	      }

	      died.sendMessage("§c-%quantia% XP".replace("%quantia%", "2"));
	      died.sendMessage("§c-%quantia% coins".replace("%quantia%", "10"));
	      bukkitPlayerDied.add(DataType.PVP_DEATHS);
	      bukkitPlayerDied.remove(DataType.COINS, 10);
	      bukkitPlayerDied.removeXP(2);
	      bukkitPlayerDied.set(DataType.PVP_KILLSTREAK, 0);
	      if (killer == null) {
	         PvPMain.runAsync(() -> {
	            bukkitPlayerDied.getDataHandler().saveCategorys(new DataCategory[]{DataCategory.ACCOUNT, DataCategory.KITPVP});
	         });
	      } else {
	         BukkitPlayer bukkitPlayerKiller = BukkitMain.getBukkitPlayer(killer.getUniqueId());
	         killer.sendMessage("§cVocê matou %nick%".replace("%nick%", died.getName()));
	         int xp = PlayerAPI.getXPKill(killer, bukkitPlayerKiller.getLong(DataType.DOUBLEXP_TIME));
	         int coins = PlayerAPI.getCoinsKill(killer, bukkitPlayerKiller.getLong(DataType.DOUBLECOINS_TIME));
	         bukkitPlayerKiller.add(DataType.COINS, coins);
	         bukkitPlayerKiller.addXP(xp);
	         bukkitPlayerKiller.add(DataType.PVP_KILLS);
	         int atualKillStreak = bukkitPlayerKiller.getInt(DataType.PVP_KILLSTREAK) + 1;
	         bukkitPlayerKiller.set(DataType.PVP_KILLSTREAK, atualKillStreak);
	         if (atualKillStreak > bukkitPlayerKiller.getInt(DataType.PVP_MAXKILLSTREAK)) {
	            bukkitPlayerKiller.set(DataType.PVP_MAXKILLSTREAK, atualKillStreak);
	         }

	         checkStreakWin(killer.getName(), atualKillStreak, "§e%nick% conseguiu atingir a taxa de killstreak de §b%valor%");
	         PvPMain.runAsync(() -> {
	            bukkitPlayerKiller.getDataHandler().saveCategorys(new DataCategory[]{DataCategory.ACCOUNT, DataCategory.KITPVP});
	            bukkitPlayerDied.getDataHandler().saveCategorys(new DataCategory[]{DataCategory.ACCOUNT, DataCategory.KITPVP});
	         });
	      }
	   }

	   public static void checkStreakWin(String nick, int value, String message) {
	      if (value >= 10 && value % 10 == 0) {
	         Bukkit.broadcastMessage(message.replace("%nick%", nick).replace("%valor%", "" + value));
	      }

	   }

	   public static void checkStreakLose(String killer, String loser, String message, int winstreak) {
	      if (winstreak >= 10) {
	         Bukkit.broadcastMessage(message.replace("%loser%", loser).replace("%killer%", killer).replace("%valor%", "" + winstreak));
	      }

	   }

	   public static void repairArmor(Player killer) {
	      killer.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
	      killer.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
	      killer.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
	      killer.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
	   }
	}