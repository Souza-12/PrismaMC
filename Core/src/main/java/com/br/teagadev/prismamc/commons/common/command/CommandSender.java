package com.br.teagadev.prismamc.commons.common.command;

import java.util.UUID;

public interface CommandSender {
   UUID getUniqueId();

   boolean isPlayer();
}