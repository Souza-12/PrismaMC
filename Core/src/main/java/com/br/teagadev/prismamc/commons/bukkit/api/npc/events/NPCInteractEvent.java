package com.br.teagadev.prismamc.commons.bukkit.api.npc.events;

import com.br.teagadev.prismamc.commons.bukkit.api.npc.api.NPC;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.events.click.ClickType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {
   private final Player player;
   private final ClickType clickType;
   private final NPC npc;
   private static final HandlerList handlers = new HandlerList();

   public NPCInteractEvent(Player player, ClickType clickType, NPC npc) {
      this.player = player;
      this.clickType = clickType;
      this.npc = npc;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public Player getPlayer() {
      return this.player;
   }

   public ClickType getClickType() {
      return this.clickType;
   }

   public NPC getNpc() {
      return this.npc;
   }
}