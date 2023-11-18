package com.br.teagadev.prismamc.hardcoregames.manager.structures.types;

import java.util.ArrayList;

import com.br.teagadev.prismamc.hardcoregames.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.BukkitSettings;

public class FinalBattle {
	   private final int altura = 128;
	   private final int radius = 30;

	   public void create() {
	      BukkitSettings.DANO_OPTION = false;
	      BukkitSettings.PVP_OPTION = false;
	      Location location = Bukkit.getWorld("world").getBlockAt(0, 2, 0).getLocation();
	      World world = location.getWorld();

	      int z;
	      int x;
	      int y;
	      for(z = -30; z < 30; ++z) {
	         for(x = -1; x < 128; ++x) {
	            for(y = -30; y < 30; ++y) {
	               location.clone().add((double)z, (double)x, (double)y).getBlock().setType(Material.AIR);
	            }
	         }
	      }

	      for(z = -30; z <= 30; ++z) {
	         if (z == -30 || z == 30) {
	            for(x = -30; x <= 30; ++x) {
	               for(y = 0; y <= 150; ++y) {
	                  world.getBlockAt(z, y, x).setType(Material.BEDROCK);
	               }
	            }
	         }
	      }

	      for(z = -30; z <= 30; ++z) {
	         if (z == -30 || z == 30) {
	            for(x = -30; x <= 30; ++x) {
	               for(y = 0; y <= 150; ++y) {
	                  world.getBlockAt(x, y, z).setType(Material.BEDROCK);
	               }
	            }
	         }
	      }

	      final Location loc = world.getBlockAt(0, 5, 0).getLocation();
	      final ArrayList<Player> players = (ArrayList)world.getPlayers();
	      (new BukkitRunnable() {
	         int teleporteds = 0;
	         final int toTeleport = players.size();

	         public void run() {
	            if (this.teleporteds > this.toTeleport) {
	               this.cancel();
	               BukkitSettings.DANO_OPTION = true;
	               BukkitSettings.PVP_OPTION = true;
	            } else {
	               try {
	                  Player target = (Player)players.get(this.teleporteds);
	                  target.setFallDistance(-5.0F);
	                  target.setNoDamageTicks(30);
	                  target.teleport(loc);
	               } catch (Exception var2) {
	               }

	               ++this.teleporteds;
	            }
	         }
	      }).runTaskTimer(BukkitMain.getInstance(), 2L, 2L);
	      Bukkit.broadcastMessage("§aA arena final foi criada");
	   }
	}