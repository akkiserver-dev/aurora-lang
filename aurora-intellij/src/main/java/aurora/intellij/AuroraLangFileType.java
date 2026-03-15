package aurora.intellij;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AuroraLangFileType extends LanguageFileType {
    public static final AuroraLangFileType INSTANCE = new AuroraLangFileType();

    private AuroraLangFileType() {
        super(AuroraLang.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "Aurora Source";
    }

    @Override
    public @NotNull String getDescription() {
        return "Aurora language source file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "ar";
    }

    @Override
    public Icon getIcon() {
        return AuroraIcons.FILE;
    }
}
