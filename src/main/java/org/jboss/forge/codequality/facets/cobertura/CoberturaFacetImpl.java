package org.jboss.forge.codequality.facets.cobertura;

import org.jboss.forge.codequality.facets.helpers.SitePluginHelper;
import org.jboss.forge.codequality.plugins.cobertura.OutputFormat;
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
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;

import javax.inject.Inject;
import java.util.List;

@Alias("forge.codequality.cobertura")
@RequiresFacet({ResourceFacet.class, MavenCoreFacet.class, JavaSourceFacet.class})
public class CoberturaFacetImpl extends BaseFacet implements CoberturaFacet
{
   @Inject SitePluginHelper sitePluginHelper;
   @Inject
   ShellPrompt prompt;

   @Override public boolean install()
   {
      if (!isInstalled())
      {
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
            return config.getConfigurationElement("reportPlugins").hasChildByContent("cobertura-maven-plugin");
         }
      }

      return false;
   }

   @Override public void addOutputFormat(OutputFormat outputFormat)
   {
      MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
      DependencyBuilder dependency = sitePluginHelper.createSitePluginDependency();
      MavenPlugin plugin = pluginFacet.getPlugin(dependency);

      ConfigurationElementBuilder configuration = getConfiguration(plugin);
      ConfigurationElementBuilder formats = getFormats(configuration);

      formats.addChild(
              ConfigurationElementBuilder.create()
                      .setName("format")
                      .setText(outputFormat.toString())
      );

      updateSitePlugin(pluginFacet, plugin);
   }


   private ConfigurationElementBuilder getFormats(ConfigurationElementBuilder configuration)
   {
      ConfigurationElementBuilder formats;
      if (configuration.hasChildByName("formats"))
      {
         formats = (ConfigurationElementBuilder) configuration.getChildByName("formats");
      } else
      {
         formats = configuration.addChild("formats");
      }
      return formats;
   }


   private ConfigurationElementBuilder getConfiguration(MavenPlugin plugin)
   {
      ConfigurationElement reportPlugins = plugin.getConfig().getConfigurationElement("reportPlugins");
      ConfigurationElementBuilder coberturaElement = (ConfigurationElementBuilder) reportPlugins.getChildByContent("cobertura-maven-plugin");

      ConfigurationElementBuilder configuration;
      if (coberturaElement.hasChildByName("configuration"))
      {
         configuration = (ConfigurationElementBuilder) coberturaElement.getChildByName("configuration");
      } else
      {
         configuration = ConfigurationElementBuilder.create().setName("configuration");
         coberturaElement.addChild(configuration);
      }
      return configuration;
   }

   private void updateSitePlugin(MavenPluginFacet pluginFacet, MavenPlugin plugin)
   {
      pluginFacet.removePlugin(sitePluginHelper.createSitePluginDependency());
      pluginFacet.addPlugin(plugin);
   }

   private void installDependencies()
   {
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      DependencyBuilder coberturaDependencyBuilder = DependencyBuilder.create()
              .setGroupId("org.codehaus.mojo")
              .setArtifactId("cobertura-maven-plugin");

      List<Dependency> coberturaVersions = dependencyFacet.resolveAvailableVersions(coberturaDependencyBuilder);
      Dependency coberturaDependency = prompt.promptChoiceTyped("Which version of Cobertura do you want to install?", coberturaVersions, coberturaVersions.get(coberturaVersions.size() - 1));

      MavenPluginBuilder coberturaPlugin = MavenPluginBuilder.create()
              .setDependency(coberturaDependency);
      sitePluginHelper.updateSitePlugin(coberturaPlugin);
   }
}
