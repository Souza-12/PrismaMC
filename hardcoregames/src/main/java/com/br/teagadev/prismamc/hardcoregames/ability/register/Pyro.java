package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Pyro extends Kit {
   public Pyro() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).name(this.getItemColor() + "Kit " + this.getName()).material(Material.FIREBALL).build(), new ItemStack(Material.FLINT_AND_STEEL)});
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      ItemStack item = event.getItem();
      Player player = event.getPlayer();
      if (event.getAction() == Action.RIGHT_CLICK_AIR && item != null && item.getType() == Material.FIREBALL && this.containsHability(player)) {
         event.setCancelled(true);
         if (this.hasCooldown(player)) {
            return;
         }

         this.addCooldown(player, "Pyro", 5L);
         Fireball fireball = (Fireball)player.launchProjectile(Fireball.class);
         fireball.setIsIncendiary(true);
         fireball.setYield(fireball.getYield() * 1.5F);
      }

   }

   protected void clean(Player player) {
   }
}