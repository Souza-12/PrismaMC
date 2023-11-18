package com.br.teagadev.prismamc.hardcoregames.manager.kit;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesOptions;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerRegisterKitEvent;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class KitManager {
   private static final HashMap<String, Kit> kits = new HashMap();
   private static final ArrayList<String> kitsDesativados = new ArrayList();
   private static final String[] combinationsBlockeds = new String[]{"Stomper-Phantom", "Stomper-Tower", "Stomper-Jumper", "Stomper-Launcher", "Stomper-Grappler", "Stomper-Flash", "Stomper-Kangaroo", "Stomper-Ninja", "Viking-Urgal", "Demoman-Tank", "Kangaroo-Grappler", "Phantom-Kangaroo", "Hulk-Phantom", "Fisherman-Magma", "Fisherman-Fireman", "Fisherman-Demoman", "Phantom-Switcher", "Gladiator-Ninja", "Gladiator-Phantom", "Gladiator-Kangaroo", "Blink-Stomper", ""};

   public static List<Kit> getAllKits() {
      return new ArrayList(kits.values());
   }

   public static List<Kit> getAllKitsAvailables() {
      List<Kit> allKits = new ArrayList();
      Iterator var1 = kits.values().iterator();

      while(var1.hasNext()) {
         Kit kit = (Kit)var1.next();
         if (!getKitsDesativados().contains(kit.getName().toLowerCase())) {
            allKits.add(kit);
         }
      }

      return allKits;
   }

   public static void disableKit(String name) {
      if (!getKitsDesativados().contains(name.toLowerCase())) {
         getKitsDesativados().add(name.toLowerCase());
      }

   }

   public static void disableKits(String... kits) {
      String[] var1 = kits;
      int var2 = kits.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String kit = var1[var3];
         disableKit(kit);
      }

   }

   public static void enableKit(String name) {
      getKitsDesativados().remove(name.toLowerCase());
   }

   public static void enableKits(String... kits) {
      String[] var1 = kits;
      int var2 = kits.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String kit = var1[var3];
         enableKit(kit);
      }

   }

   public static List<Kit> getPlayerKits(Player player) {
      List<Kit> playerKits = new ArrayList();
      Iterator var2 = getAllKits().iterator();

      while(true) {
         while(var2.hasNext()) {
            Kit kit = (Kit)var2.next();
            if (!player.hasPermission("hg.kit." + kit.getName().toLowerCase()) && !player.hasPermission("hg.kit.all")) {
               if (hasPermissionKit(player, kit.getName(), false)) {
                  playerKits.add(kit);
               }
            } else {
               playerKits.add(kit);
            }
         }

         return playerKits;
      }
   }

   public static Kit getKitInfo(String nome) {
      return nome.equalsIgnoreCase("Nenhum") ? null : (Kit)kits.get(nome.toLowerCase());
   }

   public static boolean hasPermissionKit(Player player, String kit, boolean msg) {
      return hasPermissionKit(player, kit, msg, true);
   }

   public static boolean hasPermissionKit(Player player, String kit, boolean msg, boolean checkAllFree) {
      if (checkAllFree && HardcoreGamesOptions.KITS_FREE) {
         return true;
      } else if (player.hasPermission("hg.kit.all")) {
         return true;
      } else if (player.hasPermission("hg.kit." + kit.toLowerCase())) {
         return true;
      } else {
         if (msg) {
            player.sendMessage("§cVocê não possui este kit!");
         }

         return false;
      }
   }

   public static void giveBussola(Player player) {
      if (!player.getInventory().contains(Material.COMPASS)) {
         player.getInventory().addItem(new ItemStack[]{(new ItemBuilder()).type(Material.COMPASS).name("§Bússola").build()});
         player.updateInventory();
      }

   }

   public static void give(Player player, String name, boolean firstKit) {
      if (!name.equalsIgnoreCase("Nenhum")) {
         Kit playerKit = getKitInfo(name);
         if (playerKit != null) {
            playerKit.cleanPlayer(player);
            if (playerKit.getName().equalsIgnoreCase("Surprise")) {
               playerKit = getRandomAvailableKit();
               if (firstKit) {
                  GamerManager.getGamer(player.getUniqueId()).setKit1(playerKit.getName());
               } else {
                  GamerManager.getGamer(player.getUniqueId()).setKit2(playerKit.getName());
               }
            }

            if (playerKit.getItens() != null) {
               ItemStack[] var4 = playerKit.getItens();
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  ItemStack item = var4[var6];
                  player.getInventory().addItem(new ItemStack[]{item});
               }
            }

            playerKit.addUsing();
            if (!playerKit.isListenerRegistred()) {
               playerKit.registerListener();
            }

            if (playerKit.isCallEventRegisterPlayer()) {
               Bukkit.getServer().getPluginManager().callEvent(new PlayerRegisterKitEvent(player, playerKit.getName()));
            }
         }

      }
   }

   public static void removeKits(Player player, boolean setInGamer) {
      Gamer gamer = GamerManager.getGamer(player.getUniqueId());
      Kit kit1 = getKitInfo(gamer.getKit1());
      Kit kit2 = getKitInfo(gamer.getKit2());
      if (kit1 != null) {
         kit1.unregisterPlayer(player);
      }

      if (kit2 != null) {
         kit2.unregisterPlayer(player);
      }

      if (setInGamer) {
         gamer.setKit1("Nenhum");
         gamer.setKit2("Nenhum");
      }

   }

   public static void removeIfContainsKit(Player player, String kitName) {
      Gamer gamer = GamerManager.getGamer(player.getUniqueId());
      Kit kit1 = getKitInfo(gamer.getKit1());
      Kit kit2 = getKitInfo(gamer.getKit2());
      boolean removed = false;
      if (kit1 != null && kit1.getName().equalsIgnoreCase(kitName)) {
         kit1.unregisterPlayer(player);
         gamer.setKit1("Nenhum");
         removed = true;
      }

      if (kit2 != null && kit2.getName().equalsIgnoreCase(kitName)) {
         kit2.unregisterPlayer(player);
         gamer.setKit2("Nenhum");
         removed = true;
      }

      if (removed) {
         player.sendMessage("§cO seu kit %kit% foi desativado na partida!".replace("%kit%", kitName));
      }

   }

   public static void giveItensKit(Player player, String kitName) {
      Kit kit = getKitInfo(kitName);
      if (kit != null && kit.getItens() != null) {
         ItemStack[] var3 = kit.getItens();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ItemStack item = var3[var5];
            player.getInventory().addItem(new ItemStack[]{item});
         }
      }

      player.updateInventory();
   }

   public static void handleKitSelect(Player player, boolean primary, String kitToSet) {
      Gamer gamer = GamerManager.getGamer(player.getUniqueId());
      Kit olderKit = getKitInfo(primary ? gamer.getKit1() : gamer.getKit2());
      Kit playerKit = getKitInfo(kitToSet);
      int var8;
      if (olderKit != null) {
         if (olderKit.getName().equalsIgnoreCase(kitToSet)) {
            return;
         }

         olderKit.unregisterPlayer(player);
         if (olderKit.getItens() != null) {
            ItemStack[] var6 = olderKit.getItens();
            int var7 = var6.length;

            for(var8 = 0; var8 < var7; ++var8) {
               ItemStack item = var6[var8];
               if (player.getInventory().contains(item)) {
                  player.getInventory().remove(item);
               }
            }
         }
      }

      if (playerKit != null) {
         if (kitToSet.equalsIgnoreCase("Surprise")) {
            playerKit = getRandomAvailableKit();
            kitToSet = playerKit.getName();
            player.sendMessage("§eO Surprise escolheu o kit %kit%".replace("%kit%", playerKit.getName()));
         }

         playerKit.registerPlayer();
         PlayerInventory playerInventory = player.getInventory();
         if (playerKit.getItens() != null) {
            ItemStack[] var12 = playerKit.getItens();
            var8 = var12.length;

            for(int var13 = 0; var13 < var8; ++var13) {
               ItemStack item = var12[var13];
               playerInventory.addItem(new ItemStack[]{item});
            }
         }

         if (!playerInventory.contains(Material.COMPASS)) {
            playerInventory.addItem(new ItemStack[]{(new ItemBuilder()).type(Material.COMPASS).name("§6Bússola").build()});
         }
      }

      if (primary) {
         gamer.setKit1(kitToSet);
         HardcoreGamesScoreboard.getScoreBoardCommon().updateKit1(player, kitToSet);
      } else {
         gamer.setKit2(kitToSet);
         HardcoreGamesScoreboard.getScoreBoardCommon().updateKit2(player, kitToSet);
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

   private static Kit getRandomAvailableKit() {
      List<Kit> kits = getAllKitsAvailables();
      Kit kit = (Kit)kits.get(CommonsConst.RANDOM.nextInt(kits.size()));
      return kit != null && !kit.getName().equals("Surprise") ? kit : getRandomAvailableKit();
   }

   public static HashMap<String, Kit> getKits() {
      return kits;
   }

   public static ArrayList<String> getKitsDesativados() {
      return kitsDesativados;
   }
}