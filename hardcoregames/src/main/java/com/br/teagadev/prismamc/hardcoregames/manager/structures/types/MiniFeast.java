package com.br.teagadev.prismamc.hardcoregames.manager.structures.types;

import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.hardcoregames.manager.structures.StructuresManager;
import com.br.teagadev.prismamc.hardcoregames.utility.schematic.SchematicUtility;

public class MiniFeast {
	   public static void create() {
	      Location location = StructuresManager.getValidLocation(false);
	      SchematicUtility.spawnarSchematic("minifeast", location, false);
	      int reduceX = CommonsConst.RANDOM.nextInt(60) + 1;
	      int reduceZ = CommonsConst.RANDOM.nextInt(40) + 1;
	      int upX = CommonsConst.RANDOM.nextInt(40) + 1;
	      int upZ = CommonsConst.RANDOM.nextInt(70) + 1;
	      int x = location.getBlockX() + upX;
	      int z = location.getBlockZ() + upZ;
	      int x1 = location.getBlockX() - reduceX;
	      int z1 = location.getBlockZ() - reduceZ;
	      Bukkit.broadcastMessage("§cUm mini-feast spawnou entre: (%x% x, %z% z e x %x1%, z %z1%)".replace("%x%", "" + x).replace("%z%", "" + z).replace("%x1%", "" + x1).replace("%z1%", "" + z1));
	      HardcoreGamesMain.console("MiniFeast spawnou em -> " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
	      location = null;
	   }
	}