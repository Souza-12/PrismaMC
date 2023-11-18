package com.br.teagadev.servercommunication.common.connection.handle;

import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.common.connection.IConnection;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

public class InputHandler extends Thread {
   private IConnection connection;
   private DataInputStream data;
   private boolean running;

   public InputHandler(IConnection connection, DataInputStream data) {
      this.setConnection(connection);
      this.setData(data);
   }

   public void run() {
      this.running = true;

      while(this.isRunning()) {
         try {
            String message = this.data.readUTF();
            if (message != null) {
               ServerCommunication.getPacketListener().call(message, this.connection.getSocket());
               message = null;
            }
         } catch (EOFException var4) {
            this.running = false;
         } catch (SocketException var5) {
         } catch (IOException var6) {
            var6.printStackTrace();

            try {
               this.connection.disconnect();
            } catch (IOException var3) {
               var3.printStackTrace();
            }
         }
      }

   }

   public void stopThread() {
      this.running = false;
   }

   public void close() throws IOException {
      this.data.close();
   }

   public IConnection getConnection() {
      return this.connection;
   }

   public DataInputStream getData() {
      return this.data;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void setConnection(IConnection connection) {
      this.connection = connection;
   }

   public void setData(DataInputStream data) {
      this.data = data;
   }

   public void setRunning(boolean running) {
      this.running = running;
   }
}