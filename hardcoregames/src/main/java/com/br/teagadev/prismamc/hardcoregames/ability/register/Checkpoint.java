package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Checkpoint extends Kit {
   private HashMap<UUID, Location> checkpoints = new HashMap();

   public Checkpoint() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.NETHER_FENCE).name(this.getItemColor() + "Kit " + this.getName()).build(), (new ItemBuilder()).material(Material.FLOWER_POT_ITEM).name(this.getItemColor() + "Kit " + this.getName()).build()});
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void colocar(BlockPlaceEvent e) {
      Player p = e.getPlayer();
      Block b = e.getBlock();
      if (b.getType() == Material.NETHER_FENCE && this.containsHability(p)) {
         if (this.hasCooldown(p)) {
            this.sendMessageCooldown(p);
            e.setBuild(false);
            e.setCancelled(true);
            return;
         }

         p.setItemInHand(e.getItemInHand());
         p.updateInventory();
         if (this.checkpoints.containsKey(p.getUniqueId())) {
            Location loc = (Location)this.checkpoints.get(p.getUniqueId());
            loc.getBlock().setType(Material.AIR);
         }

         this.checkpoints.put(p.getUniqueId(), e.getBlock().getLocation());
         p.sendMessage("§aPosição setada.");
      }

   }

   @EventHandler
   public void teleportCheckpoint(PlayerInteractEvent e) {
      Player p = e.getPlayer();
      ItemStack i = p.getItemInHand();
      if (i.getType() == Material.FLOWER_POT_ITEM) {
         e.setCancelled(true);
         if (p.getLocation().getY() > 128.0D) {
            return;
         }

         if (!this.containsHability(p)) {
            return;
         }

         if (!this.checkpoints.containsKey(p.getUniqueId())) {
            p.sendMessage("§cVocê não ainda não salvou nenhuma localização.");
         } else {
            if (this.hasCooldown(p)) {
               this.sendMessageCooldown(p);
               return;
            }

            p.teleport((Location)this.checkpoints.get(p.getUniqueId()));
            p.sendMessage("§aTeleportado");
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
            this.addCooldown(p, (long)this.getCooldownSeconds());
         }
      }

   }

   @EventHandler
   public void blockDamage(BlockDamageEvent e) {
      if (this.checkpoints.containsValue(e.getBlock().getLocation()) && e.getBlock().getType() == Material.NETHER_FENCE) {
         e.setCancelled(true);
         Iterator var2 = this.checkpoints.keySet().iterator();

         while(var2.hasNext()) {
            UUID p = (UUID)var2.next();
            if (this.checkpoints.get(p) == e.getBlock().getLocation()) {
               if (Bukkit.getPlayer(p) != null) {
                  Bukkit.getPlayer(p).sendMessage("§cO seu Checkpoint foi destruído.");
               }

               this.checkpoints.remove(p);
            }
         }

         e.getBlock().setType(Material.AIR);
         e.getPlayer().sendMessage("§cVocê destruiu um checkpoint.");
         e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
      }

   }

   protected void clean(Player player) {
   }
}