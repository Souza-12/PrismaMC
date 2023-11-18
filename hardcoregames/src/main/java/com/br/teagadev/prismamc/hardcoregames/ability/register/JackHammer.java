package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class JackHammer extends Kit {
   private HashMap<UUID, Integer> blocos = new HashMap();

   public JackHammer() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.STONE_AXE).name(this.getItemColor() + "Kit " + this.getName()).build()});
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onBreak(BlockBreakEvent e) {
      if (!e.isCancelled()) {
         if (e.getPlayer().getItemInHand().getType().equals(Material.STONE_AXE) && this.containsHability(e.getPlayer())) {
            if (this.hasCooldown(e.getPlayer())) {
               this.sendMessageCooldown(e.getPlayer());
               return;
            }

            this.blocos.put(e.getPlayer().getUniqueId(), this.blocos.containsKey(e.getPlayer().getUniqueId()) ? (Integer)this.blocos.get(e.getPlayer().getUniqueId()) + 1 : 1);
            if (((Integer)this.blocos.get(e.getPlayer().getUniqueId())).equals(4)) {
               this.addCooldown(e.getPlayer(), (long)this.getCooldownSeconds());
               this.blocos.remove(e.getPlayer().getUniqueId());
               return;
            }

            if (e.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
               this.quebrar(e.getBlock(), BlockFace.UP);
            } else {
               this.quebrar(e.getBlock(), BlockFace.DOWN);
            }
         }

      }
   }

   void quebrar(final Block b, final BlockFace face) {
      (new BukkitRunnable() {
         Block block = b;

         public void run() {
            if (this.block.getType() != Material.BEDROCK && this.block.getType() != Material.ENDER_PORTAL_FRAME && this.block.getY() <= 128) {
               this.block.getWorld().playEffect(this.block.getLocation(), Effect.STEP_SOUND, this.block.getType().getId(), 30);
               this.block.setType(Material.AIR);
               this.block = this.block.getRelative(face);
            } else {
               this.cancel();
            }

         }
      }).runTaskTimer(HardcoreGamesMain.getInstance(), 2L, 2L);
   }

   protected void clean(Player player) {
      if (this.blocos.containsKey(player.getUniqueId())) {
         this.blocos.remove(player.getUniqueId());
      }

   }
}