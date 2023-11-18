package com.br.teagadev.prismamc.lobby.common.npcs.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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

public class LobbyPvPNPC extends NPCCommon {
	   private static Hologram playingArena;
	   private static Hologram playingFPS;
	   private static Hologram playingLavaChallenge;

	   public void create() {
	      this.createLocations();
	      Skin skin = SkinFetcher.fetchSkinByNick("Kongamitow", "e13fb198-4ecb-41f4-93af-5cb0acd390f7", true);
	      NPC npc = NPCLib.createNPC("Arena", "§7", "py3lz629zv", skin, Material.STONE_SWORD.getId());
	      npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.arena"));
	      skin = SkinFetcher.fetchSkinByNick("iibzNowly", "d212253c-3388-4653-91b7-7379dbd4ca4d", true);
	      npc = NPCLib.createNPC("FPS", "§7§1", "rh7rgj4q7t", skin, Material.DIAMOND_SWORD.getId());
	      npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.fps"));
	      skin = SkinFetcher.fetchSkinByNick("Procurador", "ca163766-6db2-4c42-b33a-a574fbaf6cff", true);
	      npc = NPCLib.createNPC("LavaChallenge", "§7§2", "5hdtvact0p", skin, Material.LAVA_BUCKET.getId());
	      npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.lavachallenge"));
	      this.createHolograms();
	      this.registerListeners();
	   }

	   public void update() {
	      playingArena.setText("§e" + CommonsGeneral.getServersManager().getNetworkServer("arena").getOnlines() + " jogando.");
	      playingFPS.setText("§e" + CommonsGeneral.getServersManager().getNetworkServer("fps").getOnlines() + " jogando.");
	      playingLavaChallenge.setText("§e" + CommonsGeneral.getServersManager().getNetworkServer("lavachallenge").getOnlines() + " jogando.");
	   }

	   public void createLocations() {
	      PluginConfiguration.createLocations(LobbyMain.getInstance(), new String[]{"bots.arena", "bots.fps", "bots.lavachallenge"});
	   }

	   public void createHolograms() {
	      playingArena = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("Arena").getLocationForHologram().clone().add(0.0D, 0.01D, 0.0D), "§e0 jogando.");
	      playingArena.spawn();
	      playingArena.addLineAbove("bot", "§bArena");
	      playingFPS = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("FPS").getLocationForHologram().clone().add(0.0D, 0.01D, 0.0D), "§e0 jogando.");
	      playingFPS.spawn();
	      playingFPS.addLineAbove("bot", "§bFPS");
	      playingLavaChallenge = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("LavaChallenge").getLocationForHologram().clone().add(0.0D, 0.01D, 0.0D), "§e0 jogando.");
	      playingLavaChallenge.spawn();
	      playingLavaChallenge.addLineAbove("bot", "§bLava");
	   }

	   public void registerListeners() {
	      Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
	         @EventHandler
	         public void onInteract(NPCInteractEvent event) {
	            LobbyUtility.handleInteract(event.getPlayer(), ServerType.getServer(event.getNpc().getCustomName()), BukkitMain.getServerType());
	         }
	      }, LobbyMain.getInstance());
	   }

	   public static Hologram getPlayingArena() {
	      return playingArena;
	   }

	   public static Hologram getPlayingFPS() {
	      return playingFPS;
	   }

	   public static Hologram getPlayingLavaChallenge() {
	      return playingLavaChallenge;
	   }
	}