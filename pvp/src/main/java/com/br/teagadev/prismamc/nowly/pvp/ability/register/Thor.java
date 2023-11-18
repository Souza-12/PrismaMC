package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import java.util.HashSet;
import java.util.Iterator;

import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;

public class Thor extends Kit {
	   public Thor() {
	      this.initialize(this.getClass().getSimpleName());
	      this.setItens(new ItemStack[]{(new ItemBuilder()).type(Material.WOOD_AXE).name(this.getItemColor() + "Kit " + this.getName()).build()});
	   }

	   @EventHandler
	   public void onInteract(PlayerInteractEvent e) {
	      if (e.getAction() != Action.PHYSICAL) {
	         if (e.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE) && e.getAction().name().contains("RIGHT") && this.hasAbility(e.getPlayer())) {
	            if (this.hasCooldown(e.getPlayer())) {
	               this.sendMessageCooldown(e.getPlayer());
	               return;
	            }

	            Location loc = e.getPlayer().getTargetBlock((HashSet<Byte>) null, 25).getLocation();
	            if (!loc.getBlock().getType().equals(Material.AIR)) {
	               this.addCooldown(e.getPlayer(), (long)this.getCooldownSeconds());
	               LightningStrike strike = loc.getWorld().strikeLightning(loc);
	               Iterator var4 = strike.getNearbyEntities(4.0D, 4.0D, 4.0D).iterator();

	               while(var4.hasNext()) {
	                  Entity nearby = (Entity)var4.next();
	                  if (nearby instanceof Player) {
	                     nearby.setFireTicks(100);
	                  }
	               }

	               e.getPlayer().setFireTicks(0);
	            }
	         }

	      }
	   }

	   protected void clean(Player player) {
	   }
	}