package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;

public class Cultivator extends Kit {
   public Cultivator() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onPlace(BlockPlaceEvent e) {
      if (e.getBlock().getType() == Material.SAPLING && this.containsHability(e.getPlayer())) {
         e.getBlock().setType(Material.AIR);
         e.getBlock().getWorld().generateTree(e.getBlock().getLocation(), TreeType.TREE);
      } else if (e.getBlock().getType() == Material.CROPS && this.containsHability(e.getPlayer())) {
         e.getBlock().setData((byte)7);
      } else if (e.getBlock().getType() == Material.COCOA && this.containsHability(e.getPlayer())) {
         CocoaPlant bean = (CocoaPlant)e.getBlock().getState().getData();
         if (bean.getSize() != CocoaPlantSize.LARGE) {
            bean.setSize(CocoaPlantSize.LARGE);
            e.getBlock().setData(bean.getData());
         }

         bean = null;
      }

   }

   protected void clean(Player player) {
   }
}