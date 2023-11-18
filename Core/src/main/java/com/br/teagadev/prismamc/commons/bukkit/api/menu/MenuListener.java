package com.br.teagadev.prismamc.commons.bukkit.api.menu;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {
   private static Listener listener;
   private static boolean openMenus;

   public static void registerListeners() {
      if (!openMenus) {
         openMenus = true;
         listener = new Listener() {
            @EventHandler(
               priority = EventPriority.LOWEST
            )
            public void onInventoryClickListener(InventoryClickEvent event) {
               if (event.getInventory() != null) {
                  Inventory inv = event.getInventory();
                  if (inv.getType() == InventoryType.CHEST) {
                     if (inv.getHolder() != null) {
                        if (inv.getHolder() instanceof MenuHolder) {
                           event.setCancelled(true);
                           if (event.getClickedInventory() == inv) {
                              if (event.getWhoClicked() instanceof Player) {
                                 if (event.getSlot() >= 0) {
                                    MenuHolder holder = (MenuHolder)inv.getHolder();
                                    MenuInventory menu = holder.getMenu();
                                    if (menu.hasItem(event.getSlot())) {
                                       Player p = (Player)event.getWhoClicked();
                                       MenuItem item = menu.getItem(event.getSlot());
                                       item.getHandler().onClick(p, inv, event.getAction() == InventoryAction.PICKUP_HALF ? ClickType.RIGHT : ClickType.LEFT, event.getCurrentItem(), event.getSlot());
                                       p = null;
                                       item = null;
                                    }

                                    holder = null;
                                    menu = null;
                                    inv = null;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            @EventHandler
            public void onClose(InventoryCloseEvent event) {
               if (event.getInventory() != null) {
                  Inventory inv = event.getInventory();
                  if (inv.getType() == InventoryType.CHEST) {
                     if (inv.getHolder() != null) {
                        if (inv.getHolder() instanceof MenuHolder) {
                           if (event.getPlayer() instanceof Player) {
                              MenuHolder holder = (MenuHolder)inv.getHolder();
                              if (holder.isOnePerPlayer()) {
                                 holder.destroy();
                                 holder = null;
                              }

                              inv = null;
                           }
                        }
                     }
                  }
               }
            }
         };
         Bukkit.getServer().getPluginManager().registerEvents(listener, BukkitMain.getInstance());
      }
   }

   public static void unregisterListeners() {
      openMenus = false;
      HandlerList.unregisterAll(listener);
      listener = null;
   }

   public static boolean isOpenMenus() {
      return openMenus;
   }
}