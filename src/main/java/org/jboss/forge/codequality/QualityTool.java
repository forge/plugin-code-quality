package org.jboss.forge.codequality;

import org.jboss.forge.project.Facet;
import org.jboss.forge.codequality.facets.checkstyle.CheckstyleFacet;
import org.jboss.forge.codequality.facets.findbugs.FindBugsFacet;

public enum QualityTool
{

   CHECKSTYLE(CheckstyleFacet.class),
   FINDBUGS(FindBugsFacet.class);

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
