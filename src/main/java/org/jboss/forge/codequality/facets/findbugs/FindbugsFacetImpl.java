package org.jboss.forge.codequality.facets.findbugs;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.codequality.facets.helpers.SitePluginHelper;

import javax.inject.Inject;
import java.util.List;

@Alias("forge.codequality.findbugs")
@RequiresFacet({ResourceFacet.class, MavenCoreFacet.class, JavaSourceFacet.class})
public class FindbugsFacetImpl extends BaseFacet implements FindBugsFacet
{

   @Inject
   ShellPrompt prompt;

   @Inject ShellPrintWriter out;

   @Inject
   private SitePluginHelper sitePluginHelper;

   @Override public boolean install()
   {
      if(!isInstalled()) {
         installDependencies();
      }

      return true;
   }

   @Override public boolean isInstalled()
   {
      MavenPluginFacet dependencyFacet = project.getFacet(MavenPluginFacet.class);
      DependencyBuilder dependency = sitePluginHelper.createSitePluginDependency();

      if (dependencyFacet.hasPlugin(dependency))
      {
         MavenPlugin plugin = dependencyFacet.getPlugin(dependency);
         Configuration config = plugin.getConfig();
         if (config.hasConfigurationElement("reportPlugins"))
         {
            return config.getConfigurationElement("reportPlugins").hasChildByContent("findbugs-maven-plugin");
         }
      }

      return false;
   }

   private void installDependencies()
   {
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

      DependencyBuilder findbugsDependencyBuilder = DependencyBuilder.create()
              .setGroupId("org.codehaus.mojo")
              .setArtifactId("findbugs-maven-plugin");

      List<Dependency> findBugsVersions = dependencyFacet.resolveAvailableVersions(findbugsDependencyBuilder);
      Dependency findbugsDependency = prompt.promptChoiceTyped("Which version of FindBugs do you want to install?", findBugsVersions, findBugsVersions.get(findBugsVersions.size() - 1));

      MavenPluginBuilder findbugsPlugin = MavenPluginBuilder.create()
              .setDependency(findbugsDependency)
              .createConfiguration()
              .createConfigurationElement("xmlOutput")
              .setText("true").getParentPluginConfig().getOrigin();


      sitePluginHelper.updateSitePlugin(findbugsPlugin);
   }

}
