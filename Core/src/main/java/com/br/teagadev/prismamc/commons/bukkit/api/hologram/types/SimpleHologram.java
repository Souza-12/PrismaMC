package com.br.teagadev.prismamc.commons.bukkit.api.hologram.types;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.Hologram;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.HologramAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.PlayerTop;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.view.ViewHandler;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class SimpleHologram {
   private String name;
   private String display;
   private DataCategory dataCategory;
   private DataType dataType;
   private List<PlayerTop> topList;
   private final ViewHandler defaultViewHandler = (hologram, player, string) -> {
      int topId = Integer.parseInt(hologram.getName().replace("top", "").replace("-", "").replace(this.getName(), ""));
      PlayerTop playerTop = (PlayerTop)this.getTopList().get(topId - 1);
      return string.replace("%position%", "" + topId).replace("%playerName%", playerTop.getPlayerName()).replace("%value%", "" + playerTop.getValue());
   };
   private boolean created;
   private boolean updating;
   private Plugin instance;

   public SimpleHologram(String name, String display, DataCategory dataCategory, DataType dataType, Plugin instance) {
      this.name = name;
      this.display = display;
      this.dataCategory = dataCategory;
      this.dataType = dataType;
      this.instance = instance;
      this.created = false;
      this.updating = false;
      this.topList = new ArrayList();
   }

   public void create() {
      this.create("world");
   }

   public void recreate() {
      if (this.isCreated()) {
         Iterator var1 = HologramAPI.getHolograms().iterator();

         label32:
         while(true) {
            while(true) {
               if (!var1.hasNext()) {
                  break label32;
               }

               Hologram holograms = (Hologram)var1.next();
               String holoName = holograms.getName().toLowerCase();
               if (holoName.equalsIgnoreCase("titulo-" + this.getName().toLowerCase())) {
                  holograms.update();
               } else if (holoName.startsWith("top") && holoName.contains(this.getName().toLowerCase()) && holoName.contains("-")) {
                  holograms.update();
               } else if (holoName.contains("blank") && holoName.contains(this.getName().toLowerCase())) {
                  holograms.update();
               }
            }
         }
      }

      this.create();
   }

   public void create(String worldName) {
      if (!this.isCreated()) {
         PluginConfiguration.createLocation(this.getInstance(), "hologramas." + this.getName().toLowerCase(), worldName);
         Location location = PluginConfiguration.getLocation(this.getInstance(), "hologramas." + this.getName().toLowerCase());

         assert location != null;

         World world = location.getWorld();
         double y = location.getY();
         Hologram title = HologramAPI.createHologram("titulo-" + this.getName().toLowerCase(), location, "§e§lTOP 10 §b§l" + this.getDisplay().toUpperCase() + "§7(1/1)");
         y = y -= 0.25D;
         title.spawn();
         title.addLineBelow("blank-" + this.getName().toLowerCase(), "");
         y = y -= 0.25D;
         Hologram top1 = HologramAPI.createHologram("top1-" + this.getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
         top1.addViewHandler(this.defaultViewHandler);
         top1.spawn();
         y = y -= 0.25D;
         Hologram top2 = HologramAPI.createHologram("top2-" + this.getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
         top2.addViewHandler(this.defaultViewHandler);
         top2.spawn();
         y = y -= 0.25D;
         Hologram top3 = HologramAPI.createHologram("top3-" + this.getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
         top3.addViewHandler(this.defaultViewHandler);
         top3.spawn();
         y = y -= 0.25D;
         Hologram top4 = HologramAPI.createHologram("top4-" + this.getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
         top4.addViewHandler(this.defaultViewHandler);
         top4.spawn();
         y = y -= 0.25D;
         Hologram top5 = HologramAPI.createHologram("top5-" + this.getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
         top5.addViewHandler(this.defaultViewHandler);
         top5.spawn();
         y = y -= 0.25D;
         Hologram top6 = HologramAPI.createHologram("top6-" + this.getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
         top6.addViewHandler(this.defaultViewHandler);
         top6.spawn();
         y = y -= 0.25D;
         Hologram top7 = HologramAPI.createHologram("top7-" + this.getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
         top7.addViewHandler(this.defaultViewHandler);
         top7.spawn();
         y = y -= 0.25D;
         Hologram top8 = HologramAPI.createHologram("top8-" + this.getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
         top8.addViewHandler(this.defaultViewHandler);
         top8.spawn();
         y = y -= 0.25D;
         Hologram top9 = HologramAPI.createHologram("top9-" + this.getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
         top9.addViewHandler(this.defaultViewHandler);
         top9.spawn();
         y = y -= 0.25D;
         Hologram top10 = HologramAPI.createHologram("top10-" + this.getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
         top10.addViewHandler(this.defaultViewHandler);
         top10.spawn();
         this.setCreated(true);
      }
   }

   public void updateValues() {
      this.updating = true;
      (new Thread(() -> {
         List<PlayerTop> updated = new ArrayList();
         String fieldReference = "data>'$.\"" + this.getDataType().getField() + "\"'";

         try {
            Connection connection = CommonsGeneral.getMySQL().getConnection();
            Throwable var4 = null;

            try {
               PreparedStatement preparedStatament = connection.prepareStatement("SELECT nick,data FROM " + this.getDataCategory().getTableName() + " ORDER BY " + fieldReference + " DESC");
               ResultSet result = preparedStatament.executeQuery();
               int id = 0;

               while(result.next()) {
                  ++id;
                  String playerName = result.getString("nick");
                  UUID uniqueId = CommonsGeneral.getUUIDFetcher().getOfflineUUID(playerName);
                  GamingProfile profile = CommonsGeneral.getProfileManager().getGamingProfile(uniqueId);
                  if (profile == null) {
                     profile = new GamingProfile(playerName, uniqueId);
                     if (this.getDataCategory() != DataCategory.ACCOUNT && !profile.getDataHandler().isCategoryLoaded(DataCategory.ACCOUNT)) {
                        profile.getDataHandler().load(new DataCategory[]{DataCategory.ACCOUNT});
                     }

                     profile.getDataHandler().loadFromJSONString(this.getDataCategory(), result.getString("data"));
                  }

                  Groups group = profile.getGroup();
                  PlayerTop top = new PlayerTop(group.getTag().getColor() + playerName, profile.getInt(this.getDataType()));
                  if (updated.isEmpty()) {
                     updated.add(top);
                  } else if (!((PlayerTop)updated.get(updated.size() - 1)).getPlayerName().equals(top.getPlayerName())) {
                     updated.add(top);
                  }
               }

               result.close();
               preparedStatament.close();

               while(id < 10) {
                  ++id;
                  updated.add(new PlayerTop("§7Ninguém", 0));
               }

               this.topList.clear();
               updated.sort(PlayerTop::compareTo);
               this.topList.addAll(updated.subList(0, 10));
               this.recreate();
               updated.clear();
               this.updating = false;
            } catch (Throwable var21) {
               var4 = var21;
               throw var21;
            } finally {
               if (connection != null) {
                  if (var4 != null) {
                     try {
                        connection.close();
                     } catch (Throwable var20) {
                        var4.addSuppressed(var20);
                     }
                  } else {
                     connection.close();
                  }
               }

            }
         } catch (SQLException var23) {
            BukkitMain.console("§cOcorreu um erro ao tentar atualizar o TOP 10 '" + this.getName().toUpperCase() + "' -> " + var23.getLocalizedMessage());
            var23.printStackTrace();
         }

      })).start();
   }

   public String getName() {
      return this.name;
   }

   public String getDisplay() {
      return this.display;
   }

   public DataCategory getDataCategory() {
      return this.dataCategory;
   }

   public DataType getDataType() {
      return this.dataType;
   }

   public List<PlayerTop> getTopList() {
      return this.topList;
   }

   public ViewHandler getDefaultViewHandler() {
      return this.defaultViewHandler;
   }

   public boolean isCreated() {
      return this.created;
   }

   public boolean isUpdating() {
      return this.updating;
   }

   public Plugin getInstance() {
      return this.instance;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDisplay(String display) {
      this.display = display;
   }

   public void setDataCategory(DataCategory dataCategory) {
      this.dataCategory = dataCategory;
   }

   public void setDataType(DataType dataType) {
      this.dataType = dataType;
   }

   public void setTopList(List<PlayerTop> topList) {
      this.topList = topList;
   }

   public void setCreated(boolean created) {
      this.created = created;
   }

   public void setUpdating(boolean updating) {
      this.updating = updating;
   }

   public void setInstance(Plugin instance) {
      this.instance = instance;
   }
}