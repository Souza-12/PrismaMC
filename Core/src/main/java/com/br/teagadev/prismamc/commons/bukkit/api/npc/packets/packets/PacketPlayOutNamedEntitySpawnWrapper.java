package com.br.teagadev.prismamc.commons.bukkit.api.npc.packets.packets;

import com.br.teagadev.prismamc.commons.bukkit.utility.Reflection;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Location;

public class PacketPlayOutNamedEntitySpawnWrapper {
   public PacketPlayOutNamedEntitySpawn create(UUID uuid, Location location, int entityId, int itemStackID) {
      PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn();
      Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "a", Integer.TYPE).set(packetPlayOutNamedEntitySpawn, entityId);
      Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "b", UUID.class).set(packetPlayOutNamedEntitySpawn, uuid);
      Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "c", Integer.TYPE).set(packetPlayOutNamedEntitySpawn, (int)Math.floor(location.getX() * 32.0D));
      Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "d", Integer.TYPE).set(packetPlayOutNamedEntitySpawn, (int)Math.floor(location.getY() * 32.0D));
      Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "e", Integer.TYPE).set(packetPlayOutNamedEntitySpawn, (int)Math.floor(location.getZ() * 32.0D));
      Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "f", Byte.TYPE).set(packetPlayOutNamedEntitySpawn, (byte)((int)(location.getYaw() * 256.0F / 360.0F)));
      Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "g", Byte.TYPE).set(packetPlayOutNamedEntitySpawn, (byte)((int)(location.getPitch() * 256.0F / 360.0F)));
      Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "h", Integer.TYPE).set(packetPlayOutNamedEntitySpawn, itemStackID);
      DataWatcher dataWatcher = new DataWatcher((Entity)null);
      dataWatcher.a(10, (byte)127);
      Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "i", DataWatcher.class).set(packetPlayOutNamedEntitySpawn, dataWatcher);
      return packetPlayOutNamedEntitySpawn;
   }
}