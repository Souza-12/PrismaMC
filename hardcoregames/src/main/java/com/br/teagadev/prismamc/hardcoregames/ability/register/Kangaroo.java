package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.utility.LocationUtil;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerDamagePlayerEvent;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
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

public class Kangaroo extends Kit {
   private Set<Player> doubleJump;

   public Kangaroo() {
      this.initialize(this.getClass().getSimpleName());
      this.setUseInvincibility(true);
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.FIREWORK).name(this.getItemColor() + "Kit " + this.getName()).build()});
      this.doubleJump = new HashSet();
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (!event.getAction().equals(Action.PHYSICAL) && event.getPlayer().getItemInHand().getType().equals(Material.FIREWORK) && this.containsHability(event.getPlayer())) {
         event.setCancelled(true);
         Player p = event.getPlayer();
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
      if (LocationUtil.isRealMovement(e.getFrom(), e.getTo())) {
         if (this.doubleJump.contains(e.getPlayer())) {
            if (e.getPlayer().isOnGround()) {
               this.doubleJump.remove(e.getPlayer());
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onEntityDamageEvent(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (event.getCause() == DamageCause.FALL && this.containsHability(player)) {
            double damage = event.getDamage();
            if (damage > 7.0D) {
               event.setDamage(4.0D);
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
      if (this.containsHability(event.getDamaged())) {
         this.addCooldown(event.getDamaged(), "CombatLog", 3L);
      }

   }

   protected void clean(Player player) {
      if (this.doubleJump.contains(player)) {
         this.doubleJump.remove(player);
      }

   }
}