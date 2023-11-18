package com.br.teagadev.prismamc.commons.bukkit.api.npc.api;

import com.br.teagadev.prismamc.commons.bukkit.api.hologram.Hologram;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.HologramAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.NPCManager;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.api.wrapper.GameProfileWrapper;
import com.br.teagadev.prismamc.commons.common.utility.skin.Skin;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class NPC implements PacketHandler {
   protected final UUID uuid = UUID.randomUUID();
   protected final int entityId = Integer.MAX_VALUE - NPCManager.getAllNPCs().size();
   protected final Skin skin;
   private final Set<UUID> shown = new HashSet();
   protected int itemStackID = 0;
   protected final String customName;
   protected final String color;
   protected final String name;
   protected final JavaPlugin plugin;
   protected GameProfileWrapper gameProfile;
   protected Location location;
   protected Hologram nameHologram;

   public NPC(JavaPlugin plugin, String customName, String color, String name, Skin skin, int itemStackID) {
      this.plugin = plugin;
      this.skin = skin;
      this.name = name;
      this.customName = customName;
      this.color = color;
      this.itemStackID = itemStackID;
      NPCManager.add(this);
   }

   protected GameProfileWrapper generateGameProfile(UUID uuid, String name) {
      GameProfileWrapper gameProfile = new GameProfileWrapper(uuid, name);
      if (this.skin != null) {
         gameProfile.addSkin(this.skin);
      }

      return gameProfile;
   }

   public String getCustomName() {
      return this.customName;
   }

   public void destroy() {
      NPCManager.remove(this);
      Iterator var1 = this.shown.iterator();

      while(var1.hasNext()) {
         UUID uuid = (UUID)var1.next();
         this.hide(Bukkit.getPlayer(uuid));
      }

      this.nameHologram.despawn();
   }

   public void hideAll() {
      Iterator var1 = this.shown.iterator();

      while(var1.hasNext()) {
         UUID uuid = (UUID)var1.next();
         this.hide(Bukkit.getPlayer(uuid));
      }

   }

   public Set<UUID> getShown() {
      return this.shown;
   }

   public Location getLocation() {
      return this.location;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public void create(Location location) {
      this.location = location;
      this.nameHologram = HologramAPI.createHologram("name", location.clone().add(0.0D, 2.095D, 0.0D), (String)null);
      this.nameHologram.spawn();
      this.createPackets();
   }

   public Location getLocationForHologram() {
      return this.location.clone().add(0.0D, 2.05D, 0.0D);
   }

   public void show(Player player) {
      if (!this.shown.contains(player.getUniqueId())) {
         this.sendShowPackets(player);
      }
   }

   public void hide(Player player) {
      if (this.shown.contains(player.getUniqueId())) {
         this.sendHidePackets(player, false);
      }
   }

   public Hologram getNameHologram() {
      return this.nameHologram;
   }
}