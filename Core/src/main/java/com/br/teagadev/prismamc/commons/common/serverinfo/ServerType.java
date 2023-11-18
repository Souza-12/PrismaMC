package com.br.teagadev.prismamc.commons.common.serverinfo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public enum ServerType {
   BUNGEECORD("BungeeCord", 2),
   LOGIN("Login", 2),
   LOBBY("Lobby", 2, new String[]{"hub"}),
   LOBBY_HARDCOREGAMES("LobbyHardcoreGames", 2, new String[]{"LobbyHG", "lhg", "hubhg"}),
   LOBBY_PVP("LobbyPvP", 2, new String[]{"lpvp", "hubpvp", "LobbyPvP"}),
   LOBBY_DUELS("LobbyDuels", 2, new String[]{"lduel", "LobbyDuels", "hubduel"}),
   DUELS_GLADIATOR("Gladiator", 2, new String[]{"glad", "glading"}),
   DUELS_SIMULATOR("Simulator", 2, new String[]{"sim", "simulation"}),
   PARTY_GAMES("PartyGames", 2, new String[]{"pgames", "party", "gamesparty"}),
   PVP_ARENA("Arena", 2),
   PVP_FPS("FPS", 2),
   PVP_LAVACHALLENGE("LavaChallenge", 2),
   GLADIATOR("Gladiator", 3, new String[]{"glad"}),
   HARDCORE_GAMES("HardcoreGames", 2, new String[]{"hg", "HungerGames"}),
   MINIPRISMA("MiniPrisma", 3, new String[]{"miniprisma", "mini"}),
   EVENTO("Evento", 3, new String[]{"evnt", "evento"}),
   SKYWARS("SkyWars", 2, new String[]{"sw"}),
   BEDWARS("BedWars", 2, new String[]{"bw"}),
   THEBRIDGE("TheBridge", 2, new String[]{"tb"}),
   UNKNOWN("Unknown");

   private final String name;
   private final List<String> aliases;
   private final int secondsToStabilize;
   private final int secondsUpdateStatus;

   private ServerType(String name, int secondsToStabilize, int secondsUpdateStatus, String... aliases) {
      this.name = name;
      this.aliases = Arrays.asList(aliases);
      this.secondsToStabilize = secondsToStabilize;
      this.secondsUpdateStatus = secondsUpdateStatus;
   }

   private ServerType(String name, int secondsToStabilize) {
      this(name, secondsToStabilize, 2, "Unknown");
   }

   private ServerType(String name) {
      this(name, 3, 2, "Unknown");
   }

   private ServerType(String name, String... aliases) {
      this(name, 3, 2, aliases);
   }

   private ServerType(String name, int secondsUpdateStatus, String... aliases) {
      this(name, 3, secondsUpdateStatus, aliases);
   }

   public static ServerType getServer(String serverName) {
      serverName = serverName.replaceAll("\\d", "");
      ServerType finded = UNKNOWN;
      ServerType[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ServerType servers = var2[var4];
         if (servers.getName().equalsIgnoreCase(serverName)) {
            finded = servers;
            break;
         }

         if (servers.containsAlias(serverName)) {
            finded = servers;
            break;
         }

         if (servers.getName().startsWith(serverName)) {
            finded = servers;
            break;
         }
      }

      return finded;
   }

   public boolean useActionItem() {
      if (this.isLobby()) {
         return true;
      } else if (this == SKYWARS) {
         return true;
      } else if (this.isPvP()) {
         return true;
      } else {
         return this == GLADIATOR ? true : this.isHardcoreGames();
      }
   }

   public boolean useMenuListener() {
      if (this.isLobby()) {
         return true;
      } else if (this == SKYWARS) {
         return true;
      } else {
         return this.isPvP() ? true : this.isHardcoreGames();
      }
   }

   public boolean isLobby() {
      return this.getName().startsWith("Lobby");
   }

   public boolean isPvP() {
      return this.isPvP(false);
   }

   public boolean isPvP(boolean lobby) {
      if (lobby && this == LOBBY_PVP) {
         return true;
      } else {
         return this == PVP_ARENA || this == PVP_FPS || this == PVP_LAVACHALLENGE;
      }
   }

   public boolean isHardcoreGames() {
      return this.isHardcoreGames(false);
   }

   public boolean isHardcoreGames(boolean lobby) {
      if (lobby && this == LOBBY_HARDCOREGAMES) {
         return true;
      } else {
         return this == HARDCORE_GAMES || this == EVENTO || this == MINIPRISMA;
      }
   }

   public boolean containsAlias(String serverName) {
      if (this.getAliases() == null) {
         return false;
      } else {
         boolean finded = false;
         Iterator var3 = this.getAliases().iterator();

         while(var3.hasNext()) {
            String alias = (String)var3.next();
            if (alias.equalsIgnoreCase(serverName)) {
               finded = true;
               break;
            }
         }

         return finded;
      }
   }

   public boolean useSuffixRank() {
      return this.isPvP(true) || this.isHardcoreGames(true);
   }

   public String getName() {
      return this.name;
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public int getSecondsToStabilize() {
      return this.secondsToStabilize;
   }

   public int getSecondsUpdateStatus() {
      return this.secondsUpdateStatus;
   }
}