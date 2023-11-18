package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Phantom extends Kit {
   public Phantom() {
      this.initialize(this.getClass().getSimpleName());
      this.setUseInvincibility(true);
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.FEATHER).name(this.getItemColor() + "Kit " + this.getName()).build()});
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (player.getItemInHand().getType().equals(Material.FEATHER) && this.useAbility(player) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
         if (this.hasCooldown(player)) {
            this.sendMessageCooldown(player);
            return;
         }

         this.addCooldown(player, (long)this.getCooldownSeconds());
         player.setAllowFlight(true);
         player.setFlying(true);
         player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 2.0F, 1.0F);
         Iterator var3 = player.getNearbyEntities(30.0D, 30.0D, 30.0D).iterator();

         while(var3.hasNext()) {
            Entity c = (Entity)var3.next();
            if (c instanceof Player && c != player) {
               Player d = (Player)c;
               d.sendMessage("§4Há um phantom por perto!");
            }
         }

         HardcoreGamesMain.runLater(() -> {
            if (player.isOnline()) {
               player.setFallDistance(-10.0F);
               player.playSound(player.getLocation(), Sound.AMBIENCE_RAIN, 3.0F, 4.0F);
               player.setAllowFlight(false);
               player.setFlying(false);
            }
         }, 142L);
      }

   }

   protected void clean(Player player) {
   }
}