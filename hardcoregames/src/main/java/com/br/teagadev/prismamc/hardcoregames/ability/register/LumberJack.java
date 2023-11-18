package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class LumberJack extends Kit {
   public LumberJack() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.WOOD_AXE).name(this.getItemColor() + "Kit " + this.getName()).build()});
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onBreak(BlockBreakEvent event) {
      if (!event.isCancelled() && !HardcoreGamesMain.getGameManager().isInvencibilidade()) {
         if (event.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE) && this.containsHability(event.getPlayer())) {
            Block b = event.getBlock().getRelative(BlockFace.UP);

            Block b1;
            for(b1 = event.getBlock().getRelative(BlockFace.DOWN); b.getType().name().contains("LOG"); b = b.getRelative(BlockFace.UP)) {
               b.breakNaturally();
            }

            while(b1.getType().name().contains("LOG")) {
               b1.breakNaturally();
               b1 = b1.getRelative(BlockFace.DOWN);
            }
         }

      }
   }

   protected void clean(Player player) {
   }
}