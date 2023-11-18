package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.bungee.manager.premium.PremiumMapManager;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GlobalListCommand implements CommandClass {
   @Command(
      name = "globallist",
      aliases = {"glist", "gl"},
      groupsToUse = {Groups.ADMIN},
      runAsync = true
   )
   public void glist(BungeeCommandSender commandSender, String label, String[] args) {
      int crackeds = 0;
      int premiums = 0;
      commandSender.sendMessage("");
      Iterator var6 = ProxyServer.getInstance().getServers().values().iterator();

      while(var6.hasNext()) {
         ServerInfo server = (ServerInfo)var6.next();
         List<String> players = new ArrayList();
         Iterator var9 = server.getPlayers().iterator();

         while(var9.hasNext()) {
            ProxiedPlayer player = (ProxiedPlayer)var9.next();
            boolean premium = PremiumMapManager.getPremiumMap(player.getName()).isPremium();
            if (premium) {
               ++premiums;
            } else {
               ++crackeds;
            }

            if (CommonsGeneral.getProfileManager().containsProfile(player.getName())) {
               BungeePlayer proxyPlayer = BungeeMain.getBungeePlayer(player.getName());
               if (premium) {
                  players.add(proxyPlayer.getGroup().getColor() + player.getDisplayName());
               } else {
                  players.add(proxyPlayer.getGroup().getColor() + player.getDisplayName());
               }
            } else {
               players.add("§7" + player.getDisplayName());
            }
         }

         Collections.sort(players, String.CASE_INSENSITIVE_ORDER);
         commandSender.sendMessage("§a" + server.getName() + " - " + server.getPlayers().size() + Util.format(players, ChatColor.RESET + ", "));
      }

      commandSender.sendMessage("");
      commandSender.sendMessage("§fNo momento há §a" + ProxyServer.getInstance().getPlayers().size() + " §fjogadores em toda rede!");
      commandSender.sendMessage("");
      commandSender.sendMessage("§fNo momento há §a" + premiums + " §fjogadores originais online.");
      commandSender.sendMessage("§fNo momento há §a" + crackeds + " §fjogadores piratas online.");
      commandSender.sendMessage("");
   }
}