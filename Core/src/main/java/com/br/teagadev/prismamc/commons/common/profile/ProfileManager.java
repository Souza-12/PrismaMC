package com.br.teagadev.prismamc.commons.common.profile;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileManager {
   private final Map<UUID, GamingProfile> profiles = new ConcurrentHashMap();

   public void addGamingProfile(UUID uniqueId, GamingProfile profile) {
      this.profiles.put(uniqueId, profile);
   }

   public void removeGamingProfile(UUID uniqueId) {
      this.profiles.remove(uniqueId);
   }

   public GamingProfile getGamingProfile(String nick) {
      UUID uniqueId = CommonsGeneral.getUUIDFetcher().getOfflineUUID(nick);
      return this.containsProfile(uniqueId) ? this.getGamingProfile(uniqueId) : null;
   }

   public GamingProfile getGamingProfile(UUID uniqueId) {
      return (GamingProfile)this.profiles.get(uniqueId);
   }

   public boolean containsProfile(UUID uniqueId) {
      return this.profiles.containsKey(uniqueId);
   }

   public Collection<GamingProfile> getGamingProfiles() {
      return this.profiles.values();
   }

   public boolean containsProfile(String name) {
      return this.containsProfile(CommonsGeneral.getUUIDFetcher().getOfflineUUID(name));
   }
}