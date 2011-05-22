package org.jboss.seam.forge.codequality.facets.checkstyle;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.*;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.codequality.facets.helpers.SitePluginHelper;

import javax.inject.Inject;
import java.util.List;

@Alias("forge.codequality.checkstyle")
@RequiresFacet({ResourceFacet.class, MavenCoreFacet.class, JavaSourceFacet.class})
public class CheckstyleFacetImpl extends BaseFacet implements CheckstyleFacet
{
   @Inject
   ShellPrompt prompt;

   @Inject
   SitePluginHelper sitePluginHelper;

   @Override public boolean install()
   {
      if (!isInstalled())
      {
         installDependencies();
         return true;
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
            return sitePluginHelper.hasConfigElementRecursive(config.getConfigurationElement("reportPlugins"), "maven-checkstyle-plugin");
         }
      }

      return false;
   }

   @Override public void setConfigLocation(String location)
   {
      MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
      DependencyBuilder dependency = sitePluginHelper.createSitePluginDependency();
      MavenPlugin plugin = pluginFacet.getPlugin(dependency);
      ConfigurationElement reportPlugins = plugin.getConfig().getConfigurationElement("reportPlugins");
      ConfigurationElement checkstyleElement = sitePluginHelper.getConfigElementRecursive(reportPlugins, "maven-checkstyle-plugin");
      ConfigurationElementBuilder configurationElement = (ConfigurationElementBuilder) checkstyleElement;
      configurationElement.addChild(
              ConfigurationElementBuilder.create()
                      .setName("configlocation")
                      .setText(location)
      );

      pluginFacet.removePlugin(sitePluginHelper.createSitePluginDependency());
      pluginFacet.addPlugin(plugin);
   }

   private void installDependencies()
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
