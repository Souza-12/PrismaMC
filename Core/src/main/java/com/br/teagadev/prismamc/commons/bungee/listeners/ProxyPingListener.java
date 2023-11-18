package com.br.teagadev.prismamc.commons.bungee.listeners;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.events.MotdCentralizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyPingListener implements Listener {
   private List<String> motds = Arrays.asList("§eData de Abertura: 25/10/2023", "§eData de Abertura: 25/10/2023");
   public static HashMap<String, Integer> ping = new HashMap();

   @EventHandler(
      priority = -64
   )
   public void onProxyPing(ProxyPingEvent event) {
      String ipAddress = event.getConnection().getAddress().getHostString();
      if (ping.containsKey(ipAddress)) {
         ping.put(ipAddress, (Integer)ping.get(ipAddress) + 1);
      } else {
         ping.put(ipAddress, 1);
      }

      if ((Integer)ping.get(ipAddress) > 20) {
         event.setResponse((ServerPing)null);
      } else if (!BungeeMain.getManager().isGlobalWhitelist()) {
         String motd = (String)this.motds.get((new Random()).nextInt(this.motds.size() - 1));
         event.setResponse(new ServerPing(event.getResponse().getVersion(), new Players(2023, BungeeCord.getInstance().getOnlineCount(), new PlayerInfo[]{new PlayerInfo("", "")}), MotdCentralizer.makeCenteredMotd("§6§lCRAZZY§f§lMC §a➟ §7loja.crazzymc.net") + "\n" + MotdCentralizer.makeCenteredMotd(motd), event.getResponse().getFaviconObject()));
      } else {
         event.setResponse(new ServerPing(event.getResponse().getVersion(), new Players(1, BungeeCord.getInstance().getOnlineCount(), new PlayerInfo[]{new PlayerInfo("", "")}), MotdCentralizer.makeCenteredMotd("§6§lCRAZZY§f§lMC §a➟ §7loja.crazzymc.net") + "\n" + MotdCentralizer.makeCenteredMotd("§cServidores em manutenção"), event.getResponse().getFaviconObject()));
      }

   }
}