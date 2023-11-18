package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;

public class Monk extends Kit {
	   public Monk() {
	      this.initialize(this.getClass().getSimpleName());
	      this.setItens(new ItemStack[]{(new ItemBuilder()).type(Material.BLAZE_ROD).name(this.getItemColor() + "Kit " + this.getName()).build()});
	   }

	   @EventHandler
	   public void onInteractEntity(PlayerInteractEntityEvent e) {
	      if (e.getRightClicked() instanceof Player) {
	         Player p = e.getPlayer();
	         if (p.getItemInHand().getType().equals(Material.BLAZE_ROD) && this.hasAbility(p)) {
	            if (this.hasCooldown(p)) {
	               this.sendMessageCooldown(p);
	               return;
	            }

	            Player d = (Player)e.getRightClicked();
	            ItemStack item = d.getItemInHand();
	            int r = (new Random()).nextInt(35);
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