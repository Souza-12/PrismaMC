package com.br.teagadev.prismamc.hardcoregames.base;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.BukkitSettings;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.vanish.VanishAPI;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesOptions;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameStageChangeEvent;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerSpectateEvent;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerSpectateEvent.SpectateAction;
import com.br.teagadev.prismamc.hardcoregames.listeners.EndListener;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.kit.KitManager;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.teagadev.prismamc.hardcoregames.manager.timer.TimerType;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class GameType {
   public abstract void initialize();

   public abstract void checkWin();

   public abstract void choiceWinner();

   public abstract void registerListeners();

   public abstract void start();

   public void setGamer(Player player) {
      Gamer gamer = GamerManager.getGamer(player.getUniqueId());
      gamer.setPlaying(true);
      if (!this.isPreGame()) {
         player.setNoDamageTicks(100);
         player.setExp(0.0F);
         player.setLevel(0);
         player.setFireTicks(0);
         player.setFoodLevel(20);
         PlayerInventory playerInventory = player.getInventory();
         playerInventory.clear();
         playerInventory.setArmorContents((ItemStack[])null);
         player.setAllowFlight(false);
         player.setFlying(false);
         if (player.getGameMode() != GameMode.SURVIVAL) {
            player.setGameMode(GameMode.SURVIVAL);
         }

         KitManager.give(player, gamer.getKit1(), true);
         KitManager.give(player, gamer.getKit2(), false);
         playerInventory.addItem(new ItemStack[]{(new ItemBuilder()).type(Material.COMPASS).name("§6Bússola").build()});
      }

      HardcoreGamesScoreboard.createScoreboard(gamer);
      Bukkit.getServer().getPluginManager().callEvent(new PlayerSpectateEvent(player, SpectateAction.SAIU));
      HardcoreGamesScoreboard.getScoreBoardCommon().updatePlayerMode(gamer);
      gamer = null;
   }

   public void setEspectador(Player player) {
      this.setEspectador(player, false);
   }

   public void setEspectador(Player player, boolean adminChange) {
      Gamer gamer = GamerManager.getGamer(player.getUniqueId());
      gamer.setEliminado(true);
      gamer.setPlaying(false);
      Kit playerKit1 = KitManager.getKitInfo(gamer.getKit1());
      Kit playerKit2 = KitManager.getKitInfo(gamer.getKit2());
      if (playerKit1 != null) {
         playerKit1.unregisterPlayer(player);
         playerKit1 = null;
      }

      if (playerKit2 != null) {
         playerKit2.unregisterPlayer(player);
         playerKit2 = null;
      }

      if (player.getGameMode() != GameMode.ADVENTURE) {
         player.setGameMode(GameMode.ADVENTURE);
      }

      player.setHealth(20.0D);
      player.setFireTicks(0);
      player.setFoodLevel(20);
      player.setAllowFlight(true);
      player.setFlying(true);
      player.setExp(0.0F);
      player.setLevel(0);
      if (!adminChange) {
         Iterator var6 = Bukkit.getOnlinePlayers().iterator();

         while(var6.hasNext()) {
            Player ons = (Player)var6.next();
            ons.hidePlayer(player);
         }

         if (BukkitMain.getBukkitPlayer(player.getUniqueId()).getGroup().getLevel() >= Groups.YOUTUBER_PLUS.getLevel()) {
            VanishAPI.changeAdmin(player, false);
         }

         player.getInventory().addItem(new ItemStack[]{(new ItemBuilder()).type(Material.COMPASS).name("§aJogadores Vivos").build()});
      }

      HardcoreGamesScoreboard.createScoreboard(gamer);
      Bukkit.getServer().getPluginManager().callEvent(new PlayerSpectateEvent(player, SpectateAction.ENTROU));
      HardcoreGamesScoreboard.getScoreBoardCommon().updatePlayerMode(gamer);
      gamer = null;
      this.checkWin();
   }

   protected boolean isLoading() {
      return HardcoreGamesMain.getGameManager().isLoading();
   }

   protected boolean isPreGame() {
      return HardcoreGamesMain.getGameManager().isPreGame();
   }

   protected boolean isInvencibilidade() {
      return HardcoreGamesMain.getGameManager().isInvencibilidade();
   }

   protected boolean isGaming() {
      return HardcoreGamesMain.getGameManager().isGaming();
   }

   protected boolean isEnd() {
      return HardcoreGamesMain.getGameManager().isEnd();
   }

   protected Random getRandom() {
      return CommonsConst.RANDOM;
   }

   protected UUID getMVP() {
      return this.getMVP(false);
   }

   protected UUID getMVP(boolean allGamers) {
      UUID ganhador = null;
      int kills = -1;
      Iterator var4;
      int killsPlayer;
      if (allGamers) {
         var4 = GamerManager.getGamers().iterator();

         while(var4.hasNext()) {
            Gamer gamer = (Gamer)var4.next();
            killsPlayer = gamer.getKills();
            if (killsPlayer >= kills) {
               ganhador = gamer.getUniqueId();
               kills = killsPlayer;
            }
         }
      } else {
         var4 = GamerManager.getAliveGamers().iterator();

         while(var4.hasNext()) {
            Player player = (Player)var4.next();
            killsPlayer = GamerManager.getGamer(player.getUniqueId()).getKills();
            if (killsPlayer >= kills) {
               ganhador = player.getUniqueId();
               kills = killsPlayer;
            }
         }
      }

      return ganhador;
   }

   protected void callEnd() {
      HardcoreGamesMain.getGameManager().setStage(GameStages.END);
      HardcoreGamesMain.getTimerManager().setTimerType(TimerType.COUNTDOWN);
      HardcoreGamesMain.getInstance().getServer().getPluginManager().callEvent(new GameStageChangeEvent(HardcoreGamesMain.getGameManager().getStage(), GameStages.END));
      HardcoreGamesMain.getTimerManager().updateTime(15);
      BukkitSettings.PVP_OPTION = false;
      BukkitSettings.DANO_OPTION = false;
      HardcoreGamesOptions.BREAK_OPTION = false;
      HardcoreGamesOptions.PLACE_OPTION = false;
      HardcoreGamesOptions.DROP_OPTION = false;
      HardcoreGamesMain.getInstance().getServer().getPluginManager().registerEvents(new EndListener(), BukkitMain.getInstance());
      HardcoreGamesMain.getTimerManager().updateTime(15);
   }
}