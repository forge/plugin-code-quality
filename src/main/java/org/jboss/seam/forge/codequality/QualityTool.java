package org.jboss.seam.forge.codequality;

import org.jboss.seam.forge.codequality.tools.FindBugs;
import org.jboss.seam.forge.codequality.tools.Tool;

public enum QualityTool {
    FINDBUGS(FindBugs.class);

    private Class<? extends Tool> tool;

    public Class<? extends Tool> getTool() {
        return tool;
    }

    private QualityTool(Class<? extends Tool> tool) {
        this.tool = tool;
    }
}
