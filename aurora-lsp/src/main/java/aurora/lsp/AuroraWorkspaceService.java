package aurora.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the {@link WorkspaceService} for Aurora.
 * Handles workspace-wide changes, such as configuration updates and file watchers.
 */
public class AuroraWorkspaceService implements WorkspaceService {
    /** The parent language server instance. */
    private final AuroraLanguageServer server;

    /**
     * Constructs a new AuroraWorkspaceService.
     *
     * @param server The language server instance this service belongs to.
     */
    public AuroraWorkspaceService(AuroraLanguageServer server) {
        this.server = server;
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
    }
}
