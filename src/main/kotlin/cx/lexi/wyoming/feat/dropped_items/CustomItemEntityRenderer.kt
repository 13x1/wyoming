package cx.lexi.wyoming.feat.dropped_items

import cx.lexi.wyoming.Wyoming
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.random.Random

private val c = Wyoming.config

// I could write a mixin for this but copying the class is better
@Environment(value = EnvType.CLIENT)
class CustomItemEntityRenderer(context: EntityRendererFactory.Context) : EntityRenderer<ItemEntity>(context) {
    companion object {
        fun register() = EntityRendererRegistry.register(EntityType.ITEM) {
            CustomItemEntityRenderer(it)
        }
    }

    private val itemRenderer: ItemRenderer
    private val random = Random.create()

    init {
        itemRenderer = context.itemRenderer
        shadowRadius = 0.15f
        shadowOpacity = 0.75f
    }

    private fun getRenderedAmount(stack: ItemStack): Int {
        var ratio = c.visual.stacking.itemRatio()
        var max = c.visual.stacking.maxItems()
        var base = 1

        if (!c.visual.stacking.enabled()) {
            ratio=16; max=5; base=2
        }

        return ((stack.count-1)/ratio+base)
            .coerceAtMost(stack.count)
            .coerceAtMost(max)
    }

    override fun render(
        itemEntity: ItemEntity, f: Float, g: Float,
        matr: MatrixStack, vcp: VertexConsumerProvider, i: Int
    ) {

        var heightAdjustment: Float

        // minecraft code here
        run {
            var t: Float
            var s: Float
            matr.push()
            val itemStack = itemEntity.stack
            val j = if (itemStack.isEmpty) 187 else Item.getRawId(itemStack.item) + itemStack.damage
            random.setSeed(j.toLong())
            val bakedModel = itemRenderer.getModel(itemStack, itemEntity.world, null, itemEntity.id)
            val bl = bakedModel.hasDepth()
            val k = getRenderedAmount(itemStack)
            val l = MathHelper.sin((itemEntity.itemAge.toFloat() + g) / 10.0f + itemEntity.uniqueOffset) * 0.1f + 0.1f
            val m = bakedModel.transformation.getTransformation(ModelTransformationMode.GROUND).scale.y()
            matr.translate(0.0f, l + 0.25f * m, 0.0f)
            heightAdjustment = l
            val n = itemEntity.getRotation(g)
            matr.multiply(RotationAxis.POSITIVE_Y.rotation(n))
            val o = bakedModel.transformation.ground.scale.x()
            val p = bakedModel.transformation.ground.scale.y()
            val q = bakedModel.transformation.ground.scale.z()
            if (!bl) {
                val r = -0.0f * (k - 1).toFloat() * 0.5f * o
                s = -0.0f * (k - 1).toFloat() * 0.5f * p
                t = -0.09375f * (k - 1).toFloat() * 0.5f * q
                matr.translate(r, s, t)
            }
            for (u in 0 until k) {
                matr.push()
                if (u > 0) {
                    if (bl) {
                        s = (random.nextFloat() * 2.0f - 1.0f) * 0.15f
                        t = (random.nextFloat() * 2.0f - 1.0f) * 0.15f
                        val v = (random.nextFloat() * 2.0f - 1.0f) * 0.15f
                        matr.translate(s, t, v)
                    } else {
                        s = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f
                        t = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f
                        matr.translate(s, t, 0.0f)
                    }
                }
                itemRenderer.renderItem(
                    itemStack,
                    ModelTransformationMode.GROUND,
                    false,
                    matr,
                    vcp,
                    i,
                    OverlayTexture.DEFAULT_UV,
                    bakedModel
                )
                matr.pop()
                if (bl) continue
                matr.translate(0.0f * o, 0.0f * p, 0.09375f * q)
            }
            matr.pop()
            super.render(itemEntity, f, g, matr, vcp, i)
        }

        if (c.nametags.enabled() && !hasLabel(itemEntity)) {
            val id = identify(itemEntity)
            if (c.nametags.showCounts() && itemEntity.stack.count > 1 && !id.noPlural) {
                id.text.append(" (${itemEntity.stack.count}x)")
            }
            if (!c.nametags.bouncy()) heightAdjustment = 0.0f
            if (id.display) {
                itemEntity.isSneaking = c.nametags.sneaky()
                matr.push()
                matr.translate(0.0, 0.25 + heightAdjustment, 0.0)
                renderLabelIfPresent(itemEntity, id.text.formatted(id.fmt), matr, vcp, i)
                matr.pop()
                itemEntity.isSneaking = false
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun getTexture(itemEntity: ItemEntity): Identifier {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE
    }
}

