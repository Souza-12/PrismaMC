package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportCommand implements CommandClass {
   @Command(
      name = "report",
      aliases = {"reportar", "rp"}
   )
   public void execute(BungeeCommandSender commandSender, String ignored, String[] args) {
      if (commandSender.isPlayer()) {
         ProxiedPlayer proxiedPlayer = commandSender.getPlayer();
         if (args.length < 1) {
            proxiedPlayer.sendMessage("§cUtilize: /report <Nick> <Motivo>");
         } else {
            BungeePlayer proxyPlayer;
            if (args[0].equalsIgnoreCase("toggle") && proxiedPlayer.hasPermission("commons.receivereport")) {
               proxyPlayer = BungeeMain.getBungeePlayer(proxiedPlayer.getName());
               if (proxyPlayer.getBoolean(DataType.RECEIVE_REPORTS)) {
                  proxyPlayer.set(DataType.RECEIVE_REPORTS, false);
                  proxiedPlayer.sendMessage("§cVocê agora não receberá as notificações de report.");
               } else {
                  proxyPlayer.set(DataType.RECEIVE_REPORTS, true);
                  proxiedPlayer.sendMessage("§aVocê agora receberá as notificações de report.");
               }

               BungeeMain.runAsync(() -> {
                  proxyPlayer.getDataHandler().saveCategory(DataCategory.PREFERENCES);
               });
            } else if (args.length < 2) {
               proxiedPlayer.sendMessage("§cUtilize: /report <Nick> <Motivo>");
            } else {
               proxyPlayer = BungeeMain.getBungeePlayer(proxiedPlayer.getName());
               if (!proxyPlayer.podeReportar()) {
                  proxiedPlayer.sendMessage("§cAguarde alguns segundos para reportar um jogador novamente.");
               } else {
                  ProxiedPlayer reportado = ProxyServer.getInstance().getPlayer(args[0]);
                  if (reportado != null && reportado.getServer() != null) {
                     if (reportado == proxiedPlayer) {
                        proxiedPlayer.sendMessage("§cVocê não pode reportar a si mesmo!");
                     } else {
                        proxiedPlayer.sendMessage("§aJogador reportado com sucesso!");
                        proxyPlayer.setLastReport(System.currentTimeMillis());
                        this.notifyStaffers(proxiedPlayer.getName(), reportado.getName(), StringUtility.createArgs(1, args));
                     }
                  } else {
                     proxiedPlayer.sendMessage("§fJogador offline!");
                  }
               }
            }
         }
      }
   }

   private void notifyStaffers(String from, String target, String cause) {
      BaseComponent[] components = TextComponent.fromLegacyText("§cNOVO REPORT: %target% (Reportado pelo: %from%) - Motivo: %cause%".replace("%from%", from).replace("%target%", target).replace("%cause%", cause));
      BaseComponent[] var5 = components;
      int var6 = components.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         BaseComponent component = var5[var7];
         component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/go " + target));
         component.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClique para ver a lista de reports")));
      }

      ProxyServer.getInstance().getPlayers().stream().filter((pp) -> {
         return pp.hasPermission("commons.receivereport") && BungeeMain.isValid(pp) && BungeeMain.getBungeePlayer(pp.getName()).getBoolean(DataType.RECEIVE_REPORTS);
      }).forEach((pp) -> {
         pp.sendMessage(components);
      });
   }
}