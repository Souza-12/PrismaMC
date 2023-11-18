package com.br.teagadev.prismamc.commons.bungee.skins;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.server.Server;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.connection.LoginResult.Property;

public class SkinApplier {
   public static void handleSkin(PendingConnection pendingConnection) throws Exception {
      Property textures = (Property)SkinStorage.getOrCreateSkinForPlayer(pendingConnection.getName());
      InitialHandler handler = (InitialHandler)pendingConnection;
      if (textures != null) {
         if (handler.isOnlineMode()) {
            handler.getLoginProfile().setProperties(new Property[]{textures});
         } else {
            LoginResult profile = getLoginProfile(pendingConnection.getName(), pendingConnection.getUniqueId().toString(), textures);
            ReflectionUtil.setObject(InitialHandler.class, pendingConnection, "loginProfile", profile);
         }

      }
   }

   public static void fastApply(ProxiedPlayer proxiedPlayer, String skinToApply) {
      fastApply(proxiedPlayer, skinToApply, true);
   }

   public static void fastApply(ProxiedPlayer proxiedPlayer, String skinToApply, boolean sendPacket) {
      try {
         if (proxiedPlayer == null || proxiedPlayer.getServer() == null) {
            return;
         }

         Property textures = (Property)SkinStorage.getOrCreateSkinForPlayer(skinToApply);
         if (textures == null) {
            return;
         }

         InitialHandler handler = (InitialHandler)proxiedPlayer.getPendingConnection();
         if (handler.isOnlineMode()) {
            handler.getLoginProfile().setProperties(new Property[]{textures});
         } else {
            LoginResult profile = getLoginProfile(handler.getName(), handler.getUniqueId().toString(), textures);
            ReflectionUtil.setObject(InitialHandler.class, handler, "loginProfile", profile);
         }

         if (sendPacket) {
            if (proxiedPlayer.getServer() == null) {
               return;
            }

            Server.getInstance().sendPacket(proxiedPlayer.getServer().getInfo().getName(), (new CPacketCustomAction(proxiedPlayer.getUniqueId())).type(PacketType.BUKKIT_RECEIVE_SKIN_DATA).field("sendPacket").fieldValue(textures.getName()).extraValue(textures.getValue()).extraValue2(textures.getSignature()));
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public static void applySkin(ProxiedPlayer proxiedPlayer, String skinToApply) {
      BungeeMain.runAsync(() -> {
         try {
            if (proxiedPlayer == null || proxiedPlayer.getServer() == null) {
               return;
            }

            Property textures = (Property)SkinStorage.getOrCreateSkinForPlayer(skinToApply);
            if (textures == null) {
               return;
            }

            InitialHandler handler = (InitialHandler)proxiedPlayer.getPendingConnection();
            if (handler.isOnlineMode()) {
               handler.getLoginProfile().setProperties(new Property[]{textures});
            } else {
               LoginResult profile = getLoginProfile(handler.getName(), handler.getUniqueId().toString(), textures);
               ReflectionUtil.setObject(InitialHandler.class, handler, "loginProfile", profile);
            }

            Server.getInstance().sendPacket(proxiedPlayer.getServer().getInfo().getName(), (new CPacketCustomAction(proxiedPlayer.getUniqueId())).type(PacketType.BUKKIT_RECEIVE_SKIN_DATA).field("sendPacket").fieldValue(textures.getName()).extraValue(textures.getValue()).extraValue2(textures.getSignature()));
         } catch (Exception var5) {
            var5.printStackTrace();
         }

      });
   }

   private static LoginResult getLoginProfile(String name, String uniqueId, Property textures) {
      return new LoginResult(uniqueId, name, new Property[]{textures});
   }
}