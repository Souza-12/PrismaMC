package com.br.teagadev.prismamc.commons.common.utility.system;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class Machine {
   private static final String OS = System.getProperty("os.name").toLowerCase();
   private static final String USER_HOME = System.getProperty("user.home");
   private static String machineIP = "NOT LOADED";

   public static void initialize() {
      machineIP = getRealIP();
      CommonsGeneral.console("§a[Machine] -> Address: " + machineIP);
   }

   public static Long getRamUsage() {
      return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 1048576L;
   }

   public static double getCPUUse() {
      try {
         MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
         ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
         AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});
         if (list.isEmpty()) {
            return Double.NaN;
         } else {
            Attribute att = (Attribute)list.get(0);
            Double value = (Double)att.getValue();
            return value == -1.0D ? Double.NaN : (double)((int)(value * 1000.0D)) / 10.0D;
         }
      } catch (Exception var5) {
         return 0.0D;
      }
   }

   public static String getMachineIP() {
      return machineIP;
   }

   public static boolean isWindows() {
      return OS.indexOf("win") >= 0;
   }

   public static boolean isMac() {
      return OS.indexOf("mac") >= 0;
   }

   public static boolean isUnix() {
      return OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0;
   }

   public static boolean isSolaris() {
      return OS.indexOf("sunos") >= 0;
   }

   public static String getOS() {
      if (isWindows()) {
         return "win";
      } else if (isMac()) {
         return "osx";
      } else if (isUnix()) {
         return "uni";
      } else {
         return isSolaris() ? "sol" : "err";
      }
   }

   public static String getDiretorio() {
      return (isUnix() ? "/" + getUserHome() + "/" : "C:\\") + "PrismaConfig";
   }

   public static String getSeparador() {
      return isUnix() ? "/" : "\\";
   }

   private static String getRealIP() {
      try {
         URLConnection connect = (new URL("http://checkip.amazonaws.com/")).openConnection();
         connect.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
         Scanner scan = new Scanner(connect.getInputStream());
         StringBuilder sb = new StringBuilder();

         while(scan.hasNext()) {
            sb.append(scan.next());
         }

         scan.close();
         scan = null;
         connect = null;
         return sb.toString();
      } catch (Exception var3) {
         return "Erro";
      }
   }

   public static String getUserHome() {
      return USER_HOME;
   }
}