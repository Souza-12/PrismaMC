package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.BungeeMessages;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class KickAllCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "kickall", aliases = {"kall"}, groupsToUse = {Groups.DONO})
    public void kickAll(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("§cUtilize: /kickall <Motivo>");
            return;
        }

        final String motivo = StringUtility.createArgs(0, args);

        commandSender.sendMessage(BungeeMessages.KICKING_ALL_PLAYERS);

        int delay = 500;

        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (BungeeMain.isValid(proxiedPlayer)) {
                if (BungeeMain.getBungeePlayer(proxiedPlayer.getName()).getGroup().getLevel() >= Groups.ADMIN
                        .getLevel()) {
                    continue;
                }
            }

            BungeeMain.runLater(() -> proxiedPlayer.disconnect(BungeeMessages.VOCE_FOI_EXPULSO.replace("%expulsou%", commandSender.getNick())
                    .replace("%motivo%", motivo)), delay, TimeUnit.MILLISECONDS);
            delay += 500;
        }
    }
}