package com.br.teagadev.prismamc.commons.bungee.account.permission;

import com.br.teagadev.prismamc.commons.common.group.Groups;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePlayerPermissions {
   public static void addPermission(ProxiedPlayer proxiedPlayer, String permission) {
      proxiedPlayer.setPermission(permission, true);
   }

   public static void injectPermissions(ProxiedPlayer proxiedPlayer, String grupo) {
      List<String> permissions = Groups.getGroup(grupo).getPermissions();
      permissions.forEach((permission) -> {
         proxiedPlayer.setPermission(permission, true);
      });
   }

   public static void clearPermissions(ProxiedPlayer proxiedPlayer) {
      ArrayList<String> clone = new ArrayList(proxiedPlayer.getPermissions());
      clone.forEach((permission) -> {
         proxiedPlayer.setPermission(permission, false);
      });
   }
}