package com.br.teagadev.servercommunication.common.connection;

import com.br.teagadev.servercommunication.common.connection.handle.InputHandler;
import com.br.teagadev.servercommunication.common.connection.handle.OutputHandler;
import com.br.teagadev.servercommunication.common.packet.CommonPacket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CommonClient implements IConnection {
   private Socket socket;
   private InputHandler inputHandler;
   private OutputHandler outputHandler;
   private String clientName;
   private int clientID;
   private int time;

   public CommonClient(Socket socket) throws IOException {
      this.setSocket(socket);
      this.setInputHandler(new InputHandler(this, new DataInputStream(socket.getInputStream())));
      this.setOutputHandler(new OutputHandler(new DataOutputStream(socket.getOutputStream())));
      this.getInputHandler().start();
      this.getOutputHandler().start();
   }

   public void sendPacket(CommonPacket packet) {
      this.getOutputHandler().sendPacket(packet);
   }

   public void disconnect() {
      if (!this.socket.isClosed()) {
         try {
            this.inputHandler.close();
            this.outputHandler.close();
            this.socket.close();
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }

      this.inputHandler.stopThread();
      this.outputHandler.stopThread();
   }

   public String getAddress() {
      return this.socket.getInetAddress().getHostName() + ":" + this.socket.getPort();
   }

   public boolean isAuthenticated() {
      return this.getClientName() != null;
   }

   public void incrementTimeout() {
      ++this.time;
   }

   public String getServerName() {
      return this.clientName + "-" + this.clientID;
   }

   public Socket getSocket() {
      return this.socket;
   }

   public InputHandler getInputHandler() {
      return this.inputHandler;
   }

   public OutputHandler getOutputHandler() {
      return this.outputHandler;
   }

   public String getClientName() {
      return this.clientName;
   }

   public int getClientID() {
      return this.clientID;
   }

   public int getTime() {
      return this.time;
   }

   public void setSocket(Socket socket) {
      this.socket = socket;
   }

   public void setInputHandler(InputHandler inputHandler) {
      this.inputHandler = inputHandler;
   }

   public void setOutputHandler(OutputHandler outputHandler) {
      this.outputHandler = outputHandler;
   }

   public void setClientName(String clientName) {
      this.clientName = clientName;
   }

   public void setClientID(int clientID) {
      this.clientID = clientID;
   }

   public void setTime(int time) {
      this.time = time;
   }
}