package com.br.teagadev.prismamc.commons.bukkit;

public class BukkitSettings {
   public static boolean CHAT_OPTION = true;
   public static boolean PVP_OPTION = true;
   public static boolean DANO_OPTION = true;
   public static boolean DOUBLE_COINS_OPTION = false;
   public static boolean DOUBLE_XP_OPTION = false;
   public static boolean LOGIN_OPTION = true;

   public static boolean isCHAT_OPTION() {
      return CHAT_OPTION;
   }

   public static boolean isPVP_OPTION() {
      return PVP_OPTION;
   }

   public static boolean isDANO_OPTION() {
      return DANO_OPTION;
   }

   public static boolean isDOUBLE_COINS_OPTION() {
      return DOUBLE_COINS_OPTION;
   }

   public static boolean isDOUBLE_XP_OPTION() {
      return DOUBLE_XP_OPTION;
   }

   public static boolean isLOGIN_OPTION() {
      return LOGIN_OPTION;
   }
}