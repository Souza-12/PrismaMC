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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;

public class NormalType extends GameType {
   public void initialize() {
      this.registerListeners();
      HardcoreGamesMain.console("GameType (NormalType) has been loaded.");
   }

   public void start() {
      PluginManager pluginManager = Bukkit.getServer().getPluginManager();
      pluginManager.callEvent(new GameStageChangeEvent(GameStages.WAITING, GameStages.INVINCIBILITY));
      HardcoreGamesMain.getGameManager().setStage(GameStages.INVINCIBILITY);
      HardcoreGamesMain.getTimerManager().updateTime(120);
      pluginManager.registerEvents(new InvincibilityListener(), HardcoreGamesMain.getInstance());
      List<Kit> kits = KitManager.getAllKitsAvailables();
      ItemStack compass = (new ItemBuilder()).type(Material.COMPASS).name("§6Bússola").build();
      Iterator var4 = Bukkit.getOnlinePlayers().iterator();

      while(true) {
         Player player;
         Gamer gamer;
         do {
            if (!var4.hasNext()) {
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

            player = (Player)var4.next();
            gamer = GamerManager.getGamer(player.getUniqueId());
         } while(gamer == null);

         player.getPlayer().setItemOnCursor(new ItemStack(0));
         if (player.getOpenInventory() instanceof PlayerInventory) {
            player.closeInventory();
         }

         PlayerInventory playerInventory = player.getInventory();
         playerInventory.clear();
         playerInventory.setArmorContents((ItemStack[])null);
         Iterator var8 = player.getActivePotionEffects().iterator();

         while(var8.hasNext()) {
            PotionEffect pe = (PotionEffect)var8.next();
            player.removePotionEffect(pe.getType());
         }

         if (player.getGameMode() != GameMode.SURVIVAL) {
            player.setGameMode(GameMode.SURVIVAL);
         }

         player.setAllowFlight(false);
         player.setFlying(false);
         KitManager.give(player, gamer.getKit1(), true);
         KitManager.give(player, gamer.getKit2(), false);
         playerInventory.addItem(new ItemStack[]{compass});
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
                  winner.sendMessage("§a+%quantia% XP".replace("%quantia%", "100"));
                  winner.sendMessage("§a+%quantia% coins".replace("%quantia%", "200"));
                  bukkitPlayer.addXP(100);
                  bukkitPlayer.add(DataType.COINS, 200);
                  bukkitPlayer.add(DataType.HG_WINS);
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