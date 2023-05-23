package cx.lexi.wyoming.feat.dropped_items

import com.wynntils.models.gear.GearInfoRegistry
import com.wynntils.models.ingredients.IngredientInfoRegistry
import com.wynntils.utils.StringUtils
import cx.lexi.wyoming.ConfigModel
import cx.lexi.wyoming.Wyoming
import net.minecraft.entity.ItemEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting as Fmt
import java.util.*

data class ItemIdentity(
    val text: MutableText,
    val fmt: Fmt = Fmt.GRAY,
    val display: Boolean = true,
    val noPlural: Boolean = true
)

fun l(str: String) = Text.literal(str)!!
fun t(str: String) = Text.translatable(str)!!
fun cap(str: String) = StringUtils.capitalizeFirst(str.lowercase(Locale.ROOT))!!
fun capL(str: String) = l(cap(str))

fun normalizeFormatting(str: String) = str.replace(Regex("§."), "")

private object Constants {
    val materialRegex = Regex("^(Unprocessed|Refined) (.+) \\d+$")
    fun ingredientTierColor(tier: Int) = when (tier) {
        0 -> Fmt.GRAY
        1 -> Fmt.WHITE
        2 -> Fmt.YELLOW
        else -> Fmt.GOLD
    }
    val powderRegex = Regex("^(AIR|EARTH|FIRE|WATER|THUNDER) (\\d)$")
    fun powderColor(el: String) = when (el) {
        "AIR" -> Fmt.WHITE
        "EARTH" -> Fmt.GREEN
        "FIRE" -> Fmt.RED
        "WATER" -> Fmt.AQUA
        else -> Fmt.YELLOW
    }
}

private object Registries {
    val gear = GearInfoRegistry()
    val ingredient = IngredientInfoRegistry()
}

private val c = Wyoming.config

fun identify(itemEntity: ItemEntity): ItemIdentity {
    val itemName = normalizeFormatting(itemEntity.stack.name.asTruncatedString(9999))

    // Materials still use the legacy processing system. The dropped items always have this format:
    // (Unprocessed|Refined) [Type + Material] [Index]
    // Items are unprocessed while mining, and immediately get processed when they are dropped.
    // The index is 1 or 2, and is pretty much useless. (It is NOT the tier.)
    // We only need Type and Material.
    run {
        val match = Constants.materialRegex.matchEntire(itemName)
        if (match != null) {
            val (_, name) = match.destructured
            return ItemIdentity(l(name))
        }
    }
    // Ingredients just display their name, so we can look them up in the registry.
    run {
        val ingredient = Registries.ingredient.getFromDisplayName(itemName)
        if (ingredient != null) {
            return ItemIdentity(l(ingredient.name), Constants.ingredientTierColor(ingredient.tier))
        }
    }
    // Gear is a bit more complicated. Gear can be identified or unidentified.
    // Problem is, that is not super clear. Even the unidentified items have a name.
    // The only way to check is to see if the item has the right NBT data:
    // If the item has a "display" tag, and that tag has a "Lore" tag, then it is identified.
    run {
        val gear = Registries.gear.getFromDisplayName(itemName)
        if (gear != null) {
            val displayNBT = itemEntity.stack.nbt?.get("display")
            if (displayNBT is net.minecraft.nbt.NbtCompound && displayNBT.contains("Lore")) {
                return ItemIdentity(l(itemName), gear.tier.chatFormatting)
            }
            val text = t("wyoming.unID")
            text.append(" ")
            if (c.nametags.showGearRarity()) {
                text.append(capL(gear.tier.name))
                text.append(" ")
            }
            text.append(
                when (c.nametags.unIdMode()!!) {
                    ConfigModel.Nametags.UnIDMode.NAME -> l(itemName)
                    ConfigModel.Nametags.UnIDMode.TYPE -> capL(gear.type.name)
                    ConfigModel.Nametags.UnIDMode.NAME_AND_TYPE -> l("$itemName ${cap(gear.type.name)}")
                }
            )
            if (c.nametags.showGearLevel()) text.append(l(" (${gear.requirements.level})"))
            return ItemIdentity(text, gear.tier.chatFormatting)
        }
    }
    // Powders are very easy to identify. They have the display name "[ELEMENT] [TIER]".
    run {
        val match = Constants.powderRegex.matchEntire(itemName)
        if (match != null) {
            val (element, tier) = match.destructured
            return ItemIdentity(
                capL(element).append(" Powder ").append(t(tier)),
                Constants.powderColor(element)
            )
        }
    }
    // Emeralds just have the vanilla item names
    run {
        val res = mapOf<String, String>(
            "Emerald" to "em",
            "Block of Emerald" to "eb",
            "Bottle o' Enchanting" to "le",
        )[itemName]
        if (res != null) return ItemIdentity(
            l("${itemEntity.stack.count}")
                .append(t("wyoming.$res")),
            Fmt.GREEN, noPlural = true
        )
    }

    // Fallback
    return ItemIdentity(l("Unknown (${itemName})"), Fmt.UNDERLINE, display = false)
}