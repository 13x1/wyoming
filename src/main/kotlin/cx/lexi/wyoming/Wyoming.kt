package cx.lexi.wyoming

import com.wynntils.core.WynntilsMod
import net.fabricmc.api.ModInitializer

class Wyoming : ModInitializer {

    override fun onInitialize() {
        println("Config test: ${config}")
    }

    companion object {
        val config = ConfigCompiled.createAndLoad()!!
    }
}