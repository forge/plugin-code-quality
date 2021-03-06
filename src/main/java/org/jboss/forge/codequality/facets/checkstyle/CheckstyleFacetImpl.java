package org.jboss.forge.codequality.facets.checkstyle;

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
import org.jboss.forge.codequality.facets.helpers.SitePluginHelper;

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
            return config.getConfigurationElement("reportPlugins").hasChildByContent("maven-checkstyle-plugin");
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
      ConfigurationElement checkstyleElement = reportPlugins.getChildByContent("maven-checkstyle-plugin");
      ConfigurationElementBuilder configurationElement = (ConfigurationElementBuilder) checkstyleElement;

      ConfigurationElementBuilder configuration;
      if(configurationElement.hasChildByName("configuration")) {
         configuration = (ConfigurationElementBuilder) configurationElement.getChildByName("configuration");
      } else {
         configuration = ConfigurationElementBuilder.create().setName("configuration");
         configurationElement.addChild(configuration);
      }

      configuration.addChild(
              ConfigurationElementBuilder.create()
                      .setName("configlocation")
                      .setText(location)
      );


      pluginFacet.removePlugin(sitePluginHelper.createSitePluginDependency());
      pluginFacet.addPlugin(plugin);
   }

   private void installDependencies()
   {
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      DependencyBuilder checkstyleDependencyBuilder = DependencyBuilder.create()
              .setGroupId("org.apache.maven.plugins")
              .setArtifactId("maven-checkstyle-plugin");

      List<Dependency> checkstyleVersions = dependencyFacet.resolveAvailableVersions(checkstyleDependencyBuilder);
      Dependency checkstyleDependency = prompt.promptChoiceTyped("Which version of Checkstyle do you want to install?", checkstyleVersions, checkstyleVersions.get(checkstyleVersions.size() - 1));

      MavenPluginBuilder checkstylePlugin = MavenPluginBuilder.create()
              .setDependency(checkstyleDependency);
      sitePluginHelper.updateSitePlugin(checkstylePlugin);
   }

}
