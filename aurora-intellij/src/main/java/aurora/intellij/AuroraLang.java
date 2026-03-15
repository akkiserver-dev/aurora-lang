package aurora.intellij;

import com.intellij.lang.Language;

public class AuroraLang extends Language {
    public static final AuroraLang INSTANCE = new AuroraLang();

    private AuroraLang() {
        super("aurora");
    }
}
