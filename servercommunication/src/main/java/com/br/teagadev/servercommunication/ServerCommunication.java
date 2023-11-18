package com.br.teagadev.servercommunication;

import com.br.teagadev.prismamc.commons.custompackets.CommonPacketHandler;
import com.br.teagadev.servercommunication.client.Client;
import com.br.teagadev.servercommunication.common.packet.PacketListenerManager;
import com.br.teagadev.servercommunication.common.update.UpdateListener;
import com.br.teagadev.servercommunication.server.Server;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class ServerCommunication {

    public static final int TIMEOUT_TIME = 10; //TIMEOUT CLIENTS

    public static final int PORT = 30001; //SERVER PORT

    @Getter
    private static final PacketListenerManager packetListener = new PacketListenerManager();
    public static ServerCommunicationInstance INSTANCE = ServerCommunicationInstance.UNKNOWN;
    // START DEBUGS
    public static final boolean DEBUG_CLIENT_CONNECTED = false;
    public static final boolean DEBUG_CLIENT_DROPED = false;
    public static final boolean DEBUG_CLIENT_AUTHENTICATED = false;
    public static final boolean DEBUG_PACKET_RECEIVED = false;
    public static boolean DEBUG_PACKET_SEND = false;
    @Getter
    @Setter
    private static CommonPacketHandler packetHandler = null;
    @Getter
    private static UpdateListener updateListener;
    //END DEBUGS

    static {
        new Thread(updateListener = new UpdateListener()).start();
    }

    public static void startClient(final String clientName, final int clientID, final String hostName) {
        Client client = new Client(hostName, clientName, clientID);

        debug("CLIENT", "Connecting on: " + hostName);

        try {
            client.getClientConnection().connect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void startServer(final String hostName) {
        new Server(hostName);
    }

    public static void main(final String[] args) {
        if (args.length > 1) {
            new Thread(updateListener = new UpdateListener()).start();

            if (args[1].equalsIgnoreCase("server")) {
                startServer(args[0]);
            } else {
                startClient("KitPvP", 1, args[0]);
            }
        } else {
            debug("ERROR", "Correct usage: ServerCommunication.jar HostName");
        }
    }


    public static void debug(final String message) {
        debug(null, message);
    }

    public static void debug(final String prefix, final String message) {
        if (prefix == null) {
            System.out.println("[Debug] " + message);
        } else {
            System.out.println("[" + prefix + "] " + message);
        }
    }
}