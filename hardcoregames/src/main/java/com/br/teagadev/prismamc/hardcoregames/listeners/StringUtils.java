package com.br.teagadev.prismamc.hardcoregames.listeners;

import java.util.Collection;

public class StringUtils {
   public static <T> String join(T[] array, int index, String separator) {
      StringBuilder joined = new StringBuilder();

      for(int slot = index; slot < array.length; ++slot) {
         joined.append(array[slot].toString() + (slot + 1 == array.length ? "" : separator));
      }

      return joined.toString();
   }

   public static <T> String join(T[] array, String separator) {
      return join(array, 0, separator);
   }

   public static <T> String join(Collection<T> collection, String separator) {
      return join(collection.toArray(new Object[collection.size()]), separator);
   }
}