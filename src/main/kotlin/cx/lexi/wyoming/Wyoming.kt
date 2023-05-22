package cx.lexi.wyoming

import com.wynntils.core.WynntilsMod
import net.fabricmc.api.ModInitializer

class Wyoming : ModInitializer {

    override fun onInitialize() {
        println("Wynntils test: ${WynntilsMod.MOD_ID}")
    }

    companion object {
        val config = ConfigCompiled.createAndLoad()!!
    }
}