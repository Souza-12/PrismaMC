package com.br.teagadev.servercommunication;

import com.br.teagadev.prismamc.commons.custompackets.CommonPacketHandler;
import com.br.teagadev.servercommunication.client.Client;
import com.br.teagadev.servercommunication.common.packet.PacketListenerManager;
import com.br.teagadev.servercommunication.common.update.UpdateListener;
import com.br.teagadev.servercommunication.server.Server;
import java.io.IOException;

public class ServerCommunication {
   public static final int TIMEOUT_TIME = 10;
   public static final int PORT = 30001;
   private static final PacketListenerManager packetListener = new PacketListenerManager();
   public static ServerCommunicationInstance INSTANCE;
   public static final boolean DEBUG_CLIENT_CONNECTED = false;
   public static final boolean DEBUG_CLIENT_DROPED = false;
   public static final boolean DEBUG_CLIENT_AUTHENTICATED = false;
   public static final boolean DEBUG_PACKET_RECEIVED = false;
   public static boolean DEBUG_PACKET_SEND;
   private static CommonPacketHandler packetHandler;
   private static UpdateListener updateListener;

   public static void startClient(String clientName, int clientID, String hostName) {
      Client client = new Client(hostName, clientName, clientID);
      debug("CLIENT", "Connecting on: " + hostName);

      try {
         client.getClientConnection().connect();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public static void startServer(String hostName) {
      new Server(hostName);
   }

   public static void main(String[] args) {
      if (args.length > 1) {
         (new Thread(updateListener = new UpdateListener())).start();
         if (args[1].equalsIgnoreCase("server")) {
            startServer(args[0]);
         } else {
            startClient("KitPvP", 1, args[0]);
         }
      } else {
         debug("ERROR", "Correct usage: ServerCommunication.jar HostName");
      }

   }

   public static void debug(String message) {
      debug((String)null, message);
   }

   public static void debug(String prefix, String message) {
      if (prefix == null) {
         System.out.println("[Debug] " + message);
      } else {
         System.out.println("[" + prefix + "] " + message);
      }

   }

   public static PacketListenerManager getPacketListener() {
      return packetListener;
   }

   public static CommonPacketHandler getPacketHandler() {
      return packetHandler;
   }

   public static void setPacketHandler(CommonPacketHandler packetHandler) {
      ServerCommunication.packetHandler = packetHandler;
   }

   public static UpdateListener getUpdateListener() {
      return updateListener;
   }

   static {
      INSTANCE = ServerCommunicationInstance.UNKNOWN;
      DEBUG_PACKET_SEND = false;
      packetHandler = null;
      (new Thread(updateListener = new UpdateListener())).start();
   }
}