package aurora.intellij;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider;

import java.nio.file.Path;
import java.util.List;

/**
 * Launches the Aurora Language Server (aurora-lsp.jar) as a child process
 * communicating over stdin/stdout, and connects it to lsp4ij.
 */
public class AuroraLspServerConnectionProvider extends ProcessStreamConnectionProvider {
    public AuroraLspServerConnectionProvider() {
        var plugin = PluginManagerCore.getPlugin(PluginId.getId("aurora.intellij"));
        Path pluginPath = plugin.getPluginPath();
        Path jar = pluginPath.resolve("lib/aurora-lsp.jar");

        setCommands(List.of("java", "-jar", jar.toString(), "lsp"));
    }
}