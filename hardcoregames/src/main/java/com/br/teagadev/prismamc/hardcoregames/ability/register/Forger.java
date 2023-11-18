package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Forger extends Kit {
   public Forger() {
      this.initialize(this.getClass().getSimpleName());
      this.setUseInvincibility(true);
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      if (currentItem != null && currentItem.getType() != Material.AIR) {
         Player p = (Player)event.getWhoClicked();
         if (this.containsHability(p)) {
            int coalAmount = 0;
            Inventory inv = event.getView().getBottomInventory();
            ItemStack[] var6 = inv.getContents();
            int slot = var6.length;

            for(int var8 = 0; var8 < slot; ++var8) {
               ItemStack item = var6[var8];
               if (item != null && item.getType() == Material.COAL) {
                  coalAmount += item.getAmount();
               }
            }

            if (coalAmount == 0) {
               return;
            }

            int hadCoal = coalAmount;
            ItemStack item;
            if (currentItem.getType() == Material.COAL) {
               for(slot = 0; slot < inv.getSize(); ++slot) {
                  item = inv.getItem(slot);
                  if (item != null && item.getType().name().contains("ORE")) {
                     while(item.getAmount() > 0 && coalAmount > 0 && (item.getType() == Material.IRON_ORE || item.getType() == Material.GOLD_ORE)) {
                        item.setAmount(item.getAmount() - 1);
                        --coalAmount;
                        if (item.getType() == Material.IRON_ORE) {
                           p.getInventory().addItem(new ItemStack[]{new ItemStack(Material.IRON_INGOT)});
                        } else if (item.getType() == Material.GOLD_ORE) {
                           p.getInventory().addItem(new ItemStack[]{new ItemStack(Material.GOLD_INGOT)});
                        }
                     }

                     if (item.getAmount() == 0) {
                        inv.setItem(slot, new ItemStack(0));
                     }
                  }
               }
            } else if (currentItem.getType().name().contains("ORE")) {
               while(currentItem.getAmount() > 0 && coalAmount > 0 && (currentItem.getType() == Material.IRON_ORE || currentItem.getType() == Material.GOLD_ORE)) {
                  currentItem.setAmount(currentItem.getAmount() - 1);
                  --coalAmount;
                  if (currentItem.getType() == Material.IRON_ORE) {
                     p.getInventory().addItem(new ItemStack[]{new ItemStack(Material.IRON_INGOT)});
                  } else if (currentItem.getType() == Material.GOLD_ORE) {
                     p.getInventory().addItem(new ItemStack[]{new ItemStack(Material.GOLD_INGOT)});
                  }
               }

               if (currentItem.getAmount() == 0) {
                  event.setCurrentItem(new ItemStack(0));
               }
            }

            if (coalAmount != hadCoal) {
               for(slot = 0; slot < inv.getSize(); ++slot) {
                  item = inv.getItem(slot);
                  if (item != null && item.getType() == Material.COAL) {
                     while(coalAmount < hadCoal && item.getAmount() > 0) {
                        item.setAmount(item.getAmount() - 1);
                        ++coalAmount;
                     }

                     if (item.getAmount() == 0) {
                        inv.setItem(slot, new ItemStack(0));
                     }
                  }
               }
            }
         }
      }

   }

   protected void clean(Player player) {
   }
}