package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import com.br.teagadev.prismamc.nowly.pvp.PvPMain;
import com.br.teagadev.prismamc.nowly.pvp.listeners.CombatLogListener;
import lombok.val;
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

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import org.bukkit.metadata.FixedMetadataValue;

public class Fisherman extends Kit {
	   public Fisherman() {
	      this.initialize(this.getClass().getSimpleName());
	      this.setItens(new ItemStack[]{(new ItemBuilder()).type(Material.FISHING_ROD).name(this.getItemColor() + "Kit " + this.getName()).unbreakable().build()});
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
	      if (event.getState() == State.FISHING && this.hasAbility(event.getPlayer())) {
	         event.getHook().setMetadata("fisherman", new FixedMetadataValue(PvPMain.getInstance(), event.getPlayer()));
	      }

	      if (event.getState() == State.CAUGHT_ENTITY && event.getCaught() instanceof Player) {
	         Player player = event.getPlayer();
	         if (this.hasAbility(player) && !CombatLogListener.isProtected(player)) {
	            Player caughtPlayer = (Player)event.getCaught();
	            if (caughtPlayer != player) {
	               if (!CombatLogListener.isProtected(caughtPlayer)) {
	                  caughtPlayer.teleport(player.getLocation());
	               }
	            } else {
	               event.setCancelled(true);
	            }
	         }
	      }

	   }

	   protected void clean(Player player) {
	   }
	}