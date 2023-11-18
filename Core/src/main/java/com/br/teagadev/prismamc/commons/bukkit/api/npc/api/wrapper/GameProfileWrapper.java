package com.br.teagadev.prismamc.commons.bukkit.api.npc.api.wrapper;

import com.br.teagadev.prismamc.commons.bukkit.utility.Reflection;
import com.br.teagadev.prismamc.commons.bukkit.utility.Reflection.FieldAccessor;
import com.br.teagadev.prismamc.commons.common.utility.skin.Skin;
import com.google.common.collect.ForwardingMultimap;
import java.util.UUID;
import org.bukkit.Bukkit;

public class GameProfileWrapper {
   private final boolean is1_7 = Bukkit.getBukkitVersion().contains("1.7");
   private final Class<?> gameProfileClazz;
   final Object gameProfile;

   public GameProfileWrapper(UUID uuid, String name) {
      this.gameProfileClazz = Reflection.getClass((this.is1_7 ? "net.minecraft.util." : "") + "com.mojang.authlib.GameProfile");
      this.gameProfile = Reflection.getConstructor(this.gameProfileClazz, new Class[]{UUID.class, String.class}).invoke(new Object[]{uuid, name});
   }

   public void addSkin(Skin skin) {
      Class<?> propertyClazz = Reflection.getClass((this.is1_7 ? "net.minecraft.util." : "") + "com.mojang.authlib.properties.Property");
      Object property = Reflection.getConstructor(propertyClazz, new Class[]{String.class, String.class, String.class}).invoke(new Object[]{"textures", skin.getValue(), skin.getSignature()});
      Class<?> propertyMapClazz = Reflection.getClass((this.is1_7 ? "net.minecraft.util." : "") + "com.mojang.authlib.properties.PropertyMap");
      FieldAccessor propertyMapGetter = Reflection.getField(this.gameProfileClazz, "properties", propertyMapClazz);
      Object propertyMap = propertyMapGetter.get(this.gameProfile);
      Reflection.getMethod(ForwardingMultimap.class, "put", new Class[]{Object.class, Object.class}).invoke(propertyMap, new Object[]{"textures", property});
      propertyMapGetter.set(this.gameProfile, propertyMap);
   }

   public Object getGameProfile() {
      return this.gameProfile;
   }
}