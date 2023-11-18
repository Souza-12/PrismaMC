package com.br.teagadev.prismamc.commons.common.serverinfo.enums;

public enum GameType {
   UNKNOWN("Unknown"),
   SOLO("Solo"),
   DUO("Duo");

   private final String name;

   private GameType(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}