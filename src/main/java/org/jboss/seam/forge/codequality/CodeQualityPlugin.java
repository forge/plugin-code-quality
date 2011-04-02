package org.jboss.seam.forge.codequality;

import org.jboss.seam.forge.codequality.tools.Tool;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.shell.plugins.*;
import org.jboss.seam.forge.shell.util.BeanManagerUtils;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@Alias("codequality")
public class CodeQualityPlugin implements Plugin {

    @Inject BeanManager beanManager;
    @Inject private Project project;


    @Command(value = "setup")
    public void setup(@Option(name = "tool", required = true) QualityTool tool, final PipeOut out) {
        Tool qualityTool = BeanManagerUtils.getContextualInstance(beanManager, tool.getTool());
        qualityTool.installDependencies();

    }
}
