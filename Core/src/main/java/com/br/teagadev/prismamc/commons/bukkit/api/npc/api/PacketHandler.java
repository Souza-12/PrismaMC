package com.br.teagadev.prismamc.commons.bukkit.api.npc.api;

import org.bukkit.entity.Player;

interface PacketHandler {
   void createPackets();

   void sendShowPackets(Player var1);

   void sendHidePackets(Player var1, boolean var2);
}