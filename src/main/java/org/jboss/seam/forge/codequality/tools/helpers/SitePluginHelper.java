package org.jboss.seam.forge.codequality.tools.helpers;

import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.seam.forge.codequality.tools.FindBugs;

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
}