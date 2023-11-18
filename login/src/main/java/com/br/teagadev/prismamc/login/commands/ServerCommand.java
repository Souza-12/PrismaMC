package com.br.teagadev.prismamc.login.commands;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.prismamc.login.LoginMain;
import com.br.teagadev.prismamc.login.manager.gamer.Gamer;
import com.br.teagadev.prismamc.login.manager.gamer.Gamer.AuthenticationType;
import com.br.teagadev.servercommunication.client.Client;

public class ServerCommand implements CommandClass {
	   @SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList<UUID> autorizados = new ArrayList();

	   @Command(
	      name = "login",
	      aliases = {"logar"}
	   )
	   public void login(BukkitCommandSender commandSender, String label, String[] args) {
	      if (commandSender.isPlayer()) {
	         if (args.length != 1) {
	            commandSender.sendMessage("§cUse o comando: /login <senha>");
	         } else {
	            Player player = commandSender.getPlayer();
	            if (!CommonsGeneral.getProfileManager().containsProfile(player.getUniqueId())) {
	               player.kickPlayer("§cOcorreu um erro, tente novamente.");
	            } else {
	               Gamer gamer = LoginMain.getManager().getGamer(player);
	               if (!gamer.isCaptchaConcluido()) {
	                  player.sendMessage("§cPara se autenticar Você precisa completar o Captcha.");
	               } else if (gamer.getAuthenticationType() == AuthenticationType.REGISTRAR) {
	                  player.sendMessage("§cVocê nao possuí um registro.");
	               } else if (gamer.isLogado()) {
	                  player.sendMessage("§aVocê já está autenticado.");
	               } else {
	                  String senha = args[0];
	                  if (!format(senha).equalsIgnoreCase(BukkitMain.getBukkitPlayer(player.getUniqueId()).getString(DataType.REGISTRO_SENHA))) {
	                     gamer.setTentativasFalhas(gamer.getTentativasFalhas() + 1);
	                     if (gamer.getTentativasFalhas() == 3) {
	                        player.kickPlayer("§cVocê errou a senha 3 vezes e foi expulso.");
	                     } else {
	                        player.sendMessage("§cSenha incorreta!");
	                     }

	                  } else {
	                     player.sendMessage("§aLogado com sucesso!");
	                     player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
	                     gamer.setLogado(true);
	                     Client.getInstance().getClientConnection().sendPacket((new CPacketCustomAction(player.getName())).type(PacketType.BUKKIT_SEND_INFO).field("player-authenticated"));
	                     LoginMain.runLater(() -> {
	                        if (player.isOnline()) {
	                           BukkitServerAPI.redirectPlayer(player, "Lobby");
	                        }

	                     }, 20L);
	                  }
	               }
	            }
	         }
	      }
	   }

	   @Command(
	      name = "register",
	      aliases = {"registrar"}
	   )
	   public void register(BukkitCommandSender commandSender, String label, String[] args) {
	      if (commandSender.isPlayer()) {
	         if (args.length != 2) {
	            commandSender.sendMessage("§cUse o comando: /register <senha> <senha>");
	         } else {
	            Player player = commandSender.getPlayer();
	            if (!CommonsGeneral.getProfileManager().containsProfile(player.getUniqueId())) {
	               player.kickPlayer("§cOcorreu um erro, tente novamente.");
	            } else {
	               Gamer gamer = LoginMain.getManager().getGamer(player);
	               if (!gamer.isCaptchaConcluido()) {
	                  player.sendMessage("§cPara se autenticar Você precisa completar o Captcha.");
	               } else if (gamer.getAuthenticationType() == AuthenticationType.LOGAR) {
	                  player.sendMessage("§cVocê já possui um registro.");
	               } else if (gamer.isLogado()) {
	                  player.sendMessage("§aVocê já está autenticado.");
	               } else {
	                  String senha = args[0];
	                  String senha1 = args[1];
	                  if (senha.length() < 4) {
	                     player.sendMessage("§cA sua senha é muito curta!");
	                  } else if (senha.length() > 20) {
	                     player.sendMessage("§cA sua senha é muito longa!");
	                  } else if (!senha.equals(senha1)) {
	                     player.sendMessage("§cAs duas senhas não săo iguais!");
	                  } else {
	                     String data = DateUtils.getActualDate(false);
	                     BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	                     bukkitPlayer.set(DataType.REGISTRO_SENHA, format(senha));
	                     bukkitPlayer.set(DataType.REGISTRO_DATA, data);
	                     player.sendMessage("§aRegistrado com sucesso!. ");
	                     player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
	                     gamer.setLogado(true);
	                     this.registrar(player, format(senha), data);
	                  }
	               }
	            }
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
	                  PluginConfiguration.setLocation(LoginMain.getInstance(), "spawn", player);
	                  LoginMain.setSpawn(PluginConfiguration.getLocation(LoginMain.getInstance(), "spawn"));
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
	            player.setGameMode(GameMode.ADVENTURE);
	         } else {
	            autorizados.add(player.getUniqueId());
	            player.sendMessage("§aVocê ativou o modo construçăo.");
	            player.setGameMode(GameMode.CREATIVE);
	         }

	      }
	   }

	   private void registrar(Player player, String senha, String dataRegistro) {
	      Client.getInstance().getClientConnection().sendPacket((new CPacketCustomAction(player.getName())).type(PacketType.BUKKIT_SEND_INFO).field("player-authenticated"));
	      LoginMain.runAsync(() -> {
	         BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	         bukkitPlayer.set(DataType.REGISTRO_SENHA, senha);
	         bukkitPlayer.set(DataType.REGISTRO_DATA, dataRegistro);

	         try {
	            Connection connection = CommonsGeneral.getMySQL().getConnection();
	            Throwable var5 = null;

	            try {
	               PreparedStatement insert = connection.prepareStatement(bukkitPlayer.getDataHandler().createInsertIntoStringQuery(DataCategory.REGISTER));
	               insert.execute();
	               insert.close();
	               bukkitPlayer.getDataHandler().saveCategory(DataCategory.REGISTER);
	            } catch (Throwable var15) {
	               var5 = var15;
	               throw var15;
	            } finally {
	               if (connection != null) {
	                  if (var5 != null) {
	                     try {
	                        connection.close();
	                     } catch (Throwable var14) {
	                        var5.addSuppressed(var14);
	                     }
	                  } else {
	                     connection.close();
	                  }
	               }

	            }
	         } catch (SQLException var17) {
	            var17.printStackTrace();
	         }

	      });
	      LoginMain.runLater(() -> {
	         if (player.isOnline()) {
	            BukkitServerAPI.redirectPlayer(player, "Lobby");
	         }

	      }, 20L);
	   }

	   public static void kickPlayer(Player player, String mensagem) {
	      BukkitMain.runLater(() -> {
	         player.kickPlayer(mensagem);
	      });
	   }

	   public static String format(String string) {
	      try {
	         MessageDigest md = MessageDigest.getInstance("MD5");
	         byte[] array = md.digest(string.getBytes());
	         StringBuffer sb = new StringBuffer();

	         for(int i = 0; i < array.length; ++i) {
	            sb.append(Integer.toHexString(array[i] & 255 | 256).substring(1, 3));
	         }

	         return sb.toString();
	      } catch (NoSuchAlgorithmException var5) {
	         return null;
	      }
	   }
	}