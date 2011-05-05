package org.jboss.seam.forge.codequality;

import org.jboss.forge.project.Project;
import org.jboss.forge.shell.plugins.*;
import org.jboss.forge.shell.util.BeanManagerUtils;
import org.jboss.seam.forge.codequality.tools.Tool;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@Alias("codequality")
public class CodeQualityPlugin implements Plugin
{

   @Inject
   BeanManager beanManager;
   @Inject private Project project;


   @Command(value = "setup")
   public void setup(@Option(name = "tool", required = true) QualityTool tool, final PipeOut out)
   {
      Tool qualityTool = BeanManagerUtils.getContextualInstance(beanManager, tool.getTool());
      qualityTool.installDependencies();

   }
}
