package chucktcplugin;

import jetbrains.buildServer.controllers.BuildDataExtensionUtil;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
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


public class ChuckBuildPageExtension extends SimplePageExtension {

    private SBuildServer buildServer;
    private List<String> quotes;
    private PluginDescriptor pluginDescriptor;

    public ChuckBuildPageExtension(PagePlaces pagePlaces, PluginDescriptor pluginDescriptor, SBuildServer buildServer) {
        super(pagePlaces, PlaceId.BUILD_RESULTS_FRAGMENT, "chucktcpluginBuild", pluginDescriptor.getPluginResourcesPath("Chuck.jsp"));
        register();
        this.buildServer = buildServer;
        this.pluginDescriptor = pluginDescriptor;
        try {
            this.quotes = (List<String>)IOUtils.readLines(
                    getClass().getResourceAsStream("/buildServerResources/quotes.txt"), "UTF-8"
            );
        } catch (IOException e) {
            Loggers.SERVER.error("Failed to load quotes", e);
        }
    }

    @Override
    public boolean isAvailable(@NotNull HttpServletRequest request) {
        SBuild build = getBuild(request);
        return build != null && build.isFinished();
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {

        SBuild build = getBuild(request);
        model.put("buildId", build.getBuildNumber());
        model.put("chuckHappy", build.getBuildStatus().isSuccessful());
        model.put("sadImage", pluginDescriptor.getPluginResourcesPath("_chuck_sad.jpg"));
        model.put("happyImage", pluginDescriptor.getPluginResourcesPath("_chuck_happy.jpg"));
        String quote = quotes.get((new Random()).nextInt(quotes.size()));

        if (build.getBuildStatus().isSuccessful()) {
            model.put("message", "Chuck Norris approves build #" + build.getBuildId() + "and remember that " + quote);
        } else {
            model.put("message", "Chuck Norris disapproves build #" + build.getBuildId() + "and remember that " + quote);
        }
    }

    private SBuild getBuild(HttpServletRequest request) {
        return BuildDataExtensionUtil.retrieveBuild(request, buildServer);
    }
}
