package com.br.teagadev.prismamc.lobby.common.npcs.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.RandomStringUtils;
import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.Hologram;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.HologramAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.NPCLib;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.NPCManager;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.api.NPC;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.events.NPCInteractEvent;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.commons.common.utility.skin.Skin;
import com.br.teagadev.prismamc.commons.common.utility.skin.SkinFetcher;
import com.br.teagadev.prismamc.lobby.LobbyMain;
import com.br.teagadev.prismamc.lobby.common.LobbyUtility;
import com.br.teagadev.prismamc.lobby.common.npcs.NPCCommon;

import lombok.Getter;

public class LobbyNPC extends NPCCommon {
	   private static Hologram playingHG;
	   private static Hologram playingPvP;
	   private static Hologram playingDuels;

	   public void create() {
	      this.createLocations();
	      String npcProvider = "§8[NPC] ";
	      Skin skin = SkinFetcher.fetchSkinByNick("macacoenzo", "8bd87c2a-bd4d-4c23-9445-007c91bc1ab5", true);
	      NPC npc = NPCLib.createNPC("HG", "§7", "vkxz6shs6d", skin, Material.MUSHROOM_SOUP.getId());
	      npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.hg"));
	      skin = SkinFetcher.fetchSkinByNick("EuSnow", "506b4e90-7718-4ab0-80d8-a1570a7e1d28", true);
	      npc = NPCLib.createNPC("PvP", "§7§5", "0gy1pyow5x", skin, Material.IRON_CHESTPLATE.getId());
	      npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.pvp"));
	      skin = SkinFetcher.fetchSkinByNick("1cap", "21420052-3a6c-4bf2-bc1d-8b10e8c81e3c", true);
	      npc = NPCLib.createNPC("Duels", "§7§0", "826ejkt171", skin, Material.DIAMOND_SWORD.getId());
	      npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.duels"));
	      this.createHolograms();
	      this.registerListeners();
	   }

	   public void update() {
	      playingHG.setText("§e" + CommonsGeneral.getServersManager().getAmountOnlineHardcoreGames(true) + " jogando.");
	      playingPvP.setText("§e" + CommonsGeneral.getServersManager().getAmountOnlinePvP(true) + " jogando.");
	      playingDuels.setText("§e0 jogando.");
	   }

	   public void createLocations() {
	      PluginConfiguration.createLocations(LobbyMain.getInstance(), new String[]{"bots.hg", "bots.pvp", "bots.duels"});
	   }

	   public void createHolograms() {
	      playingHG = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("HG").getLocationForHologram().clone().add(0.0D, 0.01D, 0.0D), "§e0 jogando.");
	      playingHG.spawn();
	      playingHG.addLineAbove("bot", "§bHG");
	      playingPvP = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("PvP").getLocationForHologram().clone().add(0.0D, 0.01D, 0.0D), "§e0 jogando.");
	      playingPvP.spawn();
	      playingPvP.addLineAbove("bot", "§bPvP");
	      playingDuels = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("Duels").getLocationForHologram().clone().add(0.0D, 0.01D, 0.0D), "§e0 jogando.");
	      playingDuels.spawn();
	      playingDuels.addLineAbove("bot", "§bDuels");
	   }

	   public void registerListeners() {
	      Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
	         @EventHandler
	         public void onInteract(NPCInteractEvent event) {
	            ServerType serverClicked = ServerType.getServer(event.getNpc().getCustomName());
	            if (event.getNpc().getCustomName().equalsIgnoreCase("PvP")) {
	               serverClicked = ServerType.LOBBY_PVP;
	            } else if (event.getNpc().getCustomName().equalsIgnoreCase("HG")) {
	               serverClicked = ServerType.LOBBY_HARDCOREGAMES;
	            } else if (event.getNpc().getCustomName().equalsIgnoreCase("Duels")) {
	               serverClicked = ServerType.LOBBY_DUELS;
	            }

	            LobbyUtility.handleInteract(event.getPlayer(), serverClicked, BukkitMain.getServerType());
	         }
	      }, LobbyMain.getInstance());
	   }

	   public static Hologram getPlayingHG() {
	      return playingHG;
	   }

	   public static Hologram getPlayingPvP() {
	      return playingPvP;
	   }

	   public static Hologram getPlayingDuels() {
	      return playingDuels;
	   }
	}