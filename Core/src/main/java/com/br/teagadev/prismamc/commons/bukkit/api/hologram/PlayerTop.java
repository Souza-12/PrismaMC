package com.br.teagadev.prismamc.commons.bukkit.api.hologram;

public class PlayerTop implements Comparable<PlayerTop> {
   private final String playerName;
   private final int value;

   public PlayerTop(String playerName, int value) {
      this.playerName = playerName;
      this.value = value;
   }

   public int compareTo(PlayerTop o) {
      return o.getValue() - this.getValue();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof PlayerTop)) {
         return false;
      } else {
         PlayerTop topObj = (PlayerTop)obj;
         return topObj.getPlayerName().equals(this.playerName) && topObj.getValue() == this.value;
      }
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public int getValue() {
      return this.value;
   }

   // $FF: synthetic method
   // $FF: bridge method

}