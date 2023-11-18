package com.br.teagadev.prismamc.nowly.pvp.manager.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class KitManager {
	   private static final HashMap<String, Kit> kits = new HashMap();
	   private static final String[] combinationsBlockeds = new String[]{"kangaroo-nofall", "nofall-kangaroo", "stomper-switcher", "switcher-stomper", "stomper-hulk", "hulk-stomper", "grappler-kangaroo", "kangaroo-grappler", "ajnin-ninja", "ninja-ajnin"};

	   public static void giveKitToPlayer(Player player, String kitName) {
	      Kit kit = getKitInfo(kitName);
	      if (kit != null) {
	         kit.registerPlayer();
	         if (kit.getItens() != null) {
	            ItemStack[] var3 = kit.getItens();
	            int var4 = var3.length;

	            for(int var5 = 0; var5 < var4; ++var5) {
	               ItemStack item = var3[var5];
	               player.getInventory().addItem(new ItemStack[]{item});
	            }
	         }
	      }

	   }

	   public List<Kit> getAllKits() {
	      return new ArrayList(kits.values());
	   }

	   public static List<Kit> getPlayerKits(Player player) {
	      List<Kit> playerKits = new ArrayList();
	      Iterator var2 = kits.values().iterator();

	      while(true) {
	         Kit kit;
	         do {
	            if (!var2.hasNext()) {
	               return playerKits;
	            }

	            kit = (Kit)var2.next();
	         } while(!player.hasPermission("pvp.kit." + kit.getName().toLowerCase()) && !player.hasPermission("pvp.kit.all"));

	         playerKits.add(kit);
	      }
	   }

	   public static Kit getKitInfo(String nome) {
	      return (Kit)kits.getOrDefault(nome.toLowerCase(), (Kit)null);
	   }

	   public static boolean hasPermissionKit(Player player, String kit, boolean msg) {
	      if (kit.equalsIgnoreCase("PvP")) {
	         return true;
	      } else if (player.hasPermission("pvp.kit.all")) {
	         return true;
	      } else if (player.hasPermission("pvp.kit." + kit.toLowerCase())) {
	         return true;
	      } else {
	         if (msg) {
	            player.sendMessage("§cVocê não possuí este Kit.");
	         }

	         return false;
	      }
	   }

	   public static boolean isSameKit(String kit, String otherKit) {
	      return kit.equalsIgnoreCase(otherKit);
	   }

	   public static boolean hasCombinationOp(String kit, String otherKit) {
	      boolean isOp = false;
	      String playerCombination1 = kit + "-" + otherKit;
	      String playerCombination2 = otherKit + "-" + kit;
	      String[] var5 = combinationsBlockeds;
	      int var6 = var5.length;

	      for(int var7 = 0; var7 < var6; ++var7) {
	         String combinations = var5[var7];
	         if (playerCombination1.equalsIgnoreCase(combinations)) {
	            isOp = true;
	            break;
	         }

	         if (playerCombination2.equalsIgnoreCase(combinations)) {
	            isOp = true;
	            break;
	         }
	      }

	      return isOp;
	   }

	   public static HashMap<String, Kit> getKits() {
	      return kits;
	   }
	}