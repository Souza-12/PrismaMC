package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.vanish.VanishAPI;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Endermage extends Kit {
   public Endermage() {
      this.initialize(this.getClass().getSimpleName());
      this.setUseInvincibility(true);
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.NETHER_BRICK_ITEM).name(this.getItemColor() + "Kit " + this.getName()).build()});
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (event.getAction().name().contains("RIGHT")) {
         if (event.getPlayer().getItemInHand().getType().equals(Material.NETHER_BRICK_ITEM) && this.containsHability(event.getPlayer()) && event.getClickedBlock().getType() != Material.ENDER_PORTAL_FRAME) {
            final Player player = event.getPlayer();
            if (this.hasCooldown(player)) {
               this.sendMessageCooldown(player);
               return;
            }

            final Block b = event.getClickedBlock();
            final BlockState bs = b.getState();
            if (b.getType().equals(Material.BEDROCK)) {
               player.sendMessage("§cVocê não pode puxar neste bloco.");
               return;
            }

            if (player.getLocation().getBlockY() > 115) {
               player.sendMessage("§cVocê não pode puxar nesta altura.");
               return;
            }

            b.setType(Material.ENDER_PORTAL_FRAME);
            this.addCooldown(player, (long)this.getCooldownSeconds());
            (new BukkitRunnable() {
               int segundos = 5;
               final Location portal2 = b.getLocation().clone().add(0.5D, 1.5D, 0.5D);

               public void run() {
                  ArrayList<Player> players = Endermage.this.getNearbyPlayers(player, this.portal2);
                  if (!player.isOnline() || Endermage.this.calculateDistance(player.getLocation(), this.portal2) > 50) {
                     if (!player.isOnline()) {
                        this.cancel();
                     }

                     Endermage.this.resetBlock(b, bs);
                  }

                  if (!players.isEmpty()) {
                     Iterator var2 = players.iterator();

                     while(var2.hasNext()) {
                        Player pl = (Player)var2.next();
                        pl.setFallDistance(0.0F);
                        pl.setNoDamageTicks(110);
                        pl.teleport(this.portal2);
                        player.sendMessage("§aVocê puxou: " + pl.getName());
                        pl.sendMessage("§aVocê foi puxado pelo " + player.getName());
                        pl.sendMessage("§aVocê esta invencível por 5 segundos");
                     }

                     player.setFallDistance(0.0F);
                     player.setNoDamageTicks(110);
                     player.teleport(this.portal2);
                     player.sendMessage("§cVocê esta invencível por 5 segundos");
                     Endermage.this.resetBlock(b, bs);
                     this.cancel();
                  }

                  if (this.segundos == 0) {
                     Endermage.this.resetBlock(b, bs);
                     this.cancel();
                  }

                  --this.segundos;
               }
            }).runTaskTimer(HardcoreGamesMain.getInstance(), 20L, 20L);
         }

      }
   }

   private void resetBlock(Block b, BlockState bs) {
      HardcoreGamesMain.runLater(() -> {
         b.setTypeIdAndData(bs.getTypeId(), bs.getRawData(), false);
      }, 100L);
   }

   private ArrayList<Player> getNearbyPlayers(Player p2, Location portal) {
      ArrayList<Player> players = new ArrayList();
      Iterator var4 = Bukkit.getOnlinePlayers().iterator();

      while(var4.hasNext()) {
         Player onlines = (Player)var4.next();
         if (onlines != p2 && this.isEnderable(portal, onlines.getLocation()) && onlines.getLocation().getBlockY() <= 128 && !VanishAPI.isInvisible(onlines)) {
            Gamer gamer = GamerManager.getGamer(onlines.getUniqueId());
            if (!gamer.containsKit("Endermage") && gamer.isPlaying()) {
               players.add(onlines);
            }
         }
      }

      return players;
   }

   private boolean isEnderable(Location portal, Location player) {
      return Math.abs(portal.getX() - player.getX()) < 2.0D && Math.abs(portal.getZ() - player.getZ()) < 2.0D && Math.abs(portal.getY() - player.getY()) > 2.0D;
   }

   public int calculateDistance(Location a, Location b) {
      int distance = 0;
      int x1 = a.getBlockX();
      int x2 = b.getBlockX();
      int z1 = a.getBlockZ();
      int z2 = b.getBlockZ();
      int i;
      if (x1 != x2) {
         if (x1 < x2) {
            for(i = x1; i <= x2 - 1; ++i) {
               ++distance;
            }
         } else {
            for(i = x2; i <= x1 - 1; ++i) {
               ++distance;
            }
         }
      }

      if (z1 != z2) {
         if (z1 < z2) {
            for(i = z1; i <= z2 - 1; ++i) {
               ++distance;
            }
         } else {
            for(i = z2; i <= z1 - 1; ++i) {
               ++distance;
            }
         }
      }

      return distance;
   }

   protected void clean(Player player) {
   }
}