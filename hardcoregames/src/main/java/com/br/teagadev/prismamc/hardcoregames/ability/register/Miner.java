package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Miner extends Kit {
   public Miner() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.STONE_PICKAXE).unbreakable().name(this.getItemColor() + "Kit " + this.getName()).enchantment(Enchantment.DIG_SPEED).enchantment(Enchantment.DURABILITY, 5).build(), (new ItemBuilder()).material(Material.APPLE).amount(2).name(this.getItemColor() + "Kit " + this.getName()).build()});
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onBreak(BlockBreakEvent event) {
      ItemStack hand = event.getPlayer().getInventory().getItemInHand();
      if (this.containsHability(event.getPlayer()) && hand != null && hand.getType().name().contains("PICKAXE")) {
         Location spawn = event.getBlock().getLocation().clone().add(0.5D, 0.5D, 0.5D);
         Material mat = event.getBlock().getType();
         if (mat.name().contains("ORE")) {
            event.setCancelled(true);
            Map<Material, Integer> drops = new HashMap();
            Iterator var6 = this.getNearbyBlocks(event.getBlock(), 1).iterator();

            while(true) {
               Block b;
               do {
                  if (!var6.hasNext()) {
                     int xp = drops.entrySet().stream().filter(($) -> {
                        return !((Material)$.getKey()).name().endsWith("_ORE");
                     }).mapToInt(($) -> {
                        return (Integer)$.getValue() * 2;
                     }).sum();
                     drops.forEach((material, q) -> {
                        for(int i = q; i > 0; i -= Math.min(i, 64)) {
                           spawn.getWorld().dropItem(spawn, new ItemStack(material, i));
                        }

                     });
                     if (xp > 0) {
                        ExperienceOrb orb = (ExperienceOrb)event.getBlock().getWorld().spawn(spawn, ExperienceOrb.class);
                        orb.setExperience(xp);
                     }

                     return;
                  }

                  b = (Block)var6.next();
               } while(b.getType() != mat && (mat != Material.REDSTONE_ORE || b.getType() != Material.GLOWING_REDSTONE_ORE));

               b.getDrops().forEach((drop) -> {
                  drops.put(drop.getType(), (Integer)drops.getOrDefault(drop.getType(), 0) + drop.getAmount());
               });
               b.setType(Material.AIR);
            }
         }
      }
   }

   private List<Block> getNearbyBlocks(Block block, int i) {
      List<Block> blocos = new ArrayList();

      for(int x = -i; x <= i; ++x) {
         for(int y = -i; y <= i; ++y) {
            for(int z = -i; z <= i; ++z) {
               blocos.add(block.getLocation().clone().add((double)x, (double)y, (double)z).getBlock());
            }
         }
      }

      return blocos;
   }

   @EventHandler
   public void onEat(PlayerItemConsumeEvent event) {
      if (this.containsHability(event.getPlayer())) {
         if (event.getItem().getType() == Material.APPLE) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 1));
         }

      }
   }

   protected void clean(Player player) {
   }
}