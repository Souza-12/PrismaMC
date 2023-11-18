package com.br.teagadev.prismamc.commons.bukkit.custom.injector;

import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

public class PacketObject {
   private boolean cancelled;
   private Player player;
   private Channel channel;
   private Packet packet;

   public PacketObject(Player player, Channel channel, Packet packet) {
      this.player = player;
      this.channel = channel;
      this.packet = packet;
      this.cancelled = false;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Channel getChannel() {
      return this.channel;
   }

   public Packet getPacket() {
      return this.packet;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public void setPlayer(Player player) {
      this.player = player;
   }

   public void setChannel(Channel channel) {
      this.channel = channel;
   }

   public void setPacket(Packet packet) {
      this.packet = packet;
   }
}