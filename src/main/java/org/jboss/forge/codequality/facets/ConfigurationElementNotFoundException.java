package org.jboss.forge.codequality.facets;

import org.jboss.forge.maven.plugins.ConfigurationElement;

public class ConfigurationElementNotFoundException extends RuntimeException
{
   public ConfigurationElementNotFoundException(ConfigurationElement element)
   {
      super("Configuration element " + element.getName() + " not found");
   }
}
