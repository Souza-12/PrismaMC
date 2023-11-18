package com.br.teagadev.prismamc.login.manager.gamer;

import java.util.UUID;
import org.bukkit.entity.Player;

public class Gamer {
	   private Player player;
	   private String nick;
	   private UUID uniqueId;
	   private boolean captchaConcluido;
	   private boolean logado;
	   private int tentativasFalhas;
	   private int secondsCaptcha;
	   private int secondsLogin;
	   private int secondsConnect;
	   private Long timestamp;
	   private Gamer.AuthenticationType authenticationType;

	   public Gamer(Player player, Gamer.AuthenticationType authenticationType) {
	      this.setPlayer(player);
	      this.setNick(player.getName());
	      this.setAuthenticationType(authenticationType);
	      this.setUniqueId(player.getUniqueId());
	      this.setCaptchaConcluido(false);
	      this.setLogado(false);
	      this.setTentativasFalhas(0);
	      this.setSecondsCaptcha(-2);
	      this.setSecondsLogin(-1);
	      this.setSecondsConnect(-1);
	      this.setTimestamp(System.currentTimeMillis());
	   }

	   public void handleLogin(Player player) {
	      this.setPlayer(player);
	      this.refresh();
	   }

	   public void refresh() {
	      this.setCaptchaConcluido(false);
	      this.setLogado(false);
	      this.setTentativasFalhas(0);
	      this.setSecondsCaptcha(-2);
	      this.setSecondsLogin(-1);
	      this.setSecondsConnect(-1);
	   }

	   public void addSecondsCaptcha() {
	      this.setSecondsCaptcha(this.getSecondsCaptcha() + 1);
	   }

	   public void addSecondsLogin() {
	      this.setSecondsLogin(this.getSecondsLogin() + 1);
	   }

	   public void addSecondsConnect() {
	      this.setSecondsConnect(this.getSecondsConnect() + 1);
	   }

	   public Player getPlayer() {
	      return this.player;
	   }

	   public String getNick() {
	      return this.nick;
	   }

	   public UUID getUniqueId() {
	      return this.uniqueId;
	   }

	   public boolean isCaptchaConcluido() {
	      return this.captchaConcluido;
	   }

	   public boolean isLogado() {
	      return this.logado;
	   }

	   public int getTentativasFalhas() {
	      return this.tentativasFalhas;
	   }

	   public int getSecondsCaptcha() {
	      return this.secondsCaptcha;
	   }

	   public int getSecondsLogin() {
	      return this.secondsLogin;
	   }

	   public int getSecondsConnect() {
	      return this.secondsConnect;
	   }

	   public Long getTimestamp() {
	      return this.timestamp;
	   }

	   public Gamer.AuthenticationType getAuthenticationType() {
	      return this.authenticationType;
	   }

	   public void setPlayer(Player player) {
	      this.player = player;
	   }

	   public void setNick(String nick) {
	      this.nick = nick;
	   }

	   public void setUniqueId(UUID uniqueId) {
	      this.uniqueId = uniqueId;
	   }

	   public void setCaptchaConcluido(boolean captchaConcluido) {
	      this.captchaConcluido = captchaConcluido;
	   }

	   public void setLogado(boolean logado) {
	      this.logado = logado;
	   }

	   public void setTentativasFalhas(int tentativasFalhas) {
	      this.tentativasFalhas = tentativasFalhas;
	   }

	   public void setSecondsCaptcha(int secondsCaptcha) {
	      this.secondsCaptcha = secondsCaptcha;
	   }

	   public void setSecondsLogin(int secondsLogin) {
	      this.secondsLogin = secondsLogin;
	   }

	   public void setSecondsConnect(int secondsConnect) {
	      this.secondsConnect = secondsConnect;
	   }

	   public void setTimestamp(Long timestamp) {
	      this.timestamp = timestamp;
	   }

	   public void setAuthenticationType(Gamer.AuthenticationType authenticationType) {
	      this.authenticationType = authenticationType;
	   }

	   public static enum AuthenticationType {
	      REGISTRAR,
	      LOGAR;
	   }
	}