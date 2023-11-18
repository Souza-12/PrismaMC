package com.br.teagadev.prismamc.commons.custompackets;

import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketAction;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import java.net.Socket;

public abstract class CommonPacketHandler {
   public abstract void handleCPacketAction(CPacketAction var1, Socket var2);

   public abstract void handleCPacketPlayerAction(CPacketCustomAction var1, Socket var2);
}