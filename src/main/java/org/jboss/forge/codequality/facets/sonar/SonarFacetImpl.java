package org.jboss.forge.codequality.facets.sonar;

import org.apache.maven.model.Model;
import org.apache.maven.settings.Profile;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.profiles.ProfileBuilder;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

import javax.inject.Inject;
import java.util.List;

@Alias("forge.codequality.sonar")
@RequiresFacet({DependencyFacet.class, MavenCoreFacet.class})
public class SonarFacetImpl extends BaseFacet implements SonarFacet
{
   @Inject ShellPrompt prompt;

   @Override public boolean install()
   {
      if(!isInstalled()) {
         installProfile();
      }

      return true;
   }

   private void installProfile()
   {

      MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
      ProfileBuilder profileBuilder =
              ProfileBuilder.create()
                      .setId("sonar")
                      .setActiveByDefault(false);

      boolean runRemote = prompt.promptBoolean("Do you run Sonar on a remote server?", true);
      if (runRemote)
      {
         String server = prompt.prompt("What is the Sonar server address?", String.class);
         profileBuilder.addProperty("sonar.host.url", server);
      } else
      {
         String jdbcurl = prompt.prompt("sonar.jdbc.url", String.class, " jdbc:mysql://localhost:3306/sonar?useUnicode=true&amp;characterEncoding=utf8");
         String driverClass = prompt.prompt("sonar.jdbc.driverClassName", String.class, "com.mysql.jdbc.Driver");
         String username = prompt.prompt("sonar.jdbc.username", String.class, "sonar");
         String password = prompt.prompt("sonar.jdbc.password", String.class, "sonar");
         profileBuilder.addProperty("sonar.jdbc.url", jdbcurl);
         profileBuilder.addProperty("sonar.jdbc.driverClassName", driverClass);
         profileBuilder.addProperty("sonar.jdbc.username", username);
         profileBuilder.addProperty("sonar.jdbc.password", password);
      }

      Model pom = mavenCoreFacet.getPOM();
      pom.addProfile(profileBuilder.getAsMavenProfile());
      mavenCoreFacet.setPOM(pom);
   }

   @Override public boolean isInstalled()
   {
      MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
      List<org.apache.maven.model.Profile> profiles = mavenCoreFacet.getPOM().getProfiles();
      for (org.apache.maven.model.Profile profile : profiles)
      {
         if(profile.getId().equals("sonar")) {
            return true;
         }
      }
      return false;
   }
}
