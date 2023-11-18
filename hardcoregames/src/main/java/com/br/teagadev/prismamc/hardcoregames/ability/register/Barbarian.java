package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Barbarian extends Kit {
   private HashMap<UUID, Integer> kills = new HashMap();

   public Barbarian() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.WOOD_SWORD).name(this.getItemColor() + "Barbarian Sword").unbreakable().build()});
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onDeath(PlayerDeathEvent event) {
      Player morreu = event.getEntity();
      Player killer = morreu.getKiller();
      if (killer != null) {
         if (killer instanceof Player) {
            if (this.kills.containsKey(killer.getUniqueId())) {
               this.kills.put(killer.getUniqueId(), (Integer)this.kills.get(killer.getUniqueId()) + 1);
            } else {
               this.kills.put(killer.getUniqueId(), 1);
            }

            if (this.checkItem(killer.getItemInHand(), this.getItemColor() + "Barbarian Sword")) {
               switch((Integer)this.kills.get(killer.getUniqueId())) {
               case 1:
                  killer.getItemInHand().setType(Material.STONE_SWORD);
                  killer.getItemInHand().setDurability((short)0);
               case 2:
               case 3:
               case 4:
               case 6:
               case 7:
               case 9:
               case 11:
               default:
                  break;
               case 5:
                  killer.getItemInHand().setType(Material.IRON_SWORD);
                  killer.getItemInHand().setDurability((short)0);
                  break;
               case 8:
                  killer.getItemInHand().setType(Material.DIAMOND_SWORD);
                  killer.getItemInHand().setDurability((short)0);
                  break;
               case 10:
                  killer.getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                  killer.getItemInHand().setDurability((short)0);
                  break;
               case 12:
                  killer.getItemInHand().addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
                  killer.getItemInHand().setDurability((short)0);
               }
            }

         }
      }
   }

   protected void clean(Player player) {
      if (this.kills.containsKey(player.getUniqueId())) {
         this.kills.remove(player.getUniqueId());
      }

   }
}