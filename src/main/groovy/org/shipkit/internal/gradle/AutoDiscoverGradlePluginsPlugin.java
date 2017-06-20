package org.shipkit.internal.gradle;

import com.gradle.publish.PluginBundleExtension;
import com.gradle.publish.PluginConfig;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.util.PatternSet;

import java.io.File;
import java.util.Set;

/**
 * This plugin discovers gradle plugins and adds them to the {@link PluginBundleExtension}.
 */
public class AutoDiscoverGradlePluginsPlugin implements Plugin<Project> {

    private static final String DOT_PROPERTIES = ".properties";

    @Override
    public void apply(final Project project) {
        project.getPlugins().withId("com.gradle.plugin-publish", new Action<Plugin>() {
            @Override
            public void execute(Plugin plugin) {
                PluginBundleExtension pluginBundleExtension = project.getExtensions().findByType(PluginBundleExtension.class);

                Set<File> pluginPropertyFiles = discoverGradlePluginPropertyFiles(project);
                for (File pluginPropertyFile : pluginPropertyFiles) {
                    PluginConfig config = new PluginConfig(generatePluginName(pluginPropertyFile.getName()));
                    config.setId(pluginPropertyFile.getName().substring(0, pluginPropertyFile.getName().indexOf(DOT_PROPERTIES)));
                    project.getLogger().lifecycle("Adding autodiscovered plugin " + config);
                    pluginBundleExtension.getPlugins().add(config);
                }
            }
        });
    }

    private Set<File> discoverGradlePluginPropertyFiles(Project project) {
        final JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
        FileTree tree = java.getSourceSets().getByName("main").getResources().getAsFileTree();
        tree.matching(new PatternSet().include("META-INF/gradle-plugins/*.properties"));
        return tree.getFiles();
    }

    private String generatePluginName(String fileName) {
        String pluginName = fileName.substring(0, fileName.indexOf(DOT_PROPERTIES));
        pluginName = pluginName.substring(pluginName.lastIndexOf(".") + 1);
        String[] split = pluginName.split("-");
        StringBuilder sb = new StringBuilder();
        for (String string : split) {
            if(sb.length() == 0) {
                sb.append(string.substring(0, 1).toLowerCase()).append(string.substring(1));
            } else {
                sb.append(string.substring(0, 1).toUpperCase()).append(string.substring(1));
            }
        }
        return sb.toString();
    }
}
