package cx.lexi.wyoming;

import io.wispforest.owo.config.annotation.*;

@SuppressWarnings("ALL")
@Modmenu(modId = "wyoming")
@Config(name = "wyoming", wrapperName = "ConfigCompiled")
public class ConfigModel {
    @SectionHeader("DroppedItems")

    @Expanded @Nest
    public Nametags nametags = new Nametags();
    public static class Nametags {
        public boolean enabled = true;
        public boolean bouncy = true;
        public boolean sneaky = false;
        public boolean showCounts = true;
        public UnIDMode unIdMode = UnIDMode.TYPE;
        public static enum UnIDMode {
            NAME, TYPE, NAME_AND_TYPE
        }
        public boolean showGearLevel = true;
        public boolean showGearRarity = true;
    }

    @Expanded @Nest
    public Visual visual = new Visual();
    public static class Visual {
        public boolean disableBuoyancy = true;
        @Expanded @Nest
        public Stacking stacking = new Stacking();
        public static class Stacking {
            public boolean enabled = true;
            @RangeConstraint(min = 1, max = 64)
            public int itemRatio = 1;
            @RangeConstraint(min = 1, max = 64)
            public int maxItems = 8;
        }
    }
}
