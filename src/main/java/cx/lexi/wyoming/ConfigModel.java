package cx.lexi.wyoming;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;

@SuppressWarnings("ALL")
@Modmenu(modId = "wyoming")
@Config(name = "wyoming", wrapperName = "ConfigCompiled")
public class ConfigModel {
    public int anIntOption = 42;
    public boolean aBooleanToggle = false;

    public Choices anEnumOption = Choices.ANOTHER_CHOICE;

    public enum Choices {
        A_CHOICE, ANOTHER_CHOICE;
    }
    @Nest
    public ThisIsNested nestedObject = new ThisIsNested();

    public static class ThisIsNested {
        public boolean aNestedValue = false;
        public int anotherNestedValue = 42;
    }
}
