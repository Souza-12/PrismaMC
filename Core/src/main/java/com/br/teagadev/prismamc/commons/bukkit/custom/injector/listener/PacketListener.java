package com.br.teagadev.prismamc.commons.bukkit.custom.injector.listener;

import com.br.teagadev.prismamc.commons.bukkit.custom.injector.PacketObject;

public interface PacketListener {

    void onPacketReceiving(PacketObject packetObject);

    void onPacketSending(PacketObject packetObject);
}
