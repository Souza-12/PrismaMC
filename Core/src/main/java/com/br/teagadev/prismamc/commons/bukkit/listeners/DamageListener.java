package com.br.teagadev.prismamc.commons.bukkit.listeners;

import com.br.teagadev.prismamc.commons.CommonsConst;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DamageListener implements Listener {
   public static final HashMap<Material, Double> damageMaterial = new HashMap();
   public static boolean CRITICAL = true;
   public static int CHANCE_DE_CRITICAL = 30;

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player) {
         Player damager = (Player)event.getDamager();
         double dano = 1.0D;
         ItemStack itemStack = damager.getItemInHand();
         if (itemStack != null) {
            dano = (Double)damageMaterial.get(itemStack.getType());
            if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL)) {
               dano += (double)itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
            }
         }

         Iterator var6 = damager.getActivePotionEffects().iterator();

         while(var6.hasNext()) {
            PotionEffect effect = (PotionEffect)var6.next();
            if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
               dano += (double)((effect.getAmplifier() + 1) * 2);
            } else if (effect.getType().equals(PotionEffectType.WEAKNESS)) {
               dano -= (double)(effect.getAmplifier() + 1);
            }
         }

         if (CRITICAL && this.isCritical(damager)) {
            ++dano;
         }

         if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            Iterator var10 = player.getActivePotionEffects().iterator();

            while(var10.hasNext()) {
               PotionEffect effect = (PotionEffect)var10.next();
               if (effect.getType().equals(PotionEffectType.WEAKNESS)) {
                  dano += (double)(effect.getAmplifier() + 1);
               }
            }
         }

         event.setDamage(dano);
      }
   }

   private boolean isCritical(Player p) {
      return p.getFallDistance() > 0.0F && !p.isOnGround() && CommonsConst.RANDOM.nextInt(100) <= CHANCE_DE_CRITICAL && !p.hasPotionEffect(PotionEffectType.BLINDNESS);
   }
}