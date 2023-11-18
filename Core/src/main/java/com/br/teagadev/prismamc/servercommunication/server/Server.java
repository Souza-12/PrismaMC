package com.br.teagadev.servercommunication.server;

import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.ServerCommunicationInstance;
import com.br.teagadev.servercommunication.common.packet.CommonPacket;
import com.br.teagadev.servercommunication.common.packet.listener.PacketListener;
import java.net.Socket;

public class Server {
   private static Server instance;
   private ServerGeneral serverGeneral;

   public Server(String hostName) {
      setInstance(this);
      ServerCommunication.debug("[ServerCommunication] The server has started on: " + hostName);
      ServerCommunication.INSTANCE = ServerCommunicationInstance.SERVER;
      ServerCommunication.getPacketListener().register(new PacketListener());

      try {
         this.serverGeneral = new ServerGeneral(hostName);
         this.serverGeneral.start();
      } catch (Exception var3) {
         var3.printStackTrace();
         System.exit(0);
      }

   }

   public void sendPacket(String text, CommonPacket packet) {
      this.getServerGeneral().sendPacket(text, packet);
   }

   public void sendPacket(String name, int id, CommonPacket packet) {
      this.getServerGeneral().sendPacket(name, id, packet);
   }

   public void sendPacket(Socket socket, CommonPacket packet) {
      this.getServerGeneral().sendPacket(socket, packet);
   }

   public static Server getInstance() {
      return instance;
   }

   public static void setInstance(Server instance) {
      Server.instance = instance;
   }

   public ServerGeneral getServerGeneral() {
      return this.serverGeneral;
   }
}