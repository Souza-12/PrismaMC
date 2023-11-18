package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Thor extends Kit {
   public Thor() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.WOOD_AXE).name(this.getItemColor() + "Kit " + this.getName()).build()});
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (event.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE) && event.getAction() == Action.RIGHT_CLICK_BLOCK && this.containsHability(event.getPlayer())) {
         if (this.hasCooldown(event.getPlayer())) {
            this.sendMessageCooldown(event.getPlayer());
            return;
         }

         this.addCooldown(event.getPlayer(), (long)this.getCooldownSeconds());
         Location loc = new Location(event.getPlayer().getWorld(), (double)event.getClickedBlock().getX(), (double)event.getPlayer().getWorld().getHighestBlockYAt(event.getClickedBlock().getX(), event.getClickedBlock().getZ()), (double)event.getClickedBlock().getZ());
         loc = loc.subtract(0.0D, 1.0D, 0.0D);
         if (loc.getBlock().getType() == Material.AIR) {
            loc = loc.subtract(0.0D, 1.0D, 0.0D);
         }

         LightningStrike strike = loc.getWorld().strikeLightning(loc);

         for(Iterator var4 = strike.getNearbyEntities(4.0D, 4.0D, 4.0D).iterator(); var4.hasNext(); event.getPlayer().setFireTicks(0)) {
            Entity nearby = (Entity)var4.next();
            if (nearby instanceof Player) {
               nearby.setFireTicks(100);
            }
         }

         strike = null;
         loc.getBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
         loc = null;
      }

   }

   @EventHandler
   public void damage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (event.getCause() == DamageCause.LIGHTNING && this.containsHability(player)) {
            player.setFireTicks(0);
            event.setCancelled(true);
         }
      }

   }

   protected void clean(Player player) {
   }
}