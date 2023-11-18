package com.br.teagadev.prismamc.commons.common.utility.string;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtility {
   public static final DecimalFormat decimalFormat = new DecimalFormat("###,###.##");

   public static String cpuQuality(double percentage) {
      if (percentage <= 60.0D) {
         return "§a" + percentage;
      } else {
         return percentage > 60.0D && percentage < 90.0D ? "§e" + percentage : "§c" + percentage;
      }
   }

   public static String ramQuality(double percentage) {
      if (percentage <= 60.0D) {
         return "§a" + percentage;
      } else {
         return percentage > 60.0D && percentage < 90.0D ? "§e" + percentage : "§c" + percentage;
      }
   }

   public static String toMillis(double d) {
      String string = String.valueOf(d);
      StringBuilder sb = new StringBuilder();
      boolean stop = false;
      char[] var5 = string.toCharArray();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         char c = var5[var7];
         if (stop) {
            return sb.append(c).toString();
         }

         if (c == '.') {
            stop = true;
         }

         sb.append(c);
      }

      return sb.toString();
   }

   public static String reformuleMegaBytes(Long megaBytes) {
      if (megaBytes <= 999L) {
         return megaBytes + " MB";
      } else {
         long mb = megaBytes;
         long gigas = megaBytes / 1000L;
         mb -= gigas * 1000L;
         if (mb != 0L) {
            String mbFormatted = String.valueOf(mb).substring(0, 1);
            return gigas + "." + mbFormatted + " GB";
         } else {
            return gigas + " GB";
         }
      }
   }

   public static String formatStringToArrayWithoutSpace(List<String> array) {
      return formatArrayToString(array, false);
   }

   public static String formatArrayToString(List<String> array, boolean lowerCase) {
      if (array.size() == 1) {
         return (String)array.get(0);
      } else {
         StringBuilder toReturn = new StringBuilder();

         for(int i = 0; i < array.size(); ++i) {
            String string = (String)array.get(i);
            if (lowerCase) {
               toReturn.append(string.toLowerCase()).append(array.size() - i > 1 ? ", " : "");
            } else {
               toReturn.append(string).append(array.size() - i > 1 ? ", " : "");
            }

            string = null;
         }

         return toReturn.toString();
      }
   }

   public static String formatArrayToStringWithoutSpace(List<String> array, boolean lowerCase) {
      if (array.size() == 1) {
         return (String)array.get(0);
      } else {
         StringBuilder toReturn = new StringBuilder();

         for(int i = 0; i < array.size(); ++i) {
            String string = (String)array.get(i);
            if (lowerCase) {
               toReturn.append(string.toLowerCase()).append(array.size() - i > 1 ? "," : "");
            } else {
               toReturn.append(string).append(array.size() - i > 1 ? "," : "");
            }

            string = null;
         }

         return toReturn.toString();
      }
   }

   public static boolean isInteger(String string) {
      try {
         Integer.parseInt(string);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static String createArgs(int index, String[] args) {
      return createArgs(index, args, "", false);
   }

   public static String createArgs(int index, String[] args, String defaultArgs, boolean color) {
      StringBuilder sb = new StringBuilder();

      for(int i = index; i < args.length; ++i) {
         sb.append(args[i]).append(i + 1 >= args.length ? "" : " ");
      }

      if (sb.length() == 0) {
         sb.append(defaultArgs);
      }

      return color ? sb.toString().replace("&", "§") : sb.toString();
   }

   public static String replace(String message, String[] old, String[] now) {
      String replaced = message;

      for(int i = 0; i < old.length; ++i) {
         replaced = replaced.replace(old[i], now[i]);
      }

      return replaced;
   }

   public static String formatValue(int valor) {
      return decimalFormat.format((long)valor);
   }

   public static List<String> formatStringToArrayWithoutSpace(String formatted) {
      List<String> lista = new ArrayList();
      if (!formatted.equals("") && !formatted.equals(" ") && !formatted.isEmpty()) {
         if (formatted.contains(" ")) {
            formatted = formatted.replaceAll(" ", "");
         }

         if (formatted.contains(",")) {
            lista.addAll(Arrays.asList(formatted.split(",")));
         } else {
            lista.add(formatted);
         }

         return lista;
      } else {
         return lista;
      }
   }

   public static List<String> formatStringToArray(String formatted) {
      List<String> lista = new ArrayList();
      if (!formatted.equals("") && !formatted.equals(" ") && !formatted.isEmpty()) {
         if (formatted.contains(",")) {
            lista.addAll(Arrays.asList(formatted.split(",")));
         } else {
            lista.add(formatted);
         }

         return lista;
      } else {
         return lista;
      }
   }

   public static Object convertValue(String value, String classExpected) {
      if (classExpected.equalsIgnoreCase("String")) {
         return value;
      } else if (classExpected.equalsIgnoreCase("Integer")) {
         return Integer.valueOf(value);
      } else if (classExpected.equalsIgnoreCase("Long")) {
         return Long.valueOf(value);
      } else if (classExpected.equalsIgnoreCase("Boolean")) {
         return Boolean.valueOf(value);
      } else {
         return classExpected.equalsIgnoreCase("List") ? "?" : value;
      }
   }
}