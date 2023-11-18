package com.br.teagadev.prismamc.hardcoregames.game.types;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.base.GameType;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameStageChangeEvent;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameStartedEvent;
import com.br.teagadev.prismamc.hardcoregames.listeners.EndListener;
import com.br.teagadev.prismamc.hardcoregames.listeners.InvincibilityListener;
import com.br.teagadev.prismamc.hardcoregames.listeners.SpectatorListener;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.kit.KitManager;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class AutomaticEventType extends GameType {
   public void initialize() {
      this.registerListeners();
      KitManager.disableKits(new String[]{"Achilles", "Barbarian", "Boxer", "Checkpoint", "Demoman", "Fisherman", "Launcher", "Monk", "Phantom", "Stomper", "Tank", "Thor", "Urgal", "Viking", "Madman", "Digger", "HotPotato"});
      HardcoreGamesMain.console("GameType (AutomaticEventType) has been loaded.");
      (new BukkitRunnable() {
         int seconds = 400;

         public void run() {
            --this.seconds;
            if (!AutomaticEventType.this.isPreGame()) {
               this.cancel();
            } else {
               if (this.seconds == 0) {
                  this.cancel();
                  if (GamerManager.getAliveGamers().size() < 20) {
                     HardcoreGamesMain.console("AutomaticEventType fechando por conta do Timer (Poucos jogadores).");
                     AutomaticEventType.this.callEnd();
                  }
               }

            }
         }
      }).runTaskTimer(HardcoreGamesMain.getInstance(), 20L, 20L);
   }

   public void start() {
      HardcoreGamesMain.getTimerManager().updateTime(60);
      HardcoreGamesMain.getGameManager().setStage(GameStages.INVINCIBILITY);
      PluginManager pluginManager = Bukkit.getServer().getPluginManager();
      pluginManager.callEvent(new GameStageChangeEvent(GameStages.WAITING, GameStages.INVINCIBILITY));
      pluginManager.registerEvents(new InvincibilityListener(), HardcoreGamesMain.getInstance());
      List<Kit> kits = KitManager.getAllKitsAvailables();
      ItemBuilder itemBuilder = new ItemBuilder();
      ItemStack capacete = itemBuilder.type(Material.IRON_HELMET).name("§fCapacete de Ferro").build();
      ItemStack peitoral = itemBuilder.type(Material.IRON_CHESTPLATE).name("§fPeitoral de Ferro").build();
      ItemStack calça = itemBuilder.type(Material.IRON_LEGGINGS).name("§fCalça de Ferro").build();
      ItemStack bota = itemBuilder.type(Material.IRON_BOOTS).name("§fBota de Ferro").build();
      Iterator var8 = Bukkit.getOnlinePlayers().iterator();

      while(true) {
         Player player;
         Gamer gamer;
         do {
            if (!var8.hasNext()) {
               Bukkit.broadcastMessage("§aA partida foi iniciada!");
               World world = (World)Bukkit.getWorlds().get(0);
               world.setTime(0L);
               world.setStorm(false);
               world.setThundering(false);
               world.playSound(world.getSpawnLocation(), Sound.AMBIENCE_THUNDER, 4.0F, 4.0F);
               pluginManager.registerEvents(new SpectatorListener(), HardcoreGamesMain.getInstance());
               pluginManager.callEvent(new GameStartedEvent());
               kits.clear();
               return;
            }

            player = (Player)var8.next();
            gamer = GamerManager.getGamer(player.getUniqueId());
         } while(gamer == null);

         player.getPlayer().setItemOnCursor(new ItemStack(0));
         if (player.getOpenInventory() instanceof PlayerInventory) {
            player.closeInventory();
         }

         PlayerInventory playerInventory = player.getInventory();
         playerInventory.clear();
         playerInventory.setArmorContents((ItemStack[])null);
         playerInventory.setHelmet(capacete);
         playerInventory.setChestplate(peitoral);
         playerInventory.setLeggings(calça);
         playerInventory.setBoots(bota);
         Iterator var12 = player.getActivePotionEffects().iterator();

         while(var12.hasNext()) {
            PotionEffect pe = (PotionEffect)var12.next();
            player.removePotionEffect(pe.getType());
         }

         if (player.getGameMode() != GameMode.SURVIVAL) {
            player.setGameMode(GameMode.SURVIVAL);
         }

         player.setAllowFlight(false);
         player.setFlying(false);
         playerInventory.setItem(0, itemBuilder.type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL).build());
         playerInventory.setItem(3, itemBuilder.type(Material.LAVA_BUCKET).build());
         playerInventory.setItem(4, itemBuilder.type(Material.WOOD).amount(64).build());
         playerInventory.setItem(7, itemBuilder.type(Material.WATER_BUCKET).build());
         playerInventory.setItem(8, itemBuilder.type(Material.COMPASS).name("§6Bússola").build());
         KitManager.give(player, gamer.getKit1(), true);
         KitManager.give(player, gamer.getKit2(), false);
         playerInventory.addItem(new ItemStack[]{itemBuilder.type(Material.LAVA_BUCKET).build()});
         playerInventory.addItem(new ItemStack[]{itemBuilder.type(Material.LAVA_BUCKET).build()});
         playerInventory.addItem(new ItemStack[]{itemBuilder.type(Material.DIAMOND_SWORD).build()});
         playerInventory.setItem(18, capacete);
         playerInventory.setItem(19, peitoral);
         playerInventory.setItem(20, calça);
         playerInventory.setItem(21, bota);
         playerInventory.setItem(22, itemBuilder.type(Material.BOWL).amount(64).build());
         playerInventory.setItem(23, itemBuilder.type(Material.INK_SACK).durability(3).amount(64).build());
         playerInventory.setItem(24, itemBuilder.type(Material.INK_SACK).durability(3).amount(64).build());
         playerInventory.setItem(25, itemBuilder.type(Material.LOG).amount(32).build());
         playerInventory.setItem(26, itemBuilder.type(Material.STONE_AXE).build());
         playerInventory.setItem(9, capacete);
         playerInventory.setItem(10, peitoral);
         playerInventory.setItem(11, calça);
         playerInventory.setItem(12, bota);
         playerInventory.setItem(13, itemBuilder.type(Material.BOWL).amount(64).build());
         playerInventory.setItem(14, itemBuilder.type(Material.INK_SACK).durability(3).amount(64).build());
         playerInventory.setItem(15, itemBuilder.type(Material.INK_SACK).durability(3).amount(64).build());
         playerInventory.setItem(16, itemBuilder.type(Material.INK_SACK).durability(3).amount(64).build());
         playerInventory.setItem(17, itemBuilder.type(Material.STONE_PICKAXE).build());
         HardcoreGamesScoreboard.createScoreboard(gamer);
      }
   }

   public void checkWin() {
      if (!this.isEnd() && !this.isPreGame()) {
         if (Bukkit.getOnlinePlayers().size() == 0) {
            this.callEnd();
         } else {
            List<Player> aliveGamers = GamerManager.getAliveGamers();
            if (aliveGamers.size() == 0) {
               this.callEnd();
            } else if (aliveGamers.size() == 1) {
               Player winner = (Player)aliveGamers.get(0);
               if (winner == null) {
                  this.callEnd();
               } else {
                  EndListener.winner = winner;
                  this.callEnd();
                  BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(winner.getUniqueId());
                  boolean xp = true;
                  boolean coins = true;
                  winner.sendMessage("§a+%quantia% XP".replace("%quantia%", "250"));
                  winner.sendMessage("§a+%quantia% coins".replace("%quantia%", "500"));
                  bukkitPlayer.addXP(250);
                  bukkitPlayer.add(DataType.COINS, 500);
                  bukkitPlayer.add(DataType.HG_EVENT_WINS);
                  bukkitPlayer.getDataHandler().saveCategorys(new DataCategory[]{DataCategory.ACCOUNT, DataCategory.HARDCORE_GAMES});
                  aliveGamers.clear();
               }
            }
         }
      }
   }

   public void registerListeners() {
   }

   public void choiceWinner() {
      UUID ganhador = this.getMVP();
      Iterator var2 = GamerManager.getAliveGamers().iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         if (player.getUniqueId() != ganhador) {
            this.setEspectador(player, true);
         }
      }

      HardcoreGamesMain.runLater(this::checkWin, 20L);
   }
}