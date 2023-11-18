package com.br.teagadev.prismamc.nowly.pvp.menu.enums;

public enum InventoryMode {
	   KIT_PRIMARIO("Selecione o kit Primário"),
	   KIT_SECUNDARIO("Selecione o kit Secundario"),
	   LOJA("Loja de Kits");

	   private String inventoryName;

	   private InventoryMode(String inventoryName) {
	      this.inventoryName = inventoryName;
	   }

	   public String getInventoryName() {
	      return this.inventoryName;
	   }
	}