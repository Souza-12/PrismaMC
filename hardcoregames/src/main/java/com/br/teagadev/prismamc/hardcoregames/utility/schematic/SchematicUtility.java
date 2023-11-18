package com.br.teagadev.prismamc.hardcoregames.utility.schematic;

import java.io.File;
import java.io.IOException;

import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import org.bukkit.Location;

import com.br.teagadev.prismamc.commons.common.utility.system.Machine;

public class SchematicUtility {
	   public static void spawnarSchematic(String nome, Location loc, boolean force) {
	      File dir = new File(Machine.getDiretorio() + Machine.getSeparador() + "schematics");
	      if (!dir.exists()) {
	         dir.mkdir();
	      }

	      File file = new File(Machine.getDiretorio() + Machine.getSeparador() + "schematics", nome + ".schematic");
	      if (file.exists()) {
	         HardcoreGamesMain.console("Tentando carregar a schematic '" + nome + "'...");

	         try {
	            SchematicLoader schematic = new SchematicLoader(nome, file);
	            schematic.paste(nome, loc, getBlockSpeed(nome), force);
	         } catch (IOException var7) {
	            HardcoreGamesMain.console("Ocorreu um erro ao tentar carregar a schematic '" + nome + "' -> " + var7.getLocalizedMessage());
	         }
	      } else {
	         HardcoreGamesMain.console("SchematicLoader '" + nome + "' nao existe.");
	      }

	   }

	   private static int getBlockSpeed(String type) {
	      if (type.equalsIgnoreCase("feast")) {
	         return 100;
	      } else {
	         return type.equalsIgnoreCase("minifeast") ? 150 : 100;
	      }
	   }
	}