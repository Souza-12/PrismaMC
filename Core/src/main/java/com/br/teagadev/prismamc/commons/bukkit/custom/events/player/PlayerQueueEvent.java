package com.br.teagadev.prismamc.commons.bukkit.custom.events.player;

import com.br.teagadev.prismamc.commons.bukkit.queue.QueueType;
import org.bukkit.entity.Player;

public class PlayerQueueEvent extends PlayerEvent {
   private QueueType queueType;
   private String response;

   public PlayerQueueEvent(Player player, QueueType queueType) {
      this(player, queueType, "");
   }

   public PlayerQueueEvent(Player player, QueueType queueType, String response) {
      super(player);
      this.queueType = queueType;
      this.response = response;
   }

   public QueueType getQueueType() {
      return this.queueType;
   }

   public String getResponse() {
      return this.response;
   }

   public void setQueueType(QueueType queueType) {
      this.queueType = queueType;
   }

   public void setResponse(String response) {
      this.response = response;
   }
}