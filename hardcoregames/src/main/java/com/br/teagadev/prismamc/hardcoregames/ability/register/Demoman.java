package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Demoman extends Kit {
   public Demoman() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.GRAVEL).amount(8).name(this.getItemColor() + "Kit " + this.getName()).build(), (new ItemBuilder()).material(Material.STONE_PLATE).name(this.getItemColor() + "Kit " + this.getName()).amount(8).build()});
   }

   @EventHandler
   public void onPlace(BlockPlaceEvent e) {
      if (!e.isCancelled()) {
         if (e.getBlock().getType().name().contains("PLATE") && this.containsHability(e.getPlayer())) {
            e.getBlock().setMetadata("demoman", new FixedMetadataValue(HardcoreGamesMain.getInstance(), e.getPlayer()));
         }

      }
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (!event.isCancelled()) {
         if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null && event.getClickedBlock().hasMetadata("demoman") && event.getClickedBlock().getRelative(BlockFace.DOWN).getType() == Material.GRAVEL) {
            event.getClickedBlock().getMetadata("demoman").stream().findFirst().ifPresent((rawMetadata) -> {
               Player player = (Player)rawMetadata.value();
               if (!player.equals(event.getPlayer())) {
                  event.getPlayer().getWorld().createExplosion(event.getClickedBlock().getLocation(), 4.1F);
               }

            });
         }

      }
   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
      if (event.getCause().toString().contains("EXPLOSION") && event.getEntity() instanceof Player && this.containsHability((Player)event.getEntity())) {
         double damage = event.getDamage();
         double porcent = 50.0D;
         if (((Player)event.getEntity()).isBlocking()) {
            porcent = 75.0D;
         }

         event.setDamage(event.getDamage() - damage / 100.0D * porcent);
      }

   }

   protected void clean(Player player) {
   }
}