package cx.lexi.wyoming

import cx.lexi.wyoming.feat.dropped_items.CustomItemEntityRenderer
import net.fabricmc.api.ModInitializer

class Wyoming : ModInitializer {

    override fun onInitialize() {
        CustomItemEntityRenderer.register()
        println("Wyoming initialized. Config: $config")
    }

    companion object {
        val config = ConfigCompiled.createAndLoad()!!
    }
}