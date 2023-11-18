package com.br.teagadev.prismamc.commons.common.punishment;

import com.br.teagadev.prismamc.commons.common.punishment.types.Ban;
import com.br.teagadev.prismamc.commons.common.utility.Cache;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PunishmentManager {
   private static final HashMap<String, Cache> CACHE = new HashMap();

   public static Ban getBanCache(String nick) {
      return CACHE.containsKey(nick.toLowerCase()) ? (Ban)((Cache)CACHE.get(nick.toLowerCase())).getValue1() : null;
   }

   public static void addCache(String nick, Ban ban) {
      CACHE.put(nick.toLowerCase(), new Cache(nick, ban, 1));
   }

   public static void removeCache(String nick) {
      CACHE.remove(nick.toLowerCase());
   }

   public static void checkCache() {
      checkCache((Runnable)null);
   }

   public static void checkCache(Runnable callback) {
      List<String> keys = Lists.newArrayList();
      Iterator var2 = CACHE.values().iterator();

      while(var2.hasNext()) {
         Cache cache = (Cache)var2.next();
         if (cache.isExpired()) {
            keys.add(cache.getName());
         }
      }

      if (keys.size() != 0) {
         var2 = keys.iterator();

         while(var2.hasNext()) {
            String key = (String)var2.next();
            CACHE.remove(key);
         }
      }

      keys.clear();
      keys = null;
      if (callback != null) {
         callback.run();
      }

   }
}