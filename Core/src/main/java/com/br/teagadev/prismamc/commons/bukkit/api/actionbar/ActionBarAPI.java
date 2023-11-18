package com.br.teagadev.prismamc.commons.bukkit.api.actionbar;

import java.util.Iterator;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBarAPI {
   public static void send(Player player, String message) {
      PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\": \"" + message + "\"}"), (byte)2);
      ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
   }

   public static void broadcast(String message) {
      PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\": \"" + message + "\"}"), (byte)2);
      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player onlines = (Player)var2.next();
         ((CraftPlayer)onlines).getHandle().playerConnection.sendPacket(packet);
      }

   }
}