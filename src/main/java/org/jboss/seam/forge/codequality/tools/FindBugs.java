package org.jboss.seam.forge.codequality.tools;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;
import org.jboss.seam.forge.project.facets.builtin.MavenDependencyFacet;
import org.jboss.seam.forge.shell.Shell;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FindBugs implements Tool {
    @Inject Project project;
    @Inject Shell shell;

    @Override public void installDependencies() {
        MavenDependencyFacet dependencyFacet = project.getFacet(MavenDependencyFacet.class);
        MavenCoreFacet facet = project.getFacet(MavenCoreFacet.class);
        Model pom = facet.getPOM();

        Plugin sitePlugin = getSitePlugin(pom, dependencyFacet);

        Xpp3Dom existingConfiguration = (Xpp3Dom) sitePlugin.getConfiguration();
        Xpp3Dom reportPlugins = null;
        if(existingConfiguration != null) {
           reportPlugins = existingConfiguration.getChild("reportPlugins");
        }


        Plugin plugin = createFindbugsPlugin(dependencyFacet);

        try {
            if (reportPlugins != null) {
                addPluginToExistingReportingConfig(plugin, reportPlugins);
            } else {
                createNewReportingConfiguration(sitePlugin, plugin);
            }


        } catch (Exception e) {
            throw new RuntimeException("Error while generating configuration element of site plugin", e);
        }


        pom.getBuild().addPlugin(sitePlugin);
        facet.setPOM(pom);


    }

    private Plugin createFindbugsPlugin(DependencyFacet dependencyFacet) {
        List<Dependency> dependencies = dependencyFacet.resolveAvailableVersions(
                DependencyBuilder.create()
                        .setGroupId("org.codehaus.mojo")
                        .setArtifactId("findbugs-maven-plugin"));
        int choice = shell.promptChoice("Which version of FindBugs do you want to install?", dependencies);
        Dependency dependency = dependencies.get(choice);


        Plugin plugin = new Plugin();
        plugin.setGroupId(dependency.getGroupId());
        plugin.setArtifactId(dependency.getArtifactId());
        plugin.setVersion(dependency.getVersion());

        return plugin;
    }

    private void createNewReportingConfiguration(Plugin sitePlugin, Plugin plugin) throws XmlPullParserException, IOException {
        Xpp3Dom dom;
        dom = Xpp3DomBuilder.build(
                new ByteArrayInputStream(
                        ("<configuration>" +
                                "   <reportPlugins>" +
                                "       <plugin>" +
                                "           <groupId>" + plugin.getGroupId() + "</groupId>" +
                                "           <artifactId>" + plugin.getArtifactId() + "</artifactId>" +
                                "           <version>" + plugin.getVersion() + "</version>" +
                                "       </plugin>" +
                                "   </reportPlugins>" +
                                "</configuration>").getBytes()),
                "UTF-8");

        sitePlugin.setConfiguration(dom);
    }

    private void addPluginToExistingReportingConfig(Plugin plugin, Xpp3Dom reportPlugins) throws XmlPullParserException, IOException {
        reportPlugins.addChild(Xpp3DomBuilder.build(
                new ByteArrayInputStream(("<plugin>" +
                        "           <groupId>" + plugin.getGroupId() + "</groupId>" +
                        "           <artifactId>" + plugin.getArtifactId() + "</artifactId>" +
                        "           <version>" + plugin.getVersion() + "</version>" +
                        "       </plugin>").getBytes()),
                "UTF-8"));
    }

    private Plugin getSitePlugin(Model pom, DependencyFacet dependencyFacet) {
        Plugin plugin = null;

        List<Plugin> plugins = pom.getBuild().getPlugins();
        for (Plugin pluginInPom : plugins) {
            if (pluginInPom.getArtifactId().equals("maven-site-plugin")) {
                plugin = pluginInPom;

                pom.getBuild().removePlugin(pluginInPom);
                break;
            }
        }

        if (plugin == null) {
            plugin = createSitePlugin(dependencyFacet);
        }

        return plugin;

    }

    private Plugin createSitePlugin(DependencyFacet dependencyFacet) {
        List<Dependency> dependencies = dependencyFacet.resolveAvailableVersions(
                DependencyBuilder.create()
                        .setGroupId("org.apache.maven.plugins")
                        .setArtifactId("maven-site-plugin"));
        int choice = shell.promptChoice("Which version of the site plugin do you want to install?", dependencies);
        Dependency dependency = dependencies.get(choice);

        Plugin plugin = new Plugin();
        plugin.setGroupId(dependency.getGroupId());
        plugin.setArtifactId(dependency.getArtifactId());
        plugin.setVersion(dependency.getVersion());
        return plugin;
    }


}
