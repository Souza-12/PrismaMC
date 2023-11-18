package com.br.teagadev.prismamc.lobby.common.scoreboard.animating;

public class ActualAnimation extends FramesAnimation {
	   protected String context;
	   protected String normalFormat;
	   protected String highlightFormat;

	   public ActualAnimation(String context, String normalFormat, String highlightFormat) {
	      this.context = context;
	      this.normalFormat = normalFormat;
	      this.highlightFormat = highlightFormat;
	      this.generateFrames();
	   }

	   protected void generateFrames() {
	      for(int i = 0; i < this.context.length(); ++i) {
	         if (this.context.charAt(i) != ' ') {
	            String str1 = this.normalFormat + this.context.substring(0, i) + this.highlightFormat + this.context.charAt(i) + this.normalFormat + this.context.substring(i + 1, this.context.length());
	            this.addFrame(str1);
	         } else {
	            this.addFrame(this.normalFormat + this.context + " ");
	         }
	      }

	   }

	   public String getContext() {
	      return this.context;
	   }

	   public String getNormalColor() {
	      return this.normalFormat;
	   }

	   public String getHighlightColor() {
	      return this.highlightFormat;
	   }
	}