package com.br.teagadev.prismamc.hardcoregames.ability.utility;

import com.br.teagadev.prismamc.hardcoregames.ability.register.Gladiator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GladiatorFight {
   private Player[] players;
   private Location[] locations;
   private Block mainBlock;
   private int seconds;
   private int id;
   private boolean ended;

   public GladiatorFight(Block mainBlock, int id, Player... players) {
      this.setSeconds(201);
      this.setEnded(false);
      this.setId(id);
      this.setPlayers(players);
      this.setMainBlock(mainBlock);
      this.locations = new Location[2];
      this.players[0] = players[0];
      this.players[1] = players[1];
      this.locations[0] = players[0].getLocation();
      this.locations[1] = players[1].getLocation();
   }

   public void onSecond() {
      if (!this.isEnded()) {
         Player[] var1 = this.getPlayers();
         int var2 = var1.length;

         int var3;
         Player player;
         for(var3 = 0; var3 < var2; ++var3) {
            player = var1[var3];
            if (player.isOnline()) {
               if (player.getLocation().getBlockY() > this.mainBlock.getLocation().getBlockY() + 9) {
                  this.cancelGlad();
               } else if (player.getLocation().getBlockY() < this.mainBlock.getLocation().getBlockY() - 2) {
                  this.cancelGlad();
               }
            }
         }

         if (this.getSeconds() == 80) {
            var1 = this.getPlayers();
            var2 = var1.length;

            for(var3 = 0; var3 < var2; ++var3) {
               player = var1[var3];
               if (player.isOnline()) {
                  player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 9999999, 3));
               }
            }
         } else if (this.seconds == 0) {
            this.cancelGlad();
            return;
         }

         --this.seconds;
      }
   }

   public void teleportBack() {
      Player[] var1 = this.getPlayers();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Player player = var1[var3];
         if (player.isOnline()) {
            player.setFallDistance(-5.0F);
            player.setNoDamageTicks(40);
            player.teleport(this.getBackForPlayer(player));
         }
      }

   }

   public Location getBackForPlayer(Player player) {
      return player.getUniqueId().equals(this.players[0].getUniqueId()) ? this.locations[0] : this.locations[1];
   }

   public void cancelGlad() {
      this.ended = true;
      Player[] var1 = this.getPlayers();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Player player = var1[var3];
         if (player.isOnline()) {
            if (player.hasPotionEffect(PotionEffectType.WITHER)) {
               player.removePotionEffect(PotionEffectType.WITHER);
            }

            Gladiator.gladArena.remove(player.getUniqueId());
         }
      }

      this.teleportBack();
      Gladiator.removerBlocos(this.mainBlock.getLocation());
      Gladiator.gladiatorFights.remove("arena" + this.id);
   }

   public Player[] getPlayers() {
      return this.players;
   }

   public Location[] getLocations() {
      return this.locations;
   }

   public Block getMainBlock() {
      return this.mainBlock;
   }

   public int getSeconds() {
      return this.seconds;
   }

   public int getId() {
      return this.id;
   }

   public boolean isEnded() {
      return this.ended;
   }

   public void setPlayers(Player[] players) {
      this.players = players;
   }

   public void setLocations(Location[] locations) {
      this.locations = locations;
   }

   public void setMainBlock(Block mainBlock) {
      this.mainBlock = mainBlock;
   }

   public void setSeconds(int seconds) {
      this.seconds = seconds;
   }

   public void setId(int id) {
      this.id = id;
   }

   public void setEnded(boolean ended) {
      this.ended = ended;
   }
}