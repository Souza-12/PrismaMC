package com.br.teagadev.prismamc.commons.bukkit.api.bossbar;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BossBar {
   private Player player;
   private EntityWither wither;
   private boolean cancelar;
   private boolean tempo;
   private int segundos;

   public BossBar(Player player, String message, boolean tempo) {
      this.setPlayer(player);
      this.setCancelar(false);
      this.setTempo(tempo);
      this.setSegundos(20);
      this.setWither(new EntityWither(((CraftWorld)player.getWorld()).getHandle()));
      this.getWither().setInvisible(true);
      this.getWither().setCustomName(message);
      this.getWither().getEffects().clear();
      this.getWither().setLocation(this.getViableLocation().getX(), this.getViableLocation().getY(), this.getViableLocation().getZ(), 0.0F, 0.0F);
      ((CraftPlayer)this.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(this.getWither()));
   }

   public void onSecond() {
      if (this.isCancelar()) {
         this.destroy();
      } else if (!this.getPlayer().isOnline()) {
         this.setCancelar(true);
      } else if (!this.isTempo()) {
         this.update();
      } else if (this.getSegundos() == 0) {
         this.destroy();
         this.setCancelar(true);
      } else {
         --this.segundos;
         this.update();
      }
   }

   public void update() {
      if (this.wither != null && this.player != null) {
         this.wither.setLocation(this.getViableLocation().getX(), this.getViableLocation().getY(), this.getViableLocation().getZ(), 0.0F, 0.0F);
         ((CraftPlayer)this.player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(this.wither));
      }
   }

   public Location getViableLocation() {
      return this.player == null ? new Location(Bukkit.getWorld("world"), 0.0D, 0.0D, 0.0D) : this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().multiply(28));
   }

   public void destroy() {
      ((CraftPlayer)this.player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[]{this.wither.getId()}));
   }

   public Player getPlayer() {
      return this.player;
   }

   public EntityWither getWither() {
      return this.wither;
   }

   public boolean isCancelar() {
      return this.cancelar;
   }

   public boolean isTempo() {
      return this.tempo;
   }

   public int getSegundos() {
      return this.segundos;
   }

   public void setPlayer(Player player) {
      this.player = player;
   }

   public void setWither(EntityWither wither) {
      this.wither = wither;
   }

   public void setCancelar(boolean cancelar) {
      this.cancelar = cancelar;
   }

   public void setTempo(boolean tempo) {
      this.tempo = tempo;
   }

   public void setSegundos(int segundos) {
      this.segundos = segundos;
   }
}