package org.jboss.forge.codequality.plugins.cobertura;

public enum OutputFormat
{
   HTML("html"), XML("xml");

   private String formatNameForPom;

   private OutputFormat(String formatNameForPom)
   {
      this.formatNameForPom = formatNameForPom;
   }

   @Override public String toString()
   {
      return formatNameForPom;
   }
}
