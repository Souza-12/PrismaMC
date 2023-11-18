package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class Switcher extends Kit {
   public Switcher() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.SNOW_BALL).name(this.getItemColor() + "Kit " + this.getName()).amount(16).build()});
   }

   @EventHandler
   public void onProjectileLaunch(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Player player = event.getPlayer();
         if (this.containsHability(player) && this.checkItem(player.getItemInHand(), this.getItemColor() + "Kit " + this.getName())) {
            event.setCancelled(true);
            player.updateInventory();
            if (this.hasCooldown(player)) {
               return;
            }

            Snowball ball = (Snowball)player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.SNOWBALL);
            ball.setShooter(player);
            ball.setVelocity(player.getLocation().getDirection().multiply(1.5D));
            ball.setMetadata("switch", new FixedMetadataValue(HardcoreGamesMain.getInstance(), player));
            ball.setVelocity(player.getVelocity().multiply(1.5D));
            this.addCooldown(player, 5L);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager().hasMetadata("switch")) {
         Player player = (Player)((MetadataValue)event.getDamager().getMetadata("switch").get(0)).value();
         if (player == null) {
            return;
         }

         Location loc = event.getEntity().getLocation().clone();
         event.getEntity().teleport(player.getLocation().clone());
         player.teleport(loc);
      }

   }

   protected void clean(Player player) {
   }
}