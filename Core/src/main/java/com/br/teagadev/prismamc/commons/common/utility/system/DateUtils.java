package com.br.teagadev.prismamc.commons.common.utility.system;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
   public static String getActualDate() {
      return getActualDate(true);
   }

   public static String getActualDate(boolean withFormat) {
      Calendar cal = Calendar.getInstance();
      int year = cal.get(1);
      int mon = cal.get(2) + 1;
      int day = cal.get(5);
      int hour = cal.get(11);
      int min = cal.get(12);
      int sec = cal.get(13);
      StringBuilder newString = new StringBuilder();
      newString.append(day < 10 ? "0" : "").append(day).append("/");
      newString.append(mon < 10 ? "0" : "").append(mon).append("/");
      newString.append(year).append(" - ");
      newString.append(hour < 10 ? "0" : "").append(hour).append(":");
      newString.append(min < 10 ? "0" : "").append(min).append(":");
      newString.append(sec);
      cal = null;
      return (withFormat ? "[" : "") + newString + (withFormat ? "] " : "");
   }

   public static String formatTime(Integer i) {
      if (i >= 60) {
         Integer time = i / 60;
         String add = "";
         if (time > 1) {
            add = "s";
         }

         return time + " minuto" + add;
      } else {
         String add = "";
         if (i > 1) {
            add = "s";
         }

         return i + " segundo" + add;
      }
   }

   public static String formatSecondsScore(Integer i) {
      return formatSecondsScore(i, true);
   }

   public static String formatSecondsScore(Integer i, boolean useSuffix) {
      int minutes = i / 60;
      int seconds = i % 60;
      String disMinu = "" + minutes;
      String disSec = (seconds < 10 ? "0" : "") + seconds;
      String formattedTime = disMinu + ":" + disSec;
      if (useSuffix) {
         formattedTime = disMinu + ":" + disSec;
      }

      return formattedTime;
   }

   public static String formatSeconds(Integer i) {
      return formatSeconds(i, true);
   }

   public static String formatSeconds(Integer i, boolean useSuffix) {
      int minutes = i / 60;
      int seconds = i % 60;
      String disMinu = "" + minutes;
      String disSec = (seconds < 10 ? "0" : "") + seconds;
      String formattedTime = disMinu + ":" + disSec;
      if (useSuffix) {
         formattedTime = disMinu + "m " + disSec + "s";
      }

      return formattedTime;
   }

   public static String getElapsed(Long started, Long nanoStarted) {
      Long now = System.currentTimeMillis();
      Long actualNano = System.nanoTime();
      long elapsed = now - started;
      long seconds = elapsed / 1000L % 60L;
      long minutes = elapsed / 60000L % 60L;
      long hours = elapsed / 3600000L % 24L;
      long days = elapsed / 86400000L;
      StringBuilder stringBuilder = new StringBuilder();
      if (days != 0L) {
         stringBuilder.append(days).append("dia").append(days > 1L ? "s" : "").append(", ");
      }

      if (hours != 0L) {
         stringBuilder.append(hours).append("hora").append(days > 1L ? "s" : "").append(", ");
      }

      if (minutes != 0L) {
         if (seconds != 0L) {
            stringBuilder.append(minutes).append("minuto").append(days > 1L ? "s" : "").append(" e ").append(seconds).append(" segundo").append(seconds > 1L ? "s" : "");
         } else {
            stringBuilder.append(minutes).append("minuto").append(days > 1L ? "s" : "");
         }
      } else {
         stringBuilder.append(seconds).append(" segundo").append(seconds > 1L ? "s" : "");
      }

      if (days == 0L && hours == 0L && minutes == 0L && seconds == 0L) {
         String ms = String.valueOf(started - now).replaceAll("-", "");
         return ms.equals("0") ? String.valueOf(nanoStarted - actualNano).replace("-", "") + "ns" : ms + "ms";
      } else {
         return stringBuilder.toString().replaceAll("-", "") + ".";
      }
   }

   private static String fromLong(long lenth) {
      int days = (int)TimeUnit.SECONDS.toDays(lenth);
      long hours = TimeUnit.SECONDS.toHours(lenth) - (long)(days * 24);
      long minutes = TimeUnit.SECONDS.toMinutes(lenth) - TimeUnit.SECONDS.toHours(lenth) * 60L;
      long seconds = TimeUnit.SECONDS.toSeconds(lenth) - TimeUnit.SECONDS.toMinutes(lenth) * 60L;
      String totalDay = days + (days == 1 ? " dia " : " dias ");
      String totalHours = hours + (hours == 1L ? " hora " : " horas ");
      String totalMinutes = minutes + (minutes == 1L ? " minuto " : " minutos ");
      String totalSeconds = seconds + (seconds == 1L ? " segundo" : " segundos");
      if (days == 0) {
         totalDay = "";
      }

      if (hours == 0L) {
         totalHours = "";
      }

      if (minutes == 0L) {
         totalMinutes = "";
      }

      if (seconds == 0L) {
         totalSeconds = "";
      }

      String restingTime = totalDay + totalHours + totalMinutes + totalSeconds;
      restingTime = restingTime.trim();
      if (restingTime.equals("")) {
         restingTime = "0 segundos";
      }

      return restingTime;
   }

   public static String formatDifference(long time) {
      long timeLefting = time - System.currentTimeMillis();
      long seconds = timeLefting / 1000L;
      return fromLong(seconds);
   }

   public static long parseDateDiff(String time, boolean future) throws Exception {
      Pattern timePattern = Pattern.compile("(?:(\\d+)\\s*y[a-z]*[,\\s]*)?(?:(\\d+)\\s*mo[a-z]*[,\\s]*)?(?:(\\d+)\\s*w[a-z]*[,\\s]*)?(?:(\\d+)\\s*d[a-z]*[,\\s]*)?(?:(\\d+)\\s*h[a-z]*[,\\s]*)?(?:(\\d+)\\s*m[a-z]*[,\\s]*)?(?:(\\d+)\\s*(?:s[a-z]*)?)?", 2);
      Matcher m = timePattern.matcher(time);
      int years = 0;
      int months = 0;
      int weeks = 0;
      int days = 0;
      int hours = 0;
      int minutes = 0;
      int seconds = 0;
      boolean found = false;

      while(m.find()) {
         if (m.group() != null && !m.group().isEmpty()) {
            for(int i = 0; i < m.groupCount(); ++i) {
               if (m.group(i) != null && !m.group(i).isEmpty()) {
                  found = true;
                  break;
               }
            }

            if (found) {
               if (m.group(1) != null && !m.group(1).isEmpty()) {
                  years = Integer.parseInt(m.group(1));
               }

               if (m.group(2) != null && !m.group(2).isEmpty()) {
                  months = Integer.parseInt(m.group(2));
               }

               if (m.group(3) != null && !m.group(3).isEmpty()) {
                  weeks = Integer.parseInt(m.group(3));
               }

               if (m.group(4) != null && !m.group(4).isEmpty()) {
                  days = Integer.parseInt(m.group(4));
               }

               if (m.group(5) != null && !m.group(5).isEmpty()) {
                  hours = Integer.parseInt(m.group(5));
               }

               if (m.group(6) != null && !m.group(6).isEmpty()) {
                  minutes = Integer.parseInt(m.group(6));
               }

               if (m.group(7) != null && !m.group(7).isEmpty()) {
                  seconds = Integer.parseInt(m.group(7));
               }
               break;
            }
         }
      }

      if (!found) {
         throw new Exception("Illegal Date");
      } else if (years > 20) {
         throw new Exception("Illegal Date");
      } else {
         Calendar c = new GregorianCalendar();
         if (years > 0) {
            c.add(1, years * (future ? 1 : -1));
         }

         if (months > 0) {
            c.add(2, months * (future ? 1 : -1));
         }

         if (weeks > 0) {
            c.add(3, weeks * (future ? 1 : -1));
         }

         if (days > 0) {
            c.add(5, days * (future ? 1 : -1));
         }

         if (hours > 0) {
            c.add(11, hours * (future ? 1 : -1));
         }

         if (minutes > 0) {
            c.add(12, minutes * (future ? 1 : -1));
         }

         if (seconds > 0) {
            c.add(13, seconds * (future ? 1 : -1));
         }

         return c.getTimeInMillis();
      }
   }
}