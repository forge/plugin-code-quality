package org.jboss.forge.codequality;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.plugins.*;

import javax.enterprise.event.Event;
import javax.inject.Inject;

@Alias("codequality")
@RequiresFacet(MavenCoreFacet.class)
public class CodeQualityPlugin implements Plugin
{
   @Inject Project project;
   @Inject private Event<InstallFacets> installFacets;

   @Command(value = "setup")
   public void setup(@Option(name = "tool", required = true) QualityTool tool, final PipeOut out)
   {
      installFacets.fire(new InstallFacets(tool.getFacet()));
   }
}
