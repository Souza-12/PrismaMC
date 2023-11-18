package com.br.teagadev.servercommunication.common.connection.handle;

import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.common.connection.IConnection;
import lombok.Getter;
import lombok.Setter;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

@Getter
@Setter
public class InputHandler extends Thread {

    private IConnection connection;
    private DataInputStream data;

    private boolean running;

    public InputHandler(final IConnection connection, DataInputStream data) {
        setConnection(connection);
        setData(data);
    }

    @Override
    public void run() {
        running = true;

        while (isRunning()) {
            try {
                String message = data.readUTF();

                if (message != null) {
                    ServerCommunication.getPacketListener().call(message, connection.getSocket());
                    message = null;
                }
            } catch (EOFException ex) {
                running = false;
            } catch (SocketException ex) {
                //TODO nothing to do
            } catch (IOException ex) {
                ex.printStackTrace();
                try {
                    connection.disconnect();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopThread() {
        running = false;
    }

    public void close() throws IOException {
        data.close();
    }
}