package com.br.teagadev.prismamc.commons.common.profile.addons;

public enum League {
   BronzeI("§7", "I✯", "Bronze I", 1000),
   BronzeII("§7", "II✯", "Bronze II", 2000),
   BronzeIII("§7", "III✯", "Bronze III", 3000),
   BronzeIV("§7", "IV✯", "Bronze IV", 3500),
   BronzeV("§7", "V✯", "Bronze V", 4000),
   GoldI("§6", "I✻", "Gold I", 4500),
   GoldII("§6", "II✻", "Gold II", 5800),
   GoldIII("§6", "III✻", "Gold III", 7000),
   GoldIV("§6", "IV✻", "Gold IV", 10000),
   GoldV("§6", "V✻", "Gold V", 13000),
   PlatinumI("§3", "I❖", "Platinum I", 17000),
   PlatinumII("§3", "II❖", "Platinum II", 20000),
   PlatinumIII("§3", "III❖", "Platinum III", 25000),
   PlatinumIV("§3", "IV❖", "Platinum IV", 28000),
   PlatinumV("§3", "V❖", "Platinum V", 30000),
   DiamondI("§b", "I❆", "Diamond I", 33000),
   DiamondII("§b", "II❆", "Diamond II", 35000),
   DiamondIII("§b", "III❆", "Diamond III", 40000),
   DiamondIV("§b", "IV❆", "Diamond IV", 46000),
   DiamondV("§b", "V❆", "Diamond IV", 50000),
   EnderloreI("§5", "I✦", "Enderlore I", 53000),
   EnderloreII("§5", "II✦", "Enderlore II", 56000),
   EnderloreIII("§5", "III✦", "Enderlore III", 58000),
   EnderloreIV("§5", "IV✦", "Enderlore IV", 60000),
   EnderloreV("§5", "IV✦", "Enderlore V", 63000),
   ImmortalI("§c", "I✿", "Immortal I", 67000),
   ImmortalII("§c", "II✿", "Immortal II", 70000),
   ImmortalIII("§c", "III✿", "Immortal III", 73000),
   ImmortalIV("§c", "IV✿", "Immortal IV", 75000),
   Champion("§4", "❂", "Champion", 80000);

   private String color;
   private String symbol;
   private String name;
   private int experience;

   public static League getRanking(String ligaNome) {
      League liga = BronzeI;
      League[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         League rank = var2[var4];
         if (rank.getName().equalsIgnoreCase(ligaNome)) {
            liga = rank;
            break;
         }
      }

      return liga;
   }

   public static League getRanking(int xp) {
      if (xp >= Champion.getExperience()) {
         return ImmortalIV;
      } else if (xp < BronzeI.getExperience()) {
         return BronzeI;
      } else {
         League liga = BronzeI;
         League[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            League rank = var2[var4];
            if (xp <= rank.experience && xp > rank.getMin()) {
               liga = rank;
            }
         }

         return liga;
      }
   }

   public int getMin() {
      int min = 0;
      if (this.ordinal() > 0) {
         min = values()[this.ordinal() - 1].getExperience();
      }

      return min;
   }

   public League getPreviousLeague() {
      return this == BronzeI ? BronzeI : values()[this.ordinal() - 1];
   }

   public League getNextLeague() {
      return this == Champion ? Champion : values()[this.ordinal() + 1];
   }

   private League(String color, String symbol, String name, int experience) {
      this.color = color;
      this.symbol = symbol;
      this.name = name;
      this.experience = experience;
   }

   public String getColor() {
      return this.color;
   }

   public String getSymbol() {
      return this.symbol;
   }

   public String getName() {
      return this.name;
   }

   public int getExperience() {
      return this.experience;
   }
}