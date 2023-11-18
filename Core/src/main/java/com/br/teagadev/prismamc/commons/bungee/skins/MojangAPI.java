package com.br.teagadev.prismamc.commons.bungee.skins;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MojangAPI {
   private static final String uuidurl = "https://api.minetools.eu/uuid/%name%";
   private static final String uuidurl_mojang = "https://api.mojang.com/users/profiles/minecraft/%name%";
   private static final String uuidurl_backup = "https://api.ashcon.app/mojang/v2/user/%name%";
   private static final String skinurl = "https://api.minetools.eu/profile/%uuid%";
   private static final String skinurl_mojang = "https://sessionserver.mojang.com/session/minecraft/profile/%uuid%?unsigned=false";
   private static final String skinurl_backup = "https://api.ashcon.app/mojang/v2/user/%uuid%";

   public static Object getSkinProperty(String uuid, boolean tryNext) {
      try {
         String output = readURL("https://api.minetools.eu/profile/%uuid%".replace("%uuid%", uuid));
         JsonElement element = (new JsonParser()).parse(output);
         JsonObject obj = element.getAsJsonObject();
         MojangAPI.Property property = new MojangAPI.Property();
         if (obj.has("raw")) {
            JsonObject raw = obj.getAsJsonObject("raw");
            if (property.valuesFromJson(raw)) {
               return SkinStorage.createProperty("textures", property.getValue(), property.getSignature());
            }
         }

         return null;
      } catch (Exception var7) {
         return tryNext ? getSkinPropertyMojang(uuid) : null;
      }
   }

   public static Object getSkinProperty(String uuid) {
      return getSkinProperty(uuid, true);
   }

   public static Object getSkinPropertyMojang(String uuid, boolean tryNext) {
      try {
         String output = readURL("https://sessionserver.mojang.com/session/minecraft/profile/%uuid%?unsigned=false".replace("%uuid%", uuid));
         JsonElement element = (new JsonParser()).parse(output);
         JsonObject obj = element.getAsJsonObject();
         MojangAPI.Property property = new MojangAPI.Property();
         return property.valuesFromJson(obj) ? SkinStorage.createProperty("textures", property.getValue(), property.getSignature()) : null;
      } catch (Exception var6) {
         return tryNext ? getSkinPropertyBackup(uuid) : null;
      }
   }

   public static Object getSkinPropertyMojang(String uuid) {
      return getSkinPropertyMojang(uuid, true);
   }

   public static Object getSkinPropertyBackup(String uuid) {
      try {
         String output = readURL("https://api.ashcon.app/mojang/v2/user/%uuid%".replace("%uuid%", uuid), 10000);
         JsonElement element = (new JsonParser()).parse(output);
         JsonObject obj = element.getAsJsonObject();
         JsonObject textures = obj.get("textures").getAsJsonObject();
         JsonObject rawTextures = textures.get("raw").getAsJsonObject();
         MojangAPI.Property property = new MojangAPI.Property();
         property.setValue(rawTextures.get("value").getAsString());
         property.setSignature(rawTextures.get("signature").getAsString());
         return SkinStorage.createProperty("textures", property.getValue(), property.getSignature());
      } catch (Exception var7) {
         return null;
      }
   }

   public static String getUUID(String name, boolean tryNext) throws MojangAPI.SkinRequestException {
      if (name == null) {
         return null;
      } else {
         try {
            UUID uuid = CommonsGeneral.getUUIDFetcher().getUUID(name);
            if (uuid != null) {
               return uuid.toString();
            }
         } catch (UUIDFetcherException var3) {
            var3.printStackTrace();
         }

         return null;
      }
   }

   public static String getUUID(String name) throws MojangAPI.SkinRequestException {
      return getUUID(name, true);
   }

   public static String getUUIDMojang(String name, boolean tryNext) throws MojangAPI.SkinRequestException {
      try {
         String output = readURL("https://api.mojang.com/users/profiles/minecraft/%name%".replace("%name%", name));
         if (output.isEmpty()) {
            throw new MojangAPI.SkinRequestException("not premium");
         } else {
            JsonElement element = (new JsonParser()).parse(output);
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("error")) {
               return tryNext ? getUUIDBackup(name) : null;
            } else {
               return obj.get("id").getAsString();
            }
         }
      } catch (IOException var5) {
         return tryNext ? getUUIDBackup(name) : null;
      }
   }

   public static String getUUIDMojang(String name) throws MojangAPI.SkinRequestException {
      return getUUIDMojang(name, true);
   }

   public static String getUUIDBackup(String name) throws MojangAPI.SkinRequestException {
      try {
         String output = readURL("https://api.ashcon.app/mojang/v2/user/%name%".replace("%name%", name), 10000);
         JsonElement element = (new JsonParser()).parse(output);
         JsonObject obj = element.getAsJsonObject();
         if (obj.has("code")) {
            if (obj.get("code").getAsInt() == 404) {
               throw new MojangAPI.SkinRequestException("not premium");
            } else {
               throw new MojangAPI.SkinRequestException("api falha");
            }
         } else {
            return obj.get("uuid").getAsString().replace("-", "");
         }
      } catch (IOException var4) {
         throw new MojangAPI.SkinRequestException(var4.getMessage());
      }
   }

   private static String readURL(String url) throws IOException {
      return readURL(url, 5000);
   }

   private static String readURL(String url, int timeout) throws IOException {
      HttpURLConnection con = (HttpURLConnection)(new URL(url)).openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", "SkinsRestorer");
      con.setConnectTimeout(timeout);
      con.setReadTimeout(timeout);
      con.setDoOutput(true);
      StringBuilder output = new StringBuilder();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

      String line;
      while((line = in.readLine()) != null) {
         output.append(line);
      }

      in.close();
      return output.toString();
   }

   private static class HTTPResponse {
      private String output;
      private int status;

      public String getOutput() {
         return this.output;
      }

      public void setOutput(String output) {
         this.output = output;
      }

      public int getStatus() {
         return this.status;
      }

      public void setStatus(int status) {
         this.status = status;
      }
   }

   private static class Property {
      private String name;
      private String value;
      private String signature;

      private Property() {
      }

      boolean valuesFromJson(JsonObject obj) {
         if (obj.has("properties")) {
            JsonArray properties = obj.getAsJsonArray("properties");
            JsonObject propertiesObject = properties.get(0).getAsJsonObject();
            String signature = propertiesObject.get("signature").getAsString();
            String value = propertiesObject.get("value").getAsString();
            this.setSignature(signature);
            this.setValue(value);
            return true;
         } else {
            return false;
         }
      }

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         this.name = name;
      }

      String getValue() {
         return this.value;
      }

      void setValue(String value) {
         this.value = value;
      }

      String getSignature() {
         return this.signature;
      }

      void setSignature(String signature) {
         this.signature = signature;
      }

      // $FF: synthetic method
      Property(Object x0) {
         this();
      }
   }

   public static class SkinRequestException extends Exception {
      private String reason;

      public SkinRequestException(String reason) {
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