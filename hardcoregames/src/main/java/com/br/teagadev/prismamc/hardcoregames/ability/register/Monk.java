package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Monk extends Kit {
   public Monk() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.BLAZE_ROD).name(this.getItemColor() + "Kit " + this.getName()).build()});
   }

   @EventHandler
   public void onInteract(PlayerInteractEntityEvent e) {
      if (e.getRightClicked() instanceof Player) {
         Player p = e.getPlayer();
         if (p.getItemInHand().getType().equals(Material.BLAZE_ROD) && this.useAbility(p)) {
            if (this.hasCooldown(p)) {
               this.sendMessageCooldown(p);
               return;
            }

            Player d = (Player)e.getRightClicked();
            ItemStack item = d.getItemInHand();
            int r = CommonsConst.RANDOM.nextInt(35);
            ItemStack i = d.getInventory().getItem(r);
            d.getInventory().setItem(r, item);
            d.setItemInHand(i);
            this.addCooldown(p, (long)this.getCooldownSeconds());
            p.sendMessage("§aMonkado!");
            d.sendMessage("§aVocê foi monkado!");
         }
      }

   }

   protected void clean(Player player) {
   }
}