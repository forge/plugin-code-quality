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
      ConfigurationElement reportPlugins = plugin.getConfig().getConfigurationElement("reportPlugins");
      ConfigurationElement coberturaElement = reportPlugins.getChildByContent("cobertura-maven-plugin");
      ConfigurationElementBuilder configurationElement = (ConfigurationElementBuilder) coberturaElement;


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
