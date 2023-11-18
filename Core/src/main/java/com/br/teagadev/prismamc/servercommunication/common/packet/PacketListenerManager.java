package com.br.teagadev.servercommunication.common.packet;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PacketListenerManager {
   private final List<PacketListenerManager.PacketEvent> packetMap = new ArrayList();

   public void register(PacketListenerManager.PacketEvent packetEvent) {
      this.packetMap.add(packetEvent);
   }

   public void unregister(PacketListenerManager.PacketEvent packetEvent) {
      this.packetMap.remove(packetEvent);
   }

   public void call(String packetString, Socket socket) {
      this.packetMap.forEach((packetEvent) -> {
         packetEvent.onPacketReceive(packetString, socket);
      });
   }

   public interface PacketEvent {
      void onPacketReceive(String var1, Socket var2);
   }
}