package chucktcplugin;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ChuckProjectPageExtension extends SimplePageExtension {
    private SBuildServer buildServer;
    private List<String> quotes;
    private PluginDescriptor pluginDescriptor;

    public ChuckProjectPageExtension(PagePlaces pagePlaces, PluginDescriptor pluginDescriptor, SBuildServer buildServer) {
        super(pagePlaces, PlaceId.PROJECT_FRAGMENT, "chucktcpluginProject", pluginDescriptor.getPluginResourcesPath("Chuck.jsp"));
        register();
        this.buildServer = buildServer;
        this.pluginDescriptor = pluginDescriptor;
        try {
            this.quotes = (List<String>) IOUtils.readLines(
                    getClass().getResourceAsStream("/buildServerResources/quotes.txt"), "UTF-8"
            );
        } catch (IOException e) {
            Loggers.SERVER.error("Failed to load quotes", e);
        }
    }

    @Override
    public boolean isAvailable(@NotNull HttpServletRequest request) {
        return true;
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
        Set<String> fails = Sets.newHashSet();
        String projectId = request.getParameter("projectId");
        SProject project = buildServer.getProjectManager().findProjectByExternalId(projectId);
        for (SBuildType buildType : project.getBuildTypes()) {
            List<SFinishedBuild> builds = buildType.getHistory();
            if (!builds.isEmpty()) {
                SFinishedBuild lastBuild = builds.iterator().next();
                if (!lastBuild.getBuildStatus().isSuccessful()) {
                     fails.add(buildType.getName());
                }
            }
        }

        model.put("chuckHappy", fails.isEmpty());
        model.put("sadImage", pluginDescriptor.getPluginResourcesPath("_chuck_sad.jpg"));
        model.put("happyImage", pluginDescriptor.getPluginResourcesPath("_chuck_happy.jpg"));
        String quote = quotes.get((new Random()).nextInt(quotes.size())) ;

        if (fails.isEmpty()) {
            model.put("message", "Chuck Norris approves all your builds for " + project.getName() + " and remember that " + quote);
        } else {
            model.put("message", "Chuck Norris disapproves builds for " + Joiner.on(", ").join(fails) + " and remember that " + quote);
        }
    }
}
