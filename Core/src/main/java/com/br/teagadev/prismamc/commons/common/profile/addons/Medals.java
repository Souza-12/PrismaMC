package com.br.teagadev.prismamc.commons.common.profile.addons;

public enum Medals {
   BETA(1, "beta", "§e", "❝"),
   CAFE(2, "cafe", "§8", "☕"),
   MUSIC(3, "music", "§5", "♫"),
   FLASH(4, "flash", "§6", "⚡"),
   Yin_Yang(5, "Yin", "§f", "☯"),
   WARRIOR(6, "warrior", "§7", "⚔");
   private final int id;
   private final String name;
   private final String color;
   private final String symbol;

   private Medals(int id, String name, String color, String symbol) {
      this.id = id;
      this.name = name;
      this.color = color;
      this.symbol = symbol;
   }

   public static Medals getMedalById(int id) {
      if (id == 0) {
         return null;
      } else {
         Medals medal = null;
         Medals[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Medals medals = var2[var4];
            if (medals.getId() == id) {
               medal = medals;
               break;
            }
         }

         return medal;
      }
   }

   public static Medals getMedalByName(String name) {
      Medals medal = null;
      Medals[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Medals medals = var2[var4];
         if (medals.getName().equalsIgnoreCase(name)) {
            medal = medals;
            break;
         }

         if (medals.getName().startsWith(name)) {
            medal = medals;
            break;
         }
      }

      return medal;
   }

   public static Boolean existMedal(String name) {
      boolean existe = false;
      Medals[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Medals medals = var2[var4];
         if (medals.getName().equalsIgnoreCase(name)) {
            existe = true;
            break;
         }
      }

      return existe;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getColor() {
      return this.color;
   }

   public String getSymbol() {
      return this.symbol;
   }
}