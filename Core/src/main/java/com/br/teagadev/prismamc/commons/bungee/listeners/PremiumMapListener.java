package com.br.teagadev.prismamc.commons.bungee.listeners;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.BungeeMessages;
import com.br.teagadev.prismamc.commons.bungee.manager.premium.AsyncPremiumCheck;
import com.br.teagadev.prismamc.commons.bungee.manager.premium.PremiumMap;
import com.br.teagadev.prismamc.commons.bungee.manager.premium.PremiumMapManager;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PremiumMapListener implements Listener {
   @EventHandler
   public void onPreLogin(PreLoginEvent event) {
      if (!event.isCancelled()) {
         if (!BungeeMain.isLoaded()) {
            event.setCancelled(true);
            event.setCancelReason("§cO servidor está carregando, aguarde para poder entrar.");
         } else if (!CommonsGeneral.getServersManager().getNetworkServer("Lobby").isOnline()) {
            event.setCancelReason("§cNão foi possivel conectar-se a uma sala LB.".replace("%servidor%", "Lobby"));
            event.setCancelled(true);
         } else {
            String name = event.getConnection().getName();
            if (name.length() >= 3 && name.length() <= 16 && event.getConnection().getName().matches("^\\w*$")) {
               PremiumMap data = PremiumMapManager.getPremiumMap(event.getConnection().getName());
               if (data != null) {
                  event.getConnection().setOnlineMode(data.isPremium());
               } else {
                  event.registerIntent(BungeeMain.getInstance());
                  AsyncPremiumCheck asyncPremiumCheck = new AsyncPremiumCheck(event, event.getConnection());
                  BungeeMain.runAsync(asyncPremiumCheck);
               }

            } else {
               event.setCancelled(true);
               event.setCancelReason(BungeeMessages.INVALID_NICKNAME);
            }
         }
      }
   }
}