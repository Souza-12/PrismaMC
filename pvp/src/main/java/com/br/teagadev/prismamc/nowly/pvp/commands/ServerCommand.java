package com.br.teagadev.prismamc.nowly.pvp.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.nowly.pvp.PvPMain;
import com.br.teagadev.prismamc.nowly.pvp.manager.combatlog.CombatLogManager;
import com.br.teagadev.prismamc.nowly.pvp.mode.arena.ArenaMode;
import com.br.teagadev.prismamc.nowly.pvp.mode.fps.FPSMode;
import com.br.teagadev.prismamc.nowly.pvp.mode.lavachallenge.LavaChallengeMode;

public class ServerCommand implements CommandClass {
	   public static ArrayList<UUID> autorizados = new ArrayList();

	   @Command(
	      name = "spawn"
	   )
	   public void spawn(BukkitCommandSender commandSender, String label, String[] args) {
	      if (commandSender.isPlayer()) {
	         Player player = commandSender.getPlayer();
	         if (CombatLogManager.getCombatLog(player).isFighting()) {
	            player.sendMessage("§cVocê não pode ir para o spawn em combate!");
	         } else {
	            Bukkit.getServer().getPluginManager().callEvent(new PlayerRequestEvent(player, "teleport-spawn"));
	         }
	      }
	   }

	   @Command(
	      name = "setloc",
	      groupsToUse = {Groups.DONO}
	   )
	   public void setloc(BukkitCommandSender commandSender, String label, String[] args) {
	      if (commandSender.isPlayer()) {
	         if (args.length == 0) {
	            commandSender.sendMessage("§cUse: /setloc spawn");
	         } else {
	            Player player = commandSender.getPlayer();
	            if (args.length == 1) {
	               if (args[0].equalsIgnoreCase("spawn")) {
	                  PluginConfiguration.setLocation(PvPMain.getInstance(), "spawn", player);
	                  switch(BukkitMain.getServerType()) {
	                  case PVP_FPS:
	                     FPSMode.setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
	                     break;
	                  case PVP_LAVACHALLENGE:
	                     LavaChallengeMode.setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
	                     break;
	                  case PVP_ARENA:
	                     ArenaMode.setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
	                  }

	                  commandSender.sendMessage("§aSpawn setado!");
	               } else {
	                  commandSender.sendMessage("§cUse: /setloc spawn");
	               }
	            } else {
	               commandSender.sendMessage("§cUse: /setloc spawn");
	            }

	         }
	      }
	   }

	   @Command(
	      name = "build",
	      aliases = {"b"},
	      groupsToUse = {Groups.ADMIN}
	   )
	   public void build(BukkitCommandSender commandSender, String label, String[] args) {
	      if (commandSender.isPlayer()) {
	         Player player = commandSender.getPlayer();
	         if (autorizados.contains(player.getUniqueId())) {
	            autorizados.remove(player.getUniqueId());
	            player.sendMessage("§aVocê desativou o modo construçăo.");
	         } else {
	            autorizados.add(player.getUniqueId());
	            player.sendMessage("§aVocê ativou o modo construçăo.");
	         }

	      }
	   }
	}