package org.jboss.seam.forge.codequality.tools;


import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.seam.forge.codequality.tools.helpers.SitePluginHelper;

import javax.inject.Inject;
import java.util.List;

public class FindBugs implements Tool
{
   @Inject
   Project project;

   @Inject
   ShellPrompt prompt;

   @Inject
   private SitePluginHelper sitePluginHelper;

   @Override
   public void installDependencies()
   {

      MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
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


      MavenPluginBuilder sitePlugin = sitePluginHelper.getOrCreateSitePlugin();
      ConfigurationElementBuilder reportPlugins;

      if (sitePlugin.getConfig().hasConfigurationElement("reportPlugins"))
      {
         reportPlugins = ConfigurationElementBuilder.createFromExisting(sitePlugin.getConfig().getConfigurationElement("reportPlugins"));
         sitePlugin.getConfig().removeConfigurationElement("reportPlugins");
         reportPlugins.addChild(findbugsPlugin);
         sitePlugin.getConfig().addConfigurationElement(reportPlugins);
      } else
      {
         reportPlugins = sitePlugin.createConfiguration().createConfigurationElement("reportPlugins");
         reportPlugins.addChild(findbugsPlugin);
      }

      pluginFacet.addPlugin(sitePlugin);
   }

}
