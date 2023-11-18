package com.br.teagadev.prismamc.commons.bukkit.api.npc.packets.packets;

import com.br.teagadev.prismamc.commons.bukkit.utility.Reflection;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import org.bukkit.Location;

public class PacketPlayOutEntityHeadRotationWrapper {
   public PacketPlayOutEntityHeadRotation create(Location location, int entityId) {
      PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotation();
      Reflection.getField(packetPlayOutEntityHeadRotation.getClass(), "a", Integer.TYPE).set(packetPlayOutEntityHeadRotation, entityId);
      Reflection.getField(packetPlayOutEntityHeadRotation.getClass(), "b", Byte.TYPE).set(packetPlayOutEntityHeadRotation, (byte)((int)((float)((int)location.getYaw()) * 256.0F / 360.0F)));
      return packetPlayOutEntityHeadRotation;
   }
}