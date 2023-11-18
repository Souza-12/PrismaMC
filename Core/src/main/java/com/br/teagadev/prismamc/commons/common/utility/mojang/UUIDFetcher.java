package com.br.teagadev.prismamc.commons.common.utility.mojang;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.common.utility.Cache;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class UUIDFetcher {
   private static final HashMap<String, Cache> CACHE = new HashMap();
   private final List<String> apis = new ArrayList();

   public UUIDFetcher() {
      this.apis.add("https://api.mojang.com/users/profiles/minecraft/%s");
      this.apis.add("https://api.minetools.eu/uuid/%s");
   }

   public int getCacheSize() {
      return CACHE.size();
   }

   public void cleanCache() {
      CACHE.clear();
   }

   private UUID request(String name) throws UUIDFetcher.UUIDFetcherException {
      return this.request(0, (String)this.apis.get(0), name);
   }

   public UUID getOfflineUUID(String nick) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nick).getBytes(Charsets.UTF_8));
   }

   private UUID request(int idx, String api, String name) throws UUIDFetcher.UUIDFetcherException {
      try {
         URLConnection con = (new URL(String.format(api, name))).openConnection();
         JsonElement element = CommonsConst.PARSER.parse(new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)));
         if (element instanceof JsonObject) {
            JsonObject object = (JsonObject)element;
            if (object.has("error") && object.has("errorMessage")) {
               throw new Exception(object.get("errorMessage").getAsString());
            }

            if (object.has("id")) {
               return UUIDParser.parse(object.get("id"));
            }

            if (object.has("uuid")) {
               JsonObject uuid = object.getAsJsonObject("uuid");
               if (uuid.has("formatted")) {
                  return UUIDParser.parse(object.get("formatted"));
               }
            }
         }

         return null;
      } catch (Exception var8) {
         ++idx;
         if (idx < this.apis.size()) {
            api = (String)this.apis.get(idx);
            return this.request(idx, api, name);
         } else {
            throw new UUIDFetcher.UUIDFetcherException("Failed");
         }
      }
   }

   public UUID getUUID(String name) throws UUIDFetcher.UUIDFetcherException {
      if (CACHE.containsKey(name)) {
         return ((Cache)CACHE.get(name)).getUUID();
      } else {
         if (name.matches("\\w{3,16}")) {
            if (CACHE.containsKey(name)) {
               return ((Cache)CACHE.get(name)).getUUID();
            }

            try {
               UUID uniqueId = this.request(name);
               if (uniqueId != null) {
                  CACHE.put(name, new Cache(name, uniqueId));
                  return uniqueId;
               }
            } catch (Exception var4) {
               var4.printStackTrace();
            }
         }

         return UUIDParser.parse(name);
      }
   }

   public void checkCache() {
      this.checkCache((Runnable)null);
   }

   public void checkCache(Runnable callback) {
      List<String> keys = Lists.newArrayList();
      Iterator var3 = CACHE.values().iterator();

      while(var3.hasNext()) {
         Cache cache = (Cache)var3.next();
         if (cache.isExpired()) {
            keys.add(cache.getName());
         }
      }

      if (keys.size() != 0) {
         var3 = keys.iterator();

         while(var3.hasNext()) {
            String key = (String)var3.next();
            CACHE.remove(key);
         }
      }

      keys.clear();
      if (callback != null) {
         callback.run();
      }

   }

   public static class UUIDFetcherException extends Exception {
      private static final long serialVersionUID = 1L;
      private final String reason;

      public UUIDFetcherException(String reason) {
         this.reason = reason;
      }

      public String getReason() {
         return this.reason;
      }

      public String getMessage() {
         return this.reason;
      }
   }
}