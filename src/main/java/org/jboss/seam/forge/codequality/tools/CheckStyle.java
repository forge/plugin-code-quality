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

public class  CheckStyle implements Tool
{
   @Inject
   Project project;

   @Inject
   ShellPrompt prompt;

   @Inject
   SitePluginHelper sitePluginHelper;

   @Override
   public void installDependencies()
   {
      MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      DependencyBuilder checkstyleDependencyBuilder = DependencyBuilder.create()
              .setGroupId("org.apache.maven.plugins")
              .setArtifactId("maven-checkstyle-plugin");

      List<Dependency> checkstyleVersions = dependencyFacet.resolveAvailableVersions(checkstyleDependencyBuilder);
      Dependency checkstyleDependency = prompt.promptChoiceTyped("Which version of Checkstyle do you want to install?", checkstyleVersions, checkstyleVersions.get(checkstyleVersions.size() - 1));

      MavenPluginBuilder checkstylePlugin = MavenPluginBuilder.create()
              .setDependency(checkstyleDependency);

      MavenPluginBuilder sitePlugin = sitePluginHelper.getOrCreateSitePlugin();

      ConfigurationElementBuilder reportPlugins;
      if (sitePlugin.getConfig().hasConfigurationElement("reportPlugins"))
      {
         reportPlugins = ConfigurationElementBuilder.createFromExisting(sitePlugin.getConfig().getConfigurationElement("reportPlugins"));
         sitePlugin.getConfig().removeConfigurationElement("reportPlugins");
         reportPlugins.addChild(checkstylePlugin);
         sitePlugin.getConfig().addConfigurationElement(reportPlugins);
      } else
      {
         reportPlugins = sitePlugin.createConfiguration().createConfigurationElement("reportPlugins");
         reportPlugins.addChild(checkstylePlugin);
      }

      pluginFacet.addPlugin(sitePlugin);
   }
}
