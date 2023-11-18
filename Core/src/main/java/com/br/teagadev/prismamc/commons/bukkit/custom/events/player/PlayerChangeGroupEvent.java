package com.br.teagadev.prismamc.commons.bukkit.custom.events.player;

import com.br.teagadev.prismamc.commons.common.group.Groups;
import org.bukkit.entity.Player;

public class PlayerChangeGroupEvent extends PlayerEvent {
   private Groups group;

   public PlayerChangeGroupEvent(Player player, Groups group) {
      super(player);
      this.setGroup(group);
   }

   public Groups getGroup() {
      return this.group;
   }

   public void setGroup(Groups group) {
      this.group = group;
   }
}