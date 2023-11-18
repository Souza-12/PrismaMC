package com.br.teagadev.prismamc.commons.bukkit.custom.events.player;

import com.br.teagadev.prismamc.commons.common.tag.Tag;
import org.bukkit.entity.Player;

public class PlayerChangeTagEvent extends PlayerCancellableEvent {
   private final Tag oldTag;
   private final Tag newTag;
   private final boolean forced;

   public PlayerChangeTagEvent(Player p, Tag oldTag, Tag newTag, boolean forced) {
      super(p);
      this.oldTag = oldTag;
      this.newTag = newTag;
      this.forced = forced;
   }

   public Tag getOldTag() {
      return this.oldTag;
   }

   public Tag getNewTag() {
      return this.newTag;
   }

   public boolean isForced() {
      return this.forced;
   }
}