package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import com.br.teagadev.prismamc.nowly.pvp.PvPMain;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import com.br.teagadev.prismamc.nowly.pvp.events.PlayerDamagePlayerEvent;

public class Hulk extends Kit {
	   public Hulk() {
	      this.initialize(this.getClass().getSimpleName());
	   }

	   @EventHandler
	   public void onInteract(PlayerInteractEntityEvent e) {
	      if (e.getPlayer() != null && e.getRightClicked() instanceof Player) {
	         Player p = e.getPlayer();
	         if ((p.getInventory().getItemInHand().getType().equals(Material.AIR) || p.getInventory().getItemInHand().getType() == Material.STONE_SWORD) && this.hasAbility(p)) {
	            if (this.hasCooldown(p)) {
	               this.sendMessageCooldown(p);
	               return;
	            }

	            this.addCooldown(p, (long)this.getCooldownSeconds());
	            Player d = (Player)e.getRightClicked();
	            p.setPassenger(d);
	         }
	      }

	   }

	   @EventHandler
	   public void onDeath(EntityDeathEvent e) {
	      if (e.getEntity() instanceof Player) {
	         Player p = (Player)e.getEntity();
	         if (p.isInsideVehicle()) {
	            p.leaveVehicle();
	         }
	      }

	   }

	   @EventHandler(
	      ignoreCancelled = true
	   )
	   public void onDamage(PlayerDamagePlayerEvent event) {
	      Player damaged = event.getDamaged();
	      Player damager = event.getPlayer();
	      if (damaged.isInsideVehicle() && this.hasAbility(damager)) {
	         event.setCancelled(true);
	         damaged.leaveVehicle();
	         PvPMain.runLater(() -> {
	            damaged.setVelocity(new Vector(damager.getLocation().getDirection().getX() + 0.3D, 1.5D, damager.getLocation().getDirection().getZ() + 0.3D));
	         }, 2L);
	      }

	   }

	   protected void clean(Player player) {
	   }
	}