package com.br.teagadev.prismamc.commons.bukkit.account.permission;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

public class PlayerAttachment {
   private final PermissionAttachment attachment;

   public PlayerAttachment(Player player, Plugin plugin) {
      this.attachment = player.addAttachment(plugin);
   }

   public void addPermission(String permission) {
      this.attachment.setPermission(permission, true);
   }

   public void addPermissions(List<String> permissions) {
      if (permissions != null) {
         Iterator var2 = permissions.iterator();

         while(var2.hasNext()) {
            String permission = (String)var2.next();
            this.attachment.setPermission(permission, true);
         }

      }
   }

   public void removePermissions(List<String> permissions) {
      if (permissions != null) {
         Iterator var2 = permissions.iterator();

         while(var2.hasNext()) {
            String permission = (String)var2.next();
            this.attachment.setPermission(permission, false);
         }

      }
   }

   public void resetPermissions() {
      if (this.getPermissions().size() != 0) {
         Iterator var1 = this.getPermissions().iterator();

         while(var1.hasNext()) {
            String permissions = (String)var1.next();
            this.attachment.setPermission(permissions, false);
         }
      }

   }

   public void removePermission(String permission) {
      this.attachment.setPermission(permission, false);
   }

   public List<String> getPermissions() {
      return new ArrayList(this.attachment.getPermissions().keySet());
   }
}