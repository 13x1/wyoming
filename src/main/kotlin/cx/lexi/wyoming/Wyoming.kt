package cx.lexi.wyoming

import net.fabricmc.api.ModInitializer

class Wyoming : ModInitializer {

    override fun onInitialize() {
        println("Config test: ${config.anIntOption()}")
    }

    companion object {
        val config = ConfigCompiled.createAndLoad()!!
    }
}