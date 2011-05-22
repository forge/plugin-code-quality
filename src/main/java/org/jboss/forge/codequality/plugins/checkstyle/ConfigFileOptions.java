package org.jboss.forge.codequality.plugins.checkstyle;

public enum ConfigFileOptions
{
   SUN_CHECKS("docs/sun_checks.xml"),
   CUSTOM("checkstyle.xml");



   private final String filename;

   private ConfigFileOptions(String filename)
   {
      this.filename = filename;
   }

   public String getFilename()
   {
      return filename;
   }
}
