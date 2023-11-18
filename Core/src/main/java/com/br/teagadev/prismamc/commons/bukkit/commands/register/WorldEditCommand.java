package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.bukkit.worldedit.Constructions;
import com.br.teagadev.prismamc.commons.bukkit.worldedit.WorldEditManager;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorldEditCommand implements CommandClass {
   @Command(
      name = "worldedit",
      aliases = {"we"},
      groupsToUse = {Groups.ADMIN}
   )
   public void worldedit(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (args.length == 0) {
            commandSender.sendMessage("");
            commandSender.sendMessage("§cUse: /worldedit <Machado/Undo>");
            commandSender.sendMessage("§cUse: /worldedit set <ID>");
            commandSender.sendMessage("§cUse: /worldedit setblockpertick <Quantidade>");
            commandSender.sendMessage("");
         } else {
            Player player = commandSender.getPlayer();
            if (args.length == 1) {
               if (args[0].equalsIgnoreCase("machado")) {
                  player.getInventory().addItem(new ItemStack[]{(new ItemBuilder()).type(Material.WOOD_AXE).name("§e§lWORLDEDIT").build()});
                  player.sendMessage("§e§lWORLDEDIT §fVocê recebeu o Machado.");
               } else if (args[0].equalsIgnoreCase("undo")) {
                  if (!WorldEditManager.hasRollingConstructionByUUID(player.getUniqueId())) {
                     commandSender.sendMessage("§e§lWORLDEDIT §fVocê não possuí uma construção.");
                     return;
                  }

                  Constructions construction = WorldEditManager.getConstructionByUUID(player.getUniqueId());
                  if (construction.isFinished()) {
                     commandSender.sendMessage("§e§lWORLDEDIT §fA construção ainda está em andamento.");
                     return;
                  }

                  if (construction.isResetando()) {
                     commandSender.sendMessage("§e§lWORLDEDIT §fA construção já está em sendo resetada.");
                     return;
                  }

                  construction.startRegress();
                  commandSender.sendMessage("§e§lWORLDEDIT §fRetirando blocos...");
               } else {
                  commandSender.sendMessage("");
                  commandSender.sendMessage("§cUse: /worldedit <Machado/Undo>");
                  commandSender.sendMessage("§cUse: /worldedit set <ID>");
                  commandSender.sendMessage("§cUse: /worldedit setblockpertick <Quantidade>");
                  commandSender.sendMessage("");
               }
            } else if (args.length == 2) {
               if (args[0].contentEquals("setblockpertick")) {
                  if (!StringUtility.isInteger(args[1])) {
                     commandSender.sendMessage("§cUse: /worldedit setblockpertick <Quantidade>");
                     return;
                  }

                  if (!WorldEditManager.hasRollingConstructionByUUID(player.getUniqueId())) {
                     commandSender.sendMessage("§e§lWORLDEDIT §fVocê não possuí uma construção.");
                     return;
                  }

                  int quantia = Integer.valueOf(args[1]);
                  if (quantia > 1000) {
                     commandSender.sendMessage("§e§lWORLDEDIT §fValor máximo de apenas 1,000 blocos.");
                     return;
                  }

                  Constructions construction = WorldEditManager.getConstructionByUUID(player.getUniqueId());
                  construction.setBlocksPerTick(quantia);
                  commandSender.sendMessage("§e§lWORLDEDIT §fValor alterado.");
               } else if (args[0].equalsIgnoreCase("set")) {
                  String idsOriginal = args[1];
                  String ids = args[1].replaceAll(",", "");
                  if (!StringUtility.isInteger(ids)) {
                     commandSender.sendMessage("§cUse: /worldedit set <ID>");
                     return;
                  }

                  if (WorldEditManager.hasRollingConstructionByUUID(player.getUniqueId()) && !WorldEditManager.getConstructionByUUID(player.getUniqueId()).isFinished()) {
                     commandSender.sendMessage("§e§lWORLDEDIT §fVocê já possuí uma construção em andamento.");
                     return;
                  }

                  if (WorldEditManager.continueEdit(player)) {
                     List<Material> materiaisIds = new ArrayList();
                     if (idsOriginal.contains(",")) {
                        boolean error = false;
                        String[] var9 = idsOriginal.split(",");
                        int var10 = var9.length;
                        int var11 = 0;

                        while(true) {
                           if (var11 < var10) {
                              label232: {
                                 String string = var9[var11];

                                 try {
                                    materiaisIds.add(Material.getMaterial(Integer.valueOf(string)));
                                 } catch (NullPointerException var22) {
                                    error = true;
                                    break label232;
                                 }

                                 ++var11;
                                 continue;
                              }
                           }

                           if (error) {
                              player.sendMessage("§e§lWORLDEDIT §fOcorreu um erro ao tentar encontrar o material.");
                              return;
                           }
                           break;
                        }
                     } else {
                        try {
                           materiaisIds.add(Material.getMaterial(Integer.valueOf(args[1])));
                        } catch (NullPointerException var19) {
                           player.sendMessage("§e§lWORLDEDIT §fOcorreu um erro ao tentar encontrar o material.");
                           return;
                        }
                     }

                     player.sendMessage("§e§lWORLDEDIT §fProcessando blocos...");
                     List locations = null;

                     try {
                        locations = WorldEditManager.getLocationsFromTwoPoints(WorldEditManager.getPos1(player), WorldEditManager.getPos2(player));
                     } catch (Exception var20) {
                        player.sendMessage("§e§lWORLDEDIT §fErro ao processar os blocos...");
                        return;
                     } finally {
                        player.sendMessage("§e§lWORLDEDIT §e" + StringUtility.formatValue(locations.size()) + " §fblocos processados.");
                     }

                     WorldEditManager.addConstructionByUUID(player, locations);
                     WorldEditManager.getConstructionByUUID(player.getUniqueId()).setBlocksToSet(materiaisIds);
                     WorldEditManager.getConstructionByUUID(player.getUniqueId()).start();
                     locations.clear();
                     materiaisIds.clear();
                  }
               } else {
                  commandSender.sendMessage("");
                  commandSender.sendMessage("§cUse: /worldedit <Machado/Undo>");
                  commandSender.sendMessage("§cUse: /worldedit set <ID>");
                  commandSender.sendMessage("§cUse: /worldedit setblockpertick <Quantidade>");
                  commandSender.sendMessage("");
               }
            } else {
               commandSender.sendMessage("");
               commandSender.sendMessage("§cUse: /worldedit <Machado/Undo>");
               commandSender.sendMessage("§cUse: /worldedit set <ID>");
               commandSender.sendMessage("§cUse: /worldedit setblockpertick <Quantidade>");
               commandSender.sendMessage("");
            }

         }
      }
   }
}