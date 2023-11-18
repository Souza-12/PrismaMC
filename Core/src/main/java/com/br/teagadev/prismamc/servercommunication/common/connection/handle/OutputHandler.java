package com.br.teagadev.servercommunication.common.connection.handle;

import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.client.Client;
import com.br.teagadev.servercommunication.common.packet.CommonPacket;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class OutputHandler extends Thread {
   private final Object LOCK = new Object();
   private final List<CommonPacket> packetQueue = new ArrayList();
   private final DataOutputStream data;
   private boolean running;
   private int serverOutOfReach;

   public OutputHandler(DataOutputStream data) {
      this.data = data;
   }

   public void run() {
      this.running = true;

      while(this.running) {
         try {
            synchronized(this.packetQueue) {
               for(; this.packetQueue.size() > 0; this.packetQueue.remove(0)) {
                  CommonPacket packet = (CommonPacket)this.packetQueue.get(0);
                  if (packet != null) {
                     try {
                        this.data.writeUTF(packet.getJSONString());
                     } catch (SocketException var7) {
                        ++this.serverOutOfReach;
                        if (this.serverOutOfReach == 1) {
                           ServerCommunication.debug("PACKET SEND", "Server is offline!");
                        }

                        if (this.serverOutOfReach % 5 == 0) {
                           Client.getInstance().getClientConnection().reconnect((Runnable)null);
                        }

                        if (this.serverOutOfReach >= 30) {
                           this.stopThread();
                           this.close();
                           this.serverOutOfReach = 0;
                        }
                     }

                     packet = null;
                  }
               }
            }

            synchronized(this.LOCK) {
               this.LOCK.wait(3250L);
            }
         } catch (InterruptedException var9) {
            var9.printStackTrace();
         } catch (Exception var10) {
            this.packetQueue.remove(0);
            var10.printStackTrace();
         }
      }

   }

   public void sendPacket(CommonPacket packet) {
      synchronized(this.packetQueue) {
         packet.write();
         this.packetQueue.add(packet);
      }

      synchronized(this.LOCK) {
         this.LOCK.notifyAll();
      }
   }

   public void stopThread() {
      this.running = false;
   }

   public void close() throws IOException {
      this.data.close();
   }
}