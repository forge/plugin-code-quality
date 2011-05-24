package org.jboss.forge.codequality.facets.helpers;

import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.maven.plugins.PluginElement;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.codequality.facets.ConfigurationElementNotFoundException;

import javax.inject.Inject;
import java.util.List;

public class SitePluginHelper
{
   @Inject
   Project project;

   @Inject
   ShellPrompt prompt;

   public MavenPluginBuilder getOrCreateSitePlugin()
   {
      MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      MavenPluginBuilder sitePlugin = null;
      DependencyBuilder sitePluginDependencyBuilder = DependencyBuilder.create("org.apache.maven.plugins:maven-site-plugin");
      if (pluginFacet.hasPlugin(sitePluginDependencyBuilder))
      {
         sitePlugin = MavenPluginBuilder.create(pluginFacet.getPlugin(sitePluginDependencyBuilder));
         pluginFacet.removePlugin(sitePluginDependencyBuilder);
      } else
      {

         List<Dependency> sitePluginVersions = dependencyFacet.resolveAvailableVersions(sitePluginDependencyBuilder);
         Dependency sitePluginDependency = prompt.promptChoiceTyped("Which version of Site Plugin do you want to install?", sitePluginVersions, sitePluginVersions.get(sitePluginVersions.size() - 1));
         sitePlugin = MavenPluginBuilder.create().setDependency(sitePluginDependency);
         sitePlugin.createConfiguration();
      }
      return sitePlugin;
   }



   public DependencyBuilder createSitePluginDependency()
   {
      return DependencyBuilder.create()
              .setGroupId("org.apache.maven.plugins")
              .setArtifactId("maven-site-plugin");
   }

   public void updateSitePlugin(MavenPluginBuilder findbugsPlugin)
   {
      MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
      MavenPluginBuilder sitePlugin = getOrCreateSitePlugin();
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