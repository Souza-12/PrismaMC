package com.br.teagadev.servercommunication.common.update;

import java.util.concurrent.ConcurrentLinkedQueue;

public class UpdateListener implements Runnable {
   private long seconds;
   private final ConcurrentLinkedQueue<UpdateListener.UpdateEvent> list = new ConcurrentLinkedQueue();
   private boolean running;

   public void register(UpdateListener.UpdateEvent updateEvent) {
      this.list.add(updateEvent);
   }

   public void unregister(UpdateListener.UpdateEvent updateEvent) {
      this.list.remove(updateEvent);
   }

   public void run() {
      this.running = true;

      while(this.running) {
         try {
            Thread.sleep(1000L);
         } catch (Exception var2) {
         }

         ++this.seconds;
         this.call(UpdateListener.UpdateType.SECOND);
         if (this.seconds % 60L == 0L) {
            this.call(UpdateListener.UpdateType.MINUTE);
         }
      }

   }

   private void call(UpdateListener.UpdateType updateEvent) {
      this.list.forEach((update) -> {
         update.onUpdate(updateEvent);
      });
   }

   public interface UpdateEvent {
      void onUpdate(UpdateListener.UpdateType var1);
   }

   public static enum UpdateType {
      SECOND,
      MINUTE;
   }
}