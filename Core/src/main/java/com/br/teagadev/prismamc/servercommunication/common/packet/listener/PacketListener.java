package com.br.teagadev.servercommunication.common.packet.listener;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.custompackets.PacketsManager;
import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.common.packet.CommonPacket;
import com.br.teagadev.servercommunication.common.packet.PacketListenerManager.PacketEvent;
import com.google.gson.JsonObject;
import java.net.Socket;

public class PacketListener implements PacketEvent {
   public void onPacketReceive(String packetString, Socket socket) {
      JsonObject json = null;
      boolean var16 = false;

      String packetName;
      CommonPacket packet;
      label176: {
         try {
            var16 = true;
            json = CommonsConst.PARSER.parse(packetString).getAsJsonObject();
            var16 = false;
            break label176;
         } catch (Exception var20) {
            ServerCommunication.debug("PACKET PARSER", "An error ocurred while trying to parse packet! (" + var20.getLocalizedMessage() + ")");
            var16 = false;
         } finally {
            if (var16) {
               if (json != null) {
                  if (json.has("packetName")) {
                     String packetName1 = json.get("packetName").getAsString();
                     CommonPacket packet1 = PacketsManager.getPacket(packetName1);
                     if (packet1 != null) {
                        packet1.read(json);

                        try {
                           packet1.handle(socket);
                        } catch (Exception var17) {
                           ServerCommunication.debug("PACKET HANDLER", "An error ocurred while trying to handle packet! (" + var17.getLocalizedMessage() + ")");
                           var17.printStackTrace();
                        }

                        packet1 = null;
                     }

                     packetName1 = null;
                  } else {
                     ServerCommunication.debug("I received a Invalid Packet! (#1)");
                  }

                  json = null;
               }

            }
         }

         if (json != null) {
            if (json.has("packetName")) {
               packetName = json.get("packetName").getAsString();
               packet = PacketsManager.getPacket(packetName);
               if (packet != null) {
                  packet.read(json);

                  try {
                     packet.handle(socket);
                  } catch (Exception var18) {
                     ServerCommunication.debug("PACKET HANDLER", "An error ocurred while trying to handle packet! (" + var18.getLocalizedMessage() + ")");
                     var18.printStackTrace();
                  }

                  packet = null;
               }

               packetName = null;
            } else {
               ServerCommunication.debug("I received a Invalid Packet! (#1)");
            }

            json = null;
         }

         return;
      }

      if (json != null) {
         if (json.has("packetName")) {
            packetName = json.get("packetName").getAsString();
            packet = PacketsManager.getPacket(packetName);
            if (packet != null) {
               packet.read(json);

               try {
                  packet.handle(socket);
               } catch (Exception var19) {
                  ServerCommunication.debug("PACKET HANDLER", "An error ocurred while trying to handle packet! (" + var19.getLocalizedMessage() + ")");
                  var19.printStackTrace();
               }

               packet = null;
            }

            packetName = null;
         } else {
            ServerCommunication.debug("I received a Invalid Packet! (#1)");
         }

         json = null;
      }

   }
}