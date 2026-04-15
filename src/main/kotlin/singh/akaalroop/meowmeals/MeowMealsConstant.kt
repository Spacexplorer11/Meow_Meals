package singh.akaalroop.meowmeals

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab

object MeowMealsConstants {
    const val MOD_ID = "meowmeals"

    val MEOWMEALS_GROUP_KEY: ResourceKey<CreativeModeTab> = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB,
        Identifier.fromNamespaceAndPath(MOD_ID, "meowmeals_group")
    )
}