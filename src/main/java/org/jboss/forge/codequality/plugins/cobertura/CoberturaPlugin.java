package org.jboss.forge.codequality.plugins.cobertura;

import org.jboss.forge.codequality.facets.checkstyle.CheckstyleFacet;
import org.jboss.forge.codequality.facets.cobertura.CoberturaFacet;
import org.jboss.forge.codequality.plugins.checkstyle.ConfigFileOptions;
import org.jboss.forge.project.Project;
import org.jboss.forge.shell.plugins.*;

import javax.inject.Inject;

@Alias("cobertura")
@RequiresFacet(CoberturaFacet.class)
public class CoberturaPlugin implements Plugin
{
   @Inject Project project;

   @Command(value = "addFormat")
   public void setup(@Option(name = "format", required = true) OutputFormat outputFormat, PipeOut out)
   {
      CoberturaFacet coberturaFacet = project.getFacet(CoberturaFacet.class);

      coberturaFacet.addOutputFormat(outputFormat);

   }

   @Command(value = "setMaxMemoryConsumption")
   public void setMaxMemoryConsumption(@Option(name = "maxMemoryInMB") int maxMemoryConsumption) {
      CoberturaFacet coberturaFacet = project.getFacet(CoberturaFacet.class);
      coberturaFacet.setMaxMemoryConsumption(maxMemoryConsumption);
   }

}
