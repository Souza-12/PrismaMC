package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.utility.LocationUtil;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

public class Launcher extends Kit {
   private final String NOFALL_TAG = "nofall";
   private final String NOFALL_TAG_TIME = "nofall.time";

   public Launcher() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.SPONGE).name(this.getItemColor() + "Kit" + this.getName()).amount(20).build()});
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onRealMove(PlayerMoveEvent event) {
      if (LocationUtil.isRealMovement(event.getFrom(), event.getTo())) {
         if (!event.isCancelled()) {
            Material material = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
            if (material.equals(Material.SPONGE)) {
               Player player = event.getPlayer();
               Location loc = event.getTo().getBlock().getLocation();
               Vector sponge = player.getLocation().getDirection().multiply(0).setY(3);
               player.setVelocity(sponge);
               player.setMetadata("nofall", new FixedMetadataValue(HardcoreGamesMain.getInstance(), true));
               player.setMetadata("nofall.time", new FixedMetadataValue(HardcoreGamesMain.getInstance(), System.currentTimeMillis()));
               player.playSound(loc, Sound.FIREWORK_LAUNCH, 6.0F, 1.0F);
            }

            material = null;
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onDamage(EntityDamageEvent event) {
      if (event.getCause() == DamageCause.FALL) {
         if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            if (p.hasMetadata("nofall")) {
               p.removeMetadata("nofall", HardcoreGamesMain.getInstance());
               if (!p.hasMetadata("nofall.time")) {
                  event.setCancelled(true);
                  return;
               }

               if (GamerManager.getGamer(p.getUniqueId()).containsKit("Stomper")) {
                  p.removeMetadata("nofall.time", HardcoreGamesMain.getInstance());
                  return;
               }

               Long time = ((MetadataValue)p.getMetadata("nofall.time").get(0)).asLong();
               if (time + 6200L > System.currentTimeMillis()) {
                  event.setCancelled(true);
               }

               p.removeMetadata("nofall.time", HardcoreGamesMain.getInstance());
            }

         }
      }
   }

   protected void clean(Player player) {
   }
}