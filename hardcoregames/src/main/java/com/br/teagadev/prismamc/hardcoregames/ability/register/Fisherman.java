package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Fisherman extends Kit {
   public Fisherman() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.FISHING_ROD).name(this.getItemColor() + "Kit " + this.getName()).unbreakable().build()});
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onDamage(EntityDamageByEntityEvent e) {
      if (e.getEntity() instanceof Player) {
         Entity damager = e.getDamager();
         if (damager != null && damager.hasMetadata("fisherman")) {
            e.setDamage(0.0D);
            damager.getMetadata("fisherman").stream().findFirst().ifPresent((rawMeta) -> {
               Player player = (Player)rawMeta.value();
               player.sendMessage(ChatColor.GREEN + "Você fisgou " + e.getEntity().getName());
            });
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onPlayerFishListener(PlayerFishEvent event) {
      if (event.getState() == State.FISHING && this.containsHability(event.getPlayer())) {
         event.getHook().setMetadata("fisherman", new FixedMetadataValue(HardcoreGamesMain.getInstance(), event.getPlayer()));
      }

      if (event.getState() == State.CAUGHT_ENTITY && event.getCaught() instanceof Player) {
         Player player = event.getPlayer();
         if (this.containsHability(player)) {
            Player caughtPlayer = (Player)event.getCaught();
            if (caughtPlayer == player) {
               return;
            }

            caughtPlayer.teleport(player.getLocation());
         }
      }

   }

   protected void clean(Player player) {
   }
}