package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import com.br.teagadev.prismamc.nowly.pvp.events.PlayerDamagePlayerEvent;

public class Kangaroo extends Kit {
	   private Set<Player> doubleJump;

	   public Kangaroo() {
	      this.initialize(this.getClass().getSimpleName());
	      this.setItens(new ItemStack[]{(new ItemBuilder()).type(Material.FIREWORK).name(this.getItemColor() + "Kit " + this.getName()).build()});
	      this.doubleJump = new HashSet();
	   }

	   public void eject(Player p) {
	      if (this.doubleJump.contains(p)) {
	         this.doubleJump.remove(p);
	      }

	   }

	   @EventHandler
	   public void Habilidade(PlayerInteractEvent e) {
	      if (!e.getAction().equals(Action.PHYSICAL) && e.getPlayer().getItemInHand().getType().equals(Material.FIREWORK) && this.hasAbility(e.getPlayer())) {
	         e.setCancelled(true);
	         Player p = e.getPlayer();
	         if (this.hasCooldown(p, "CombatLog")) {
	            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3, 5), true);
	            p.sendMessage("§cVocê levou um hit recentemente, aguarde para usar a habilidade novamente.");
	            return;
	         }

	         if (!this.doubleJump.contains(p)) {
	            Vector velocity = p.getEyeLocation().getDirection();
	            if (p.isSneaking()) {
	               velocity = velocity.multiply(2.45F).setY(0.5F);
	            } else {
	               velocity = velocity.multiply(0.5F).setY(1.0F);
	            }

	            p.setFallDistance(-1.0F);
	            p.setVelocity(velocity);
	            this.doubleJump.add(p);
	            velocity = null;
	         }
	      }

	   }

	   @EventHandler
	   public void removeOnMove(PlayerMoveEvent e) {
	      if (this.doubleJump.contains(e.getPlayer())) {
	         if (e.getPlayer().isOnGround()) {
	            this.doubleJump.remove(e.getPlayer());
	         }
	      }
	   }

	   @EventHandler(
	      priority = EventPriority.HIGHEST
	   )
	   public void onEntityDamageEvent(EntityDamageEvent event) {
	      if (event.getEntity() instanceof Player) {
	         Player player = (Player)event.getEntity();
	         if (event.getCause() == DamageCause.FALL && this.hasAbility(player)) {
	            double damage = event.getDamage();
	            if (damage > 7.0D) {
	               event.setDamage(7.0D);
	            } else {
	               event.setCancelled(true);
	            }
	         }
	      }

	   }

	   @EventHandler(
	      ignoreCancelled = true
	   )
	   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
	      if (this.hasAbility(event.getDamaged())) {
	         this.addCooldown(event.getDamaged(), "CombatLog", 3L);
	      }

	   }

	   protected void clean(Player player) {
	      if (this.doubleJump.contains(player)) {
	         this.doubleJump.remove(player);
	      }

	   }
	}