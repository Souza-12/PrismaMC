package com.br.teagadev.servercommunication.client;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketAction;
import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.common.connection.CommonClient;
import com.br.teagadev.servercommunication.common.connection.IConnection;
import com.br.teagadev.servercommunication.common.packet.CommonPacket;
import com.br.teagadev.servercommunication.common.packet.listener.PacketListener;
import com.br.teagadev.servercommunication.common.update.UpdateListener.UpdateEvent;
import com.br.teagadev.servercommunication.common.update.UpdateListener.UpdateType;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection implements IConnection {
   private String hostName;
   private int port;
   private Socket socket;
   private CommonClient connectionListen;

   public ClientConnection(String hostName) {
      this.setHostName(hostName);
      this.setPort(30001);
   }

   public void connect() throws IOException {
      this.socket = new Socket(this.getHostName(), 30001);
      this.setConnectionListen(new CommonClient(this.socket));
      ServerCommunication.getPacketListener().register(new PacketListener());
      this.debug("Connected to " + this.hostName + ":" + this.port + "!");
      this.sendPacket((new CPacketAction(Client.getInstance().getClientName(), Client.getInstance().getClientID())).writeType("HandShake").writeField(Client.getInstance().getClientName()).writeFieldValue("" + Client.getInstance().getClientID()));
      if (CommonsGeneral.getPluginInstance().isBukkit() && BukkitServerAPI.isRegisteredServer()) {
         BukkitServerAPI.registerServer();
      }

      ServerCommunication.getUpdateListener().register(new UpdateEvent() {
         int seconds = 0;

         public void onUpdate(UpdateType updateType) {
            if (updateType == UpdateType.SECOND) {
               ++this.seconds;
               if (this.seconds % 10 == 0) {
                  ClientConnection.this.sendPacket((new CPacketAction(Client.getInstance().getClientName(), Client.getInstance().getClientID())).writeType("KeepAlive"));
               }

            }
         }
      });
   }

   public void sendPacket(CommonPacket packet) {
      this.connectionListen.sendPacket(packet);
   }

   public void debug(String string) {
      ServerCommunication.debug("CLIENT - " + Client.getInstance().getClientName() + ":" + Client.getInstance().getClientID(), string);
   }

   public boolean isConnected() {
      return this.socket != null && this.socket.isConnected();
   }

   public String getAddress() {
      return this.hostName + ":" + this.port;
   }

   public void disconnect() {
      try {
         this.socket.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      } finally {
         this.socket = null;
      }

   }

   public Socket getSocket() {
      return this.socket;
   }

   public String getServerName() {
      return null;
   }

   public void reconnect(Runnable callback) {
      if (this.socket != null) {
         this.disconnect();
      }

      if (this.connectionListen != null) {
         this.connectionListen.disconnect();
      }

      try {
         this.connect();
      } catch (Exception var5) {
         this.debug("Failed to reconnect, retrying... (" + var5.getLocalizedMessage() + ").");

         try {
            Thread.sleep(5000L);
         } catch (Exception var4) {
         }

         this.reconnect(callback);
         return;
      }

      if (callback != null) {
         callback.run();
      }

   }

   public String getHostName() {
      return this.hostName;
   }

   public int getPort() {
      return this.port;
   }

   public CommonClient getConnectionListen() {
      return this.connectionListen;
   }

   public void setHostName(String hostName) {
      this.hostName = hostName;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public void setSocket(Socket socket) {
      this.socket = socket;
   }

   public void setConnectionListen(CommonClient connectionListen) {
      this.connectionListen = connectionListen;
   }
}