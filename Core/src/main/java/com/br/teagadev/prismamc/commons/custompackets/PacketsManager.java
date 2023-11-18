package com.br.teagadev.prismamc.commons.custompackets;

import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketAction;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.common.packet.CommonPacket;
import java.util.HashMap;
import java.util.Map;

public class PacketsManager {
   private static final Map<String, Class<?>> MAP_CLASS = new HashMap();

   public static void register(Class<? extends CommonPacket> packetClass) {
      MAP_CLASS.put(packetClass.getSimpleName(), packetClass);
   }

   public static CommonPacket getPacket(String packetName) {
      try {
         return (CommonPacket)((Class)MAP_CLASS.get(packetName)).newInstance();
      } catch (Exception var2) {
         ServerCommunication.debug("PACKET MANAGER", "An error ocurred on trying get packet with the name -> " + packetName);
         return null;
      }
   }

   static {
      register(CPacketAction.class);
      register(CPacketCustomAction.class);
   }
}