package com.br.teagadev.servercommunication.server;

import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketAction;
import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.common.connection.CommonClient;
import com.br.teagadev.servercommunication.common.connection.IConnection;
import com.br.teagadev.servercommunication.common.packet.CommonPacket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerGeneral extends Thread implements IConnection {
   private String hostName;
   private int port;
   private ServerSocket serverSocket;
   private ConcurrentHashMap<Socket, CommonClient> socketToRegister;
   private Map<String, CommonClient> clientMap;
   private boolean RUNNING;

   public ServerGeneral(String hostName) throws Exception {
      this.setHostName(hostName);
      this.setPort(30001);
      this.setServerSocket(new ServerSocket());
      this.getServerSocket().bind(new InetSocketAddress(hostName, this.getPort()));
      this.setSocketToRegister(new ConcurrentHashMap());
      this.setClientMap(new HashMap());
      this.debug("Connection to " + hostName + ":" + this.port + " established!");
      ServerCommunication.getUpdateListener().register((updateType) -> {
         Iterator var2 = this.getSocketToRegister().values().iterator();

         while(var2.hasNext()) {
            CommonClient connections = (CommonClient)var2.next();
            if (!connections.isAuthenticated()) {
               connections.incrementTimeout();
               if (connections.getTime() == 13) {
                  connections.disconnect();
                  this.socketToRegister.remove(connections.getSocket());
               }

               if (connections.getTime() == 10) {
                  connections.sendPacket((new CPacketAction("Server", 0)).writeType("TimedOut"));
               }
            }
         }

      });
   }

   public void run() {
      this.RUNNING = true;

      while(this.RUNNING) {
         if (this.serverSocket == null) {
            this.RUNNING = false;
            return;
         }

         if (this.getServerSocket().isClosed()) {
            try {
               this.getServerSocket().bind(new InetSocketAddress(this.getHostName(), this.getPort()));
            } catch (IOException var7) {
               var7.printStackTrace();
               break;
            }
         }

         try {
            Socket socket = this.serverSocket.accept();
            String clientHost = socket.getInetAddress().getHostName();
            int clientPort = socket.getPort();
            this.socketToRegister.put(socket, new CommonClient(socket));
         } catch (SocketException var5) {
            if (!var5.getLocalizedMessage().equals("Socket closed")) {
               var5.printStackTrace();
            }
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      try {
         this.disconnect();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public void sendPacket(Socket socket, CommonPacket packet) {
      CommonClient connection = this.getConnectionByPort(socket.getPort());
      if (connection != null) {
         connection.sendPacket(packet);
      }

   }

   public void sendPacket(String text, CommonPacket packet) {
      String name = text.replaceAll("\\d", "");
      int id = name.length() != text.length() ? Integer.parseInt(text.replaceAll("\\D+", "")) : 1;
      this.sendPacket(name, id, packet);
   }

   public void sendPacket(String name, int id, CommonPacket packet) {
      CommonClient connection = this.getConnectionListen(name, id);
      if (connection != null) {
         connection.sendPacket(packet);
      }

   }

   public CommonClient getConnectionListen(String name, int id) {
      CommonClient connection = (CommonClient)this.getClientMap().getOrDefault(name.toLowerCase() + "," + id, (CommonClient)null);
      if (connection != null) {
         return connection;
      } else {
         Iterator var4 = this.getClientMap().values().iterator();

         while(var4.hasNext()) {
            CommonClient connections = (CommonClient)var4.next();
            if (connections.getClientName().equalsIgnoreCase(name) && connections.getClientID() == id) {
               connection = connections;
               break;
            }
         }

         return connection;
      }
   }

   public CommonClient getConnectionListenByPort(int port) {
      CommonClient connection = null;
      Iterator var3 = this.socketToRegister.values().iterator();

      while(var3.hasNext()) {
         CommonClient connectionListen = (CommonClient)var3.next();
         if (connectionListen.getSocket().getPort() == port) {
            connection = connectionListen;
            break;
         }
      }

      return connection;
   }

   public CommonClient getConnectionByPort(int port) {
      CommonClient connection = null;
      Iterator var3 = this.getClientMap().values().iterator();

      while(var3.hasNext()) {
         CommonClient connectionListen = (CommonClient)var3.next();
         if (connectionListen.getSocket().getPort() == port) {
            connection = connectionListen;
            break;
         }
      }

      return connection;
   }

   public Socket getSocketByPort(int port) {
      Socket socket = null;
      Iterator var3 = this.socketToRegister.keySet().iterator();

      while(var3.hasNext()) {
         Socket sockets = (Socket)var3.next();
         if (sockets.getPort() == port) {
            socket = sockets;
            break;
         }
      }

      return socket;
   }

   public void registerClient(String clientName, Integer clientID, Socket socket) {
      CommonClient connection = this.getConnectionListenByPort(socket.getPort());
      if (connection != null) {
         connection.setClientName(clientName);
         connection.setClientID(clientID);
         this.clientMap.put(clientName + "," + clientID, connection);
      }

      this.socketToRegister.remove(socket);
   }

   public void unregisterClient(Socket socket) {
      CommonClient connection = this.getConnectionListenByPort(socket.getPort());
      if (connection != null) {
         String clientName = connection.getClientName();
         int clientId = connection.getClientID();
         this.clientMap.remove(clientName.toLowerCase() + "," + clientId);
      }

   }

   public void debug(String string) {
      ServerCommunication.debug("SERVER", string);
   }

   public String getAddress() {
      return this.hostName + ":" + this.port;
   }

   public void disconnect() throws IOException {
      this.RUNNING = false;
      if (this.getServerSocket() != null) {
         this.getServerSocket().close();
      }

      this.serverSocket = null;
   }

   public Socket getSocket() {
      return null;
   }

   public String getServerName() {
      return "SERVER";
   }

   public String getHostName() {
      return this.hostName;
   }

   public int getPort() {
      return this.port;
   }

   public ServerSocket getServerSocket() {
      return this.serverSocket;
   }

   public ConcurrentHashMap<Socket, CommonClient> getSocketToRegister() {
      return this.socketToRegister;
   }

   public Map<String, CommonClient> getClientMap() {
      return this.clientMap;
   }

   public boolean isRUNNING() {
      return this.RUNNING;
   }

   public void setHostName(String hostName) {
      this.hostName = hostName;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public void setServerSocket(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
   }

   public void setSocketToRegister(ConcurrentHashMap<Socket, CommonClient> socketToRegister) {
      this.socketToRegister = socketToRegister;
   }

   public void setClientMap(Map<String, CommonClient> clientMap) {
      this.clientMap = clientMap;
   }

   public void setRUNNING(boolean RUNNING) {
      this.RUNNING = RUNNING;
   }
}