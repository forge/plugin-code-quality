package org.jboss.seam.forge.codequality.facets.helpers;

import com.sun.org.apache.xpath.internal.operations.Neg;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.maven.plugins.PluginElement;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.seam.forge.codequality.facets.ConfigurationElementNotFoundException;

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

   public ConfigurationElement getConfigElementRecursive(ConfigurationElement parent, String elementToGet)
   {
      List<PluginElement> children = parent.getChildren();
      for (PluginElement child : children)
      {
         if (child instanceof ConfigurationElement)
         {
            ConfigurationElement element = (ConfigurationElement) child;
            if (element.hasChilderen())
            {
               try {
                  return getConfigElementRecursive(element, elementToGet);
               } catch(ConfigurationElementNotFoundException ex) {

               }
            } else
            {
               if (elementToGet.equals(element.getText()))
               {
                  return parent;
               }
            }
         }
      }

      throw new ConfigurationElementNotFoundException(parent);
   }

   public boolean hasConfigElementRecursive(ConfigurationElement configurationElement, String elementToGet)
   {
      try
      {
         getConfigElementRecursive(configurationElement, elementToGet);
         return true;
      } catch (Exception ex)
      {
         return false;
      }

   }

   public DependencyBuilder createSitePluginDependency()
   {
      return DependencyBuilder.create()
              .setGroupId("org.apache.maven.plugins")
              .setArtifactId("maven-site-plugin");
   }
}