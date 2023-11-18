package com.br.teagadev.prismamc.commons.bukkit.api;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.client.Client;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitServerAPI {
   private static boolean registeredServer = false;

   public static void registerServer() {
      registeredServer = true;
      Client.getInstance().getClientConnection().sendPacket((new CPacketCustomAction(BukkitMain.getServerType(), BukkitMain.getServerID())).type(PacketType.BUKKIT_SEND_INFO).field("bukkit-register-server").fieldValue(Bukkit.getServer().getIp()).extraValue("" + Bukkit.getServer().getPort()));
   }

   public static void redirectPlayer(Player player, String server) {
      redirectPlayer(player, server, false);
   }

   public static boolean checkItem(ItemStack item, String display) {
      return item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().startsWith(display);
   }

   public static void redirectPlayer(Player player, String server, boolean kick) {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("Connect");
      out.writeUTF(server);
      player.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());
      player.sendMessage("§aConectando...");
      if (kick) {
         BukkitMain.runLater(() -> {
            if (player.isOnline()) {
               if (!server.equalsIgnoreCase("LobbyPvP") && !server.equalsIgnoreCase("LobbyHardcoreGames")) {
                  player.kickPlayer("§cOcorreu um erro ao tentar conectar-se ao servidor: " + server);
               } else {
                  player.sendMessage("§cOcorreu um erro ao tentar conectar-se ao Servidor: §7" + server);
                  redirectPlayer(player, "Lobby", true);
               }
            }

         }, 40L);
      }

   }

   public static void unregisterCommands(String... commands) {
      try {
         Field firstField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
         firstField.setAccessible(true);
         CommandMap commandMap = (CommandMap)firstField.get(Bukkit.getServer());
         Field secondField = commandMap.getClass().getDeclaredField("knownCommands");
         secondField.setAccessible(true);
         HashMap<String, Command> knownCommands = (HashMap)secondField.get(commandMap);
         String[] var5 = commands;
         int var6 = commands.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String command = var5[var7];
            if (knownCommands.containsKey(command)) {
               knownCommands.remove(command);
               List<String> aliases = new ArrayList();
               Iterator var10 = knownCommands.keySet().iterator();

               String key;
               while(var10.hasNext()) {
                  key = (String)var10.next();
                  if (key.contains(":")) {
                     String substr = key.substring(key.indexOf(":") + 1);
                     if (substr.equalsIgnoreCase(command)) {
                        aliases.add(key);
                     }
                  }
               }

               var10 = aliases.iterator();

               while(var10.hasNext()) {
                  key = (String)var10.next();
                  knownCommands.remove(key);
               }
            }
         }
      } catch (Exception var13) {
         var13.printStackTrace();
      }

   }

   public static void removePlayerFile(UUID uuid) {
      World world = (World)Bukkit.getWorlds().get(0);
      File folder = new File(world.getWorldFolder(), "playerdata");
      if (folder.exists() && folder.isDirectory()) {
         File file = new File(folder, uuid.toString() + ".dat");
         Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitMain.getInstance(), () -> {
            if (file.exists() && !file.delete()) {
               removePlayerFile(uuid);
            }

         }, 2L);
      }

   }

   public static void warnStaff(String message, Groups tag) {
      Iterator var2 = CommonsGeneral.getProfileManager().getGamingProfiles().iterator();

      while(var2.hasNext()) {
         GamingProfile profiles = (GamingProfile)var2.next();
         if (profiles.getGroup().getLevel() >= tag.getLevel()) {
            Player target = Bukkit.getPlayer(profiles.getUniqueId());
            if (target != null) {
               target.sendMessage(message);
            }
         }
      }

   }

   public static Player getExactPlayerByNick(String nick) {
      Player finded = null;
      Iterator var2 = CommonsGeneral.getProfileManager().getGamingProfiles().iterator();

      while(var2.hasNext()) {
         GamingProfile profiles = (GamingProfile)var2.next();
         if (profiles.getNick().equalsIgnoreCase(nick)) {
            finded = Bukkit.getPlayer(profiles.getUniqueId());
            break;
         }
      }

      return finded;
   }

   public static String getRealNick(Player target) {
      return CommonsGeneral.getProfileManager().getGamingProfile(target.getUniqueId()).getNick();
   }

   public static boolean isRegisteredServer() {
      return registeredServer;
   }
}