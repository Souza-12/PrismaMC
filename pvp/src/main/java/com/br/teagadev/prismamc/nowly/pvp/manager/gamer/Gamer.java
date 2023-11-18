package com.br.teagadev.prismamc.nowly.pvp.manager.gamer;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Gamer {
   private UUID uniqueId;
   private boolean protection;
   private Player player;
   private String kit1;
   private String kit2;

   public Gamer(UUID uniqueId) {
      this.setUniqueId(uniqueId);
      this.setProtection(true);
      this.setKit1("Nenhum");
      this.setKit2("Nenhum");
   }

   public Player getPlayer() {
      if (this.player == null) {
         this.player = Bukkit.getPlayer(this.getUniqueId());
      }

      return this.player;
   }

   public void removeProtection(Player player, boolean message) {
      if (this.isProtection()) {
         this.setProtection(false);
         player.closeInventory();
         if (message) {
            player.sendMessage("§cVocê perdeu a proteção do spawn.");
         }

      }
   }

   public boolean containsKit(String name) {
      return this.getKit1().equalsIgnoreCase(name) || this.getKit2().equalsIgnoreCase(name);
   }

   public void joinArena() {
      if (this.getKit1().equalsIgnoreCase("Nenhum")) {
         this.setKit1("PvP");
      }

      if (this.getKit2().equalsIgnoreCase("Nenhum")) {
         this.setKit2("PvP");
      }

      this.removeProtection(this.getPlayer(), true);
   }

   public String getKits() {
      return this.getKit1() + " e " + this.getKit2();
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public boolean isProtection() {
      return this.protection;
   }

   public String getKit1() {
      return this.kit1;
   }

   public String getKit2() {
      return this.kit2;
   }

   public void setUniqueId(UUID uniqueId) {
      this.uniqueId = uniqueId;
   }

   public void setProtection(boolean protection) {
      this.protection = protection;
   }

   public void setPlayer(Player player) {
      this.player = player;
   }

   public void setKit1(String kit1) {
      this.kit1 = kit1;
   }

   public void setKit2(String kit2) {
      this.kit2 = kit2;
   }
}