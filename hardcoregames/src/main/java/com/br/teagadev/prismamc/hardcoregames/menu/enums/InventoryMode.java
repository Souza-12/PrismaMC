package com.br.teagadev.prismamc.hardcoregames.menu.enums;

public enum InventoryMode {
	   KIT_PRIMARIO("Selecione um kit primário"),
	   KIT_SECUNDARIO("Selecione um kit secundário"),
	   LOJA("Loja de Kits");

	   private String inventoryName;

	   private InventoryMode(String inventoryName) {
	      this.inventoryName = inventoryName;
	   }

	   public String getInventoryName() {
	      return this.inventoryName;
	   }
	}