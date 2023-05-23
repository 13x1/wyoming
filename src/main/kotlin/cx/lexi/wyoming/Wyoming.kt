package cx.lexi.wyoming

import cx.lexi.wyoming.feat.dropped_items.CustomItemEntityRenderer
import net.fabricmc.api.ModInitializer

class Wyoming : ModInitializer {

    override fun onInitialize() {
        CustomItemEntityRenderer.register()
        println("Config test: ${config}")
    }

    companion object {
        val config = ConfigCompiled.createAndLoad()!!
    }
}