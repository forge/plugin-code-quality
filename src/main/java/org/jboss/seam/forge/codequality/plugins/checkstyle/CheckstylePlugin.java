package org.jboss.seam.forge.codequality.plugins.checkstyle;

import org.jboss.forge.project.Project;
import org.jboss.forge.shell.plugins.*;
import org.jboss.seam.forge.codequality.facets.checkstyle.CheckstyleFacet;

import javax.inject.Inject;

@Alias("checkstyle")
@RequiresFacet(CheckstyleFacet.class)
public class CheckstylePlugin implements Plugin
{
   @Inject Project project;

   @Command(value = "configure")
   public void setup(@Option(name = "ruleset", required = true) ConfigFileOptions configFileOptions, PipeOut out)
   {
      CheckstyleFacet checkstyleFacet = project.getFacet(CheckstyleFacet.class);

      if(configFileOptions != ConfigFileOptions.CUSTOM) {
         out.println(configFileOptions.getFilename());
         checkstyleFacet.setConfigLocation(configFileOptions.getFilename());
      } else {
         out.println("Custom config");
      }

   }
}
