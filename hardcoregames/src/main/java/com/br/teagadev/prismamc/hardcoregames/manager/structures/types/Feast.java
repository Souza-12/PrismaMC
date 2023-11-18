package com.br.teagadev.prismamc.hardcoregames.manager.structures.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.StringUtils;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesOptions;
import com.br.teagadev.prismamc.hardcoregames.utility.HardcoreGamesUtility;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.teagadev.prismamc.hardcoregames.manager.structures.StructuresManager;
import com.br.teagadev.prismamc.hardcoregames.utility.schematic.SchematicUtility;

import lombok.Getter;


public class Feast {
   private boolean spawned;
   private Location location;
   private Location enchantmentTable = null;
   private List<Block> chests = new ArrayList();
   private Listener listener;

   public void createFeast(final Location location) {
      if (!this.isSpawned()) {
         this.location = location;
         SchematicUtility.spawnarSchematic("feast", location, false);
         int x = location.getBlockX();
         int y = location.getBlockY();
         int z = location.getBlockZ();
         final String coords = "(" + x + ", " + y + ", " + z + ")";
         this.registerListener();
         final int totalTime = HardcoreGamesMain.getTimerManager().getTime().get() + 301;
         (new BukkitRunnable() {
            public void run() {
               Feast.this.spawned = true;
               int segundos = totalTime - HardcoreGamesMain.getTimerManager().getTime().get();
               if (!HardcoreGamesOptions.FEAST) {
                  this.cancel();
                  Bukkit.broadcastMessage("§cO feast foi cancelado!");
                  Feast.this.destroyListener();
               } else {
                  if (segundos != 300 && segundos != 240 && segundos != 180 && segundos != 120) {
                     if (segundos == 60) {
                        Bukkit.broadcastMessage("§cO feast irá spawnar em %coords% em %tempo%.".replace("%coords%", coords).replace("%tempo%", "1 minuto"));
                     } else if (segundos == 30 || segundos == 15 || segundos == 10 || segundos > 1 && segundos <= 5) {
                        Bukkit.broadcastMessage("§cO feast irá spawnar em %coords% em %tempo%.".replace("%coords%", coords).replace("%tempo%", segundos + " segundos"));
                     } else if (segundos == 1) {
                        Bukkit.broadcastMessage("§cO feast irá spawnar em %coords% em %tempo%.".replace("%coords%", coords).replace("%tempo%", segundos + " segundo"));
                     } else if (segundos <= 0) {
                        HardcoreGamesUtility.strikeLightning(location);
                        Feast.this.destroyListener();
                        Feast.this.fillChests();
                        Bukkit.broadcastMessage("§aO feast spawnou em %coords%!".replace("%coords%", coords));
                        this.cancel();
                     }
                  } else {
                     Bukkit.broadcastMessage("§cO feast irá spawnar em %coords% em %tempo%.".replace("%coords%", coords).replace("%tempo%", segundos / 60 + " minutos"));
                  }

               }
            }
         }).runTaskTimer(HardcoreGamesMain.getInstance(), 0L, 20L);
      }
   }

   public void fillChests() {
      if (this.location != null) {
         Iterator var1 = this.chests.iterator();

         while(var1.hasNext()) {
            Block chest = (Block)var1.next();
            chest.setType(Material.CHEST);
            StructuresManager.addChestItens((Chest)chest.getLocation().getBlock().getState());
         }

         if (this.enchantmentTable != null) {
            this.enchantmentTable.getBlock().setType(Material.ENCHANTMENT_TABLE);
         }

         this.chests.clear();
         this.chests = null;
      }
   }

   public void registerListener() {
      this.listener = new Listener() {
         @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
         )
         public void onPlace(BlockBreakEvent event) {
            if (Feast.this.enchantmentTable != null) {
               if (event.getBlock().getLocation().distance(Feast.this.enchantmentTable) < 15.0D) {
                  event.setCancelled(true);
               }

            }
         }
      };
      Bukkit.getServer().getPluginManager().registerEvents(this.listener, HardcoreGamesMain.getInstance());
   }

   public void destroyListener() {
      HandlerList.unregisterAll(this.listener);
      this.listener = null;
   }

   public void addChest(Block block) {
      this.chests.add(block);
   }

   public void setEnchantmentTable(Location location) {
      this.enchantmentTable = location;
   }

   public boolean isSpawned() {
      return this.spawned;
   }

   public Location getLocation() {
      return this.location;
   }

   public Location getEnchantmentTable() {
      return this.enchantmentTable;
   }

   public List<Block> getChests() {
      return this.chests;
   }

   public Listener getListener() {
      return this.listener;
   }
}