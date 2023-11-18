package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Specialist extends Kit {
   private final ItemStack item;

   public Specialist() {
      this.initialize(this.getClass().getSimpleName());
      this.item = (new ItemBuilder()).material(Material.ENCHANTMENT_TABLE).name(this.getItemColor() + "Kit " + this.getName()).build();
      this.setItens(new ItemStack[]{this.item});
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onDeath(PlayerDeathEvent event) {
      Player morreu = event.getEntity();
      Player killer = morreu.getKiller();
      if (killer != null) {
         if (this.containsHability(killer)) {
            killer.setLevel(killer.getLevel() + 1);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onInteract(PlayerInteractEvent e) {
      if (e.getAction() != Action.PHYSICAL) {
         Player player = e.getPlayer();
         if (this.containsHability(player)) {
            if (this.item.equals(e.getItem())) {
               e.setCancelled(true);
               player.openEnchanting((Location)null, true);
            }
         }
      }
   }

   protected void clean(Player player) {
   }
}