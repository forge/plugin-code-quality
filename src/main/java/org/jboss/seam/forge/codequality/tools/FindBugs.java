package org.jboss.seam.forge.codequality.tools;

import org.jboss.seam.forge.maven.MavenPluginFacet;
import org.jboss.seam.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.shell.ShellPrompt;

import javax.inject.Inject;
import java.util.List;

public class FindBugs implements Tool {
    @Inject
    Project project;

    @Inject
    ShellPrompt prompt;

    @Override
    public void installDependencies() {

        MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
        DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

        DependencyBuilder findbugsDependencyBuilder = DependencyBuilder.create()
                .setGroupId("org.codehaus.mojo")
                .setArtifactId("findbugs-maven-plugin");

        List<Dependency> findBugsVersions = dependencyFacet.resolveAvailableVersions(findbugsDependencyBuilder);
        Dependency findbugsDependency = prompt.promptChoiceTyped("Which version of FindBugs do you want to install?", findBugsVersions, findBugsVersions.get(findBugsVersions.size() - 1));

        MavenPluginBuilder findbugsPlugin = MavenPluginBuilder.create()
                .setDependency(findbugsDependency)
                .createConfiguration()
                .createConfigurationElement("xmlOutput")
                .setText("true").getParentPluginConfig().getOrigin();


        MavenPluginBuilder sitePlugin = null;
        DependencyBuilder sitePluginDependencyBuilder = DependencyBuilder.create("org.apache.maven.plugins:maven-site-plugin");
        if (pluginFacet.hasPlugin(sitePluginDependencyBuilder)) {
            sitePlugin = MavenPluginBuilder.create(pluginFacet.getPlugin(sitePluginDependencyBuilder));
            pluginFacet.removePlugin(sitePluginDependencyBuilder);
        } else {

            List<Dependency> sitePluginVersions = dependencyFacet.resolveAvailableVersions(sitePluginDependencyBuilder);
            Dependency sitePluginDependency = prompt.promptChoiceTyped("Which version of Site Plugin do you want to install?", sitePluginVersions, sitePluginVersions.get(sitePluginVersions.size() - 1));
            sitePlugin = MavenPluginBuilder.create().setDependency(sitePluginDependency);
        }

        sitePlugin.createConfiguration().createConfigurationElement("reportPlugins").addChild(findbugsPlugin).getParentPluginConfig().getOrigin();

        pluginFacet.addPlugin(sitePlugin);
    }


}
