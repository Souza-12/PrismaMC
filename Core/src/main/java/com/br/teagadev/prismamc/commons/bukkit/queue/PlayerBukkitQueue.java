package com.br.teagadev.prismamc.commons.bukkit.queue;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerQueueEvent;
import com.br.teagadev.prismamc.commons.bukkit.queue.player.PlayerQueue;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketAction;
import com.br.teagadev.servercommunication.client.Client;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class PlayerBukkitQueue {
   private Listener bukkitListener;
   private List<PlayerQueue> players;
   private boolean destroyOnFinish;
   private boolean stopOnFinish;
   private int ticks;
   private QueueType queueType;

   public PlayerBukkitQueue(int ticks, QueueType queueType) {
      this(ticks, true, queueType);
   }

   public PlayerBukkitQueue(int ticks, boolean destroyOnFinish, QueueType queueType) {
      this.players = new ArrayList();
      this.destroyOnFinish = destroyOnFinish;
      this.ticks = ticks;
      this.queueType = queueType;
   }

   public void start() {
      this.bukkitListener = new Listener() {
         @EventHandler
         public void onSecond(BukkitUpdateEvent event) {
            if (event.getType() == UpdateType.TICK) {
               if (event.getCurrentTick() % (long)PlayerBukkitQueue.this.ticks == 0L) {
                  if (PlayerBukkitQueue.this.getPlayers().size() == 0) {
                     if (PlayerBukkitQueue.this.isDestroyOnFinish()) {
                        PlayerBukkitQueue.this.destroy();
                     }

                  } else if (PlayerBukkitQueue.this.getPlayers().size() != 0) {
                     PlayerQueue current = (PlayerQueue)PlayerBukkitQueue.this.getPlayers().get(0);
                     if (current != null && current.getPlayer().isOnline()) {
                        PlayerBukkitQueue.this.getPlayers().remove(current);
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueEvent(current.getPlayer(), PlayerBukkitQueue.this.queueType, current.getServer()));
                        current.destroy();
                        current = null;
                     } else {
                        PlayerBukkitQueue.this.getPlayers().remove(current);
                        current.destroy();
                        current = null;
                     }
                  }
               }
            }
         }
      };
      Bukkit.getServer().getPluginManager().registerEvents(this.bukkitListener, BukkitMain.getInstance());
   }

   public void destroy() {
      if (this.bukkitListener != null) {
         HandlerList.unregisterAll(this.bukkitListener);
         this.bukkitListener = null;
      }

      if (this.isStopOnFinish()) {
         Client.getInstance().getClientConnection().sendPacket((new CPacketAction(BukkitMain.getServerType().getName(), BukkitMain.getServerID())).writeType("Loggout"));
         BukkitMain.runLater(() -> {
            Iterator var0 = Bukkit.getOnlinePlayers().iterator();

            while(var0.hasNext()) {
               Player onlines = (Player)var0.next();
               onlines.kickPlayer("§cNão foi possível conectar-se ao servidor.");
            }

            Bukkit.shutdown();
         }, 30L);
      }

   }

   public boolean addToQueue(Player player) {
      return this.addToQueue(player, "");
   }

   public boolean addToQueue(Player player, String server) {
      if (this.getQueuePlayer(player) != null) {
         return false;
      } else {
         this.getPlayers().add(new PlayerQueue(player, server));
         return true;
      }
   }

   public PlayerQueue getQueuePlayer(Player player) {
      return (PlayerQueue)this.players.stream().filter((qp) -> {
         return qp.player.equals(player);
      }).findFirst().orElse((PlayerQueue)null);
   }

   public Listener getBukkitListener() {
      return this.bukkitListener;
   }

   public List<PlayerQueue> getPlayers() {
      return this.players;
   }

   public boolean isDestroyOnFinish() {
      return this.destroyOnFinish;
   }

   public boolean isStopOnFinish() {
      return this.stopOnFinish;
   }

   public int getTicks() {
      return this.ticks;
   }

   public QueueType getQueueType() {
      return this.queueType;
   }

   public void setBukkitListener(Listener bukkitListener) {
      this.bukkitListener = bukkitListener;
   }

   public void setPlayers(List<PlayerQueue> players) {
      this.players = players;
   }

   public void setDestroyOnFinish(boolean destroyOnFinish) {
      this.destroyOnFinish = destroyOnFinish;
   }

   public void setStopOnFinish(boolean stopOnFinish) {
      this.stopOnFinish = stopOnFinish;
   }

   public void setTicks(int ticks) {
      this.ticks = ticks;
   }

   public void setQueueType(QueueType queueType) {
      this.queueType = queueType;
   }
}