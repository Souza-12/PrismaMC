package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Digger extends Kit {
   public Digger() {
      this.initialize(this.getClass().getSimpleName());
      this.setUseInvincibility(true);
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.DRAGON_EGG).name(this.getItemColor() + "Kit " + this.getName()).amount(5).build()});
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlace(BlockPlaceEvent event) {
      ItemStack item = event.getItemInHand();
      if (item != null && item.getTypeId() == Material.DRAGON_EGG.getId() && this.useAbility(event.getPlayer())) {
         if (this.hasCooldown(event.getPlayer())) {
            this.sendMessageCooldown(event.getPlayer());
            return;
         }

         if (event.getBlock().getY() > 110) {
            return;
         }

         this.addCooldown(event.getPlayer(), (long)this.getCooldownSeconds());
         final Block b = event.getBlock();
         b.setType(Material.AIR);
         (new BukkitRunnable() {
            public void run() {
               int dist = (int)Math.ceil(2.0D);

               for(int y = -1; y >= -5; --y) {
                  for(int x = -dist; x <= dist; ++x) {
                     for(int z = -dist; z <= dist; ++z) {
                        if (b.getY() + y > 0) {
                           Block block = b.getWorld().getBlockAt(b.getX() + x, b.getY() + y, b.getZ() + z);
                           if (block.getType() != Material.BEDROCK) {
                              block.setType(Material.AIR);
                           }

                           block = null;
                        }
                     }
                  }
               }

            }
         }).runTaskLater(HardcoreGamesMain.getInstance(), 20L);
      }

      item = null;
   }

   protected void clean(Player player) {
   }
}