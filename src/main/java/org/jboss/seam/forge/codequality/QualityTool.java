package org.jboss.seam.forge.codequality;

import org.jboss.forge.project.Facet;
import org.jboss.seam.forge.codequality.facets.CheckstyleFacet;
import org.jboss.seam.forge.codequality.tools.CheckStyle;
import org.jboss.seam.forge.codequality.tools.FindBugs;
import org.jboss.seam.forge.codequality.tools.Tool;

public enum QualityTool
{

   CHECKSTYLE(CheckstyleFacet.class);

   private Class<? extends Facet> facet;

   public Class<? extends Facet> getFacet()
   {
      return facet;
   }

   private QualityTool(Class<? extends Facet> facet)
   {
      this.facet = facet;
   }
}
