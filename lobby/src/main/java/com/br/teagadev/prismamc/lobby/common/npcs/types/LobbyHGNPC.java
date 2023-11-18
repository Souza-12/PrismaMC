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

public class LobbyHGNPC extends NPCCommon {
	   private static Hologram playingHG;
	   private static Hologram playingMiniPrisma;
	   private static Hologram playingEventos;

	   public void create() {
	      this.createLocations();
	      Skin skin = SkinFetcher.fetchSkinByNick("EuFakeON", "7ac29642-e158-4a34-8909-53dbce9efcbd", true);
	      NPC npc = NPCLib.createNPC("HG", "§7§a", "47kypxflog", skin, Material.MUSHROOM_SOUP.getId());
	      npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.hg"));
	      skin = SkinFetcher.fetchSkinByNick("SpectayUS", "49f67001-bf18-4164-b37a-5f3e1ab460d5", true);
	      npc = NPCLib.createNPC("Evento", "§7§f", "v990g8u6wq", skin, Material.CAKE.getId());
	      npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.evento"));
	      skin = SkinFetcher.fetchSkinByNick("iHuxNOWLY", "43d75222-0290-48ea-a18a-440d77b16047", true);
	      npc = NPCLib.createNPC("MiniPrisma", "§7§c", "gytixcqcj7", skin, Material.DIAMOND_CHESTPLATE.getId());
	      npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.miniprisma"));
	      this.createHolograms();
	      this.registerListeners();
	   }

	   public void update() {
	      playingHG.setText("§e" + CommonsGeneral.getServersManager().getAmountOnlineHardcoreGames(false) + " jogando.");
	      playingMiniPrisma.setText("§e" + CommonsGeneral.getServersManager().getHardcoreGamesServer("MiniPrisma", 1).getOnlines() + " jogando.");
	      playingEventos.setText("§e" + CommonsGeneral.getServersManager().getHardcoreGamesServer("Evento", 1).getOnlines() + " jogando.");
	   }

	   public void createLocations() {
	      PluginConfiguration.createLocations(LobbyMain.getInstance(), new String[]{"bots.hg", "bots.evento", "bots.miniprisma"});
	   }

	   public void createHolograms() {
	      playingHG = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("HG").getLocationForHologram().clone().add(0.0D, 0.01D, 0.0D), "§e0 jogando.");
	      playingHG.spawn();
	      playingHG.addLineAbove("bot", "§bHG Mix");
	      playingMiniPrisma = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("MiniPrisma").getLocationForHologram().clone().add(0.0D, 0.01D, 0.0D), "§e0 jogando.");
	      playingMiniPrisma.spawn();
	      playingMiniPrisma.addLineAbove("bot", "§bMiniPrisma");
	      playingMiniPrisma.addLineAbove("bot", "§cOffline");
	      playingEventos = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("Evento").getLocationForHologram().clone().add(0.0D, 0.01D, 0.0D), "§e0 jogando.");
	      playingEventos.spawn();
	      playingEventos.addLineAbove("bot", "§bEvento");
	   }

	   public void registerListeners() {
	      Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
	         @EventHandler
	         public void onInteract(NPCInteractEvent event) {
	            LobbyUtility.handleInteract(event.getPlayer(), ServerType.getServer(event.getNpc().getCustomName()), BukkitMain.getServerType());
	         }
	      }, LobbyMain.getInstance());
	   }

	   public static Hologram getPlayingHG() {
	      return playingHG;
	   }

	   public static Hologram getPlayingMiniPrisma() {
	      return playingMiniPrisma;
	   }

	   public static Hologram getPlayingEventos() {
	      return playingEventos;
	   }
	}