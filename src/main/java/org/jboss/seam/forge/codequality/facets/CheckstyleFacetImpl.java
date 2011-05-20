package org.jboss.seam.forge.codequality.facets;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.*;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.codequality.tools.CheckStyle;

import javax.inject.Inject;
import java.util.List;

@Alias("forge.codequality.checkstyle")
@RequiresFacet({ResourceFacet.class, MavenCoreFacet.class, JavaSourceFacet.class})
public class CheckstyleFacetImpl extends BaseFacet implements CheckstyleFacet
{
   @Inject CheckStyle checkStyle;

   @Override public boolean install()
   {
      if (!isInstalled())
      {
         checkStyle.installDependencies();
         return true;
      }

      return true;
   }

   @Override public boolean isInstalled()
   {
      MavenPluginFacet dependencyFacet = project.getFacet(MavenPluginFacet.class);
      DependencyBuilder dependency = createSitePluginDependency();

      if (dependencyFacet.hasPlugin(dependency))
      {
         MavenPlugin plugin = dependencyFacet.getPlugin(dependency);
         Configuration config = plugin.getConfig();
         if (config.hasConfigurationElement("reportPlugins"))
         {
            return hasCheckstyleElement(config.getConfigurationElement("reportPlugins"));
         }
      }

      return false;
   }

   private DependencyBuilder createSitePluginDependency()
   {
      return DependencyBuilder.create()
              .setGroupId("org.apache.maven.plugins")
              .setArtifactId("maven-site-plugin");
   }

   @Override public void setConfigLocation(String location)
   {
      MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
      DependencyBuilder dependency = createSitePluginDependency();
      MavenPlugin plugin = pluginFacet.getPlugin(dependency);
      ConfigurationElement reportPlugins = plugin.getConfig().getConfigurationElement("reportPlugins");
      ConfigurationElement checkstyleElement = getCheckstyleElement(reportPlugins);
      ConfigurationElementBuilder configurationElement = (ConfigurationElementBuilder) checkstyleElement;
      configurationElement.addChild(
              ConfigurationElementBuilder.create()
                      .setName("configlocation")
                      .setText(location)
      );

      pluginFacet.removePlugin(createSitePluginDependency());
      pluginFacet.addPlugin(plugin);
   }

   private ConfigurationElement getCheckstyleElement(ConfigurationElement parent)
   {
      List<PluginElement> children = parent.getChildren();
      for (PluginElement child : children)
      {
         if (child instanceof ConfigurationElement)
         {
            ConfigurationElement element = (ConfigurationElement) child;
            if (element.hasChilderen())
            {
               return getCheckstyleElement(element);
            } else
            {
               if ("maven-checkstyle-plugin".equals(element.getText()))
               {
                  return parent;
               }
            }
         }
      }

      throw new ConfigurationElementNotFoundException(parent);
   }

   private boolean hasCheckstyleElement(ConfigurationElement configurationElement)
   {
      try
      {
         getCheckstyleElement(configurationElement);
         return true;
      } catch (Exception ex)
      {
         return false;
      }

   }
}
