package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.nowly.pvp.PvPMain;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;

public class Jumper extends Kit {
	   public Jumper() {
	      this.initialize(this.getClass().getSimpleName());
	      this.setItens(new ItemStack[]{(new ItemBuilder()).type(Material.EYE_OF_ENDER).name(this.getItemColor() + "Kit " + this.getName()).build()});
	   }

	   @EventHandler(
	      priority = EventPriority.LOWEST
	   )
	   public void onJumperListener(PlayerInteractEvent event) {
	      if (event.getAction() != Action.PHYSICAL) {
	         Player p = event.getPlayer();
	         if (this.hasAbility(p) && this.checkItem(p.getItemInHand(), this.getItemColor() + "Kit " + this.getName())) {
	            event.setCancelled(true);
	            p.updateInventory();
	            if (!this.hasCooldown(p)) {
	               this.addCooldown(p, (long)this.getCooldownSeconds());
	               p.setFallDistance(0.0F);
	               EnderPearl ender = (EnderPearl)p.launchProjectile(EnderPearl.class);
	               ender.setPassenger(p);
	               ender.setMetadata("Jumper", new FixedMetadataValue(PvPMain.getInstance(), p.getUniqueId()));
	               ender.setShooter((ProjectileSource)null);
	            } else {
	               this.sendMessageCooldown(p);
	            }
	         }

	      }
	   }

	   @EventHandler(
	      priority = EventPriority.LOWEST
	   )
	   public void onJumperHit(ProjectileHitEvent event) {
	      if (event.getEntity().hasMetadata("Jumper")) {
	         if (event.getEntity().getPassenger() != null) {
	            event.getEntity().eject();
	         }

	      }
	   }

	   protected void clean(Player player) {
	   }
	}