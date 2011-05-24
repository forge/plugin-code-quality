package org.jboss.forge.codequality.facets.cobertura;

import org.jboss.forge.codequality.plugins.cobertura.OutputFormat;
import org.jboss.forge.project.Facet;

public interface CoberturaFacet extends Facet
{
   void addOutputFormat(OutputFormat outputFormat);
   void setMaxMemoryConsumption(int maxMemoryConsumption);
}
