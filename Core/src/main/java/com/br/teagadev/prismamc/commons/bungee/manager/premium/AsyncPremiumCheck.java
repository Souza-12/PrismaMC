package com.br.teagadev.prismamc.commons.bungee.manager.premium;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;

public class AsyncPremiumCheck implements Runnable {
   private final PendingConnection connection;
   private final PreLoginEvent preLoginEvent;

   public AsyncPremiumCheck(PreLoginEvent event, PendingConnection connection) {
      this.preLoginEvent = event;
      this.connection = connection;
   }

   public void run() {
      try {
         if (this.connection.isConnected()) {
            try {
               PremiumMapManager.load(this.connection.getName());
            } catch (UUIDFetcherException var5) {
               this.preLoginEvent.setCancelled(true);
               this.preLoginEvent.setCancelReason("§cNão foi possivel verificar sua conta, tente entrar novamente.");
            }

            if (this.preLoginEvent.isCancelled()) {
               return;
            }

            PremiumMap data = PremiumMapManager.getPremiumMap(this.connection.getName());
            if (data != null) {
               this.connection.setOnlineMode(data.isPremium());
            }

            if (PremiumMapManager.getChangedNicks().contains(this.connection.getName())) {
               PremiumMapManager.getChangedNicks().remove(this.connection.getName());
               this.preLoginEvent.setCancelled(true);
               this.preLoginEvent.setCancelReason("§cDetectamos uma alteração em seu nickname, espere até que os dados sejam transferidos.");
            }

            return;
         }
      } finally {
         this.preLoginEvent.completeIntent(BungeeMain.getInstance());
      }

   }
}