package com.br.teagadev.prismamc.commons.bukkit.api.npc.packets;

import com.br.teagadev.prismamc.commons.bukkit.api.npc.api.NPC;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.packets.packets.PacketPlayOutEntityHeadRotationWrapper;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.packets.packets.PacketPlayOutNamedEntitySpawnWrapper;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.packets.packets.PacketPlayOutPlayerInfoWrapper;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.packets.packets.PacketPlayOutScoreboardTeamWrapper;
import com.br.teagadev.prismamc.commons.common.utility.skin.Skin;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NPC_v1_8_R3 extends NPC {
   private PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn;
   private PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeamRegister;
   private PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeamUnregister;
   private PacketPlayOutPlayerInfo packetPlayOutPlayerInfoAdd;
   private PacketPlayOutPlayerInfo packetPlayOutPlayerInfoRemove;
   private PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation;
   private PacketPlayOutEntityDestroy packetPlayOutEntityDestroy;

   public NPC_v1_8_R3(JavaPlugin plugin, String customName, String color, String name, Skin skin, int itemStackID) {
      super(plugin, customName, color, name, skin, itemStackID);
   }

   public void createPackets() {
      this.gameProfile = this.generateGameProfile(this.uuid, this.name);
      PacketPlayOutPlayerInfoWrapper packetPlayOutPlayerInfoWrapper = new PacketPlayOutPlayerInfoWrapper();
      this.packetPlayOutScoreboardTeamRegister = (new PacketPlayOutScoreboardTeamWrapper()).createRegisterTeam(this.name, this.color);
      this.packetPlayOutPlayerInfoAdd = packetPlayOutPlayerInfoWrapper.create(EnumPlayerInfoAction.ADD_PLAYER, this.gameProfile, this.name);
      this.packetPlayOutNamedEntitySpawn = (new PacketPlayOutNamedEntitySpawnWrapper()).create(this.uuid, this.location, this.entityId, this.itemStackID);
      this.packetPlayOutEntityHeadRotation = (new PacketPlayOutEntityHeadRotationWrapper()).create(this.location, this.entityId);
      this.packetPlayOutPlayerInfoRemove = packetPlayOutPlayerInfoWrapper.create(EnumPlayerInfoAction.REMOVE_PLAYER, this.gameProfile, this.name);
      this.packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(new int[]{this.entityId});
      this.packetPlayOutScoreboardTeamUnregister = (new PacketPlayOutScoreboardTeamWrapper()).createUnregisterTeam(this.name, this.color);
   }

   public void sendShowPackets(Player player) {
      PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
      playerConnection.sendPacket(this.packetPlayOutScoreboardTeamRegister);
      playerConnection.sendPacket(this.packetPlayOutPlayerInfoAdd);
      playerConnection.sendPacket(this.packetPlayOutNamedEntitySpawn);
      playerConnection.sendPacket(this.packetPlayOutEntityHeadRotation);
      Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
         playerConnection.sendPacket(this.packetPlayOutPlayerInfoRemove);
      }, 40L);
   }

   public void sendHidePackets(Player player, boolean scheduler) {
      PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
      playerConnection.sendPacket(this.packetPlayOutEntityDestroy);
      playerConnection.sendPacket(this.packetPlayOutPlayerInfoRemove);
      if (scheduler) {
         Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            playerConnection.sendPacket(this.packetPlayOutScoreboardTeamUnregister);
         }, 5L);
      } else {
         playerConnection.sendPacket(this.packetPlayOutScoreboardTeamUnregister);
      }

   }
}