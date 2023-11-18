package com.br.teagadev.prismamc.login.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import com.br.teagadev.prismamc.commons.common.connections.mysql.MySQLManager;
import com.br.teagadev.prismamc.login.LoginMain;
import com.br.teagadev.prismamc.login.listener.GeneralListeners;
import com.br.teagadev.prismamc.login.manager.gamer.Gamer;
import com.br.teagadev.prismamc.login.manager.gamer.Gamer.AuthenticationType;


public class LoginManager {
	   @SuppressWarnings({ "rawtypes", "unchecked" })
	private final HashMap<UUID, Gamer> gamers = new HashMap();

	   public Gamer getGamer(Player player) {
	      return (Gamer)this.getGamers().get(player.getUniqueId());
	   }

	   public void addGamer(Player player, AuthenticationType authenticationType) {
	      if (this.getGamers().containsKey(player.getUniqueId())) {
	         ((Gamer)this.getGamers().get(player.getUniqueId())).handleLogin(player);
	      } else {
	         this.getGamers().put(player.getUniqueId(), new Gamer(player, authenticationType));
	      }
	   }

	   public void removeGamers(boolean addToQueue) {
	      @SuppressWarnings({ "rawtypes", "unchecked" })
		List<UUID> toRemove = new ArrayList();
	      @SuppressWarnings("rawtypes")
		Iterator var3 = this.getGamers().values().iterator();

	      while(var3.hasNext()) {
	         Gamer gamers = (Gamer)var3.next();
	         if (System.currentTimeMillis() > gamers.getTimestamp() + TimeUnit.MINUTES.toMillis(10L) && !gamers.getPlayer().isOnline()) {
	            toRemove.add(gamers.getUniqueId());
	         }
	      }

	      if (toRemove.size() != 0) {
	         var3 = toRemove.iterator();

	         while(var3.hasNext()) {
	            UUID uuid = (UUID)var3.next();
	            String nick = ((Gamer)this.getGamers().get(uuid)).getNick();
	            this.getGamers().remove(uuid);
	            if (addToQueue) {
	               GeneralListeners.getRunnableQueue().add(() -> {
	                  LoginMain.runAsync(() -> {
	                     if (MySQLManager.contains("premium_map", "nick", nick) && !MySQLManager.contains("accounts", "nick", nick)) {
	                        MySQLManager.executeUpdate(new String[]{"DELETE FROM premium_map WHERE nick='" + nick + "';"});
	                     }

	                  });
	               });
	            } else {
	               LoginMain.runAsync(() -> {
	                  if (MySQLManager.contains("premium_map", "nick", nick) && !MySQLManager.contains("accounts", "nick", nick)) {
	                     MySQLManager.executeUpdate(new String[]{"DELETE FROM premium_map WHERE nick='" + nick + "';"});
	                  }

	               });
	            }
	         }
	      }

	      toRemove.clear();
	   }

	   public HashMap<UUID, Gamer> getGamers() {
	      return this.gamers;
	   }
	}