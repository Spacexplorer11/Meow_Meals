// src/main/kotlin/singh/akaalroop/meowmeals/MeowMeals.kt
package singh.akaalroop.meowmeals

import net.fabricmc.api.ModInitializer
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import org.slf4j.LoggerFactory
import singh.akaalroop.meowmeals.items.ModItems

/**
 * Main mod initializer for MeowMeals.
 * Handles registration of items and item groups.
 */
object MeowMeals : ModInitializer {
    /**
     * The unique identifier for the MeowMeals mod.
     */
    const val MOD_ID: String = "meowmeals"
    private val logger = LoggerFactory.getLogger(MOD_ID)

    val MEOWMEALS_GROUP_KEY: ResourceKey<CreativeModeTab> = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB,
        Identifier.fromNamespaceAndPath(MOD_ID, "meowmeals_group")
    )

    override fun onInitialize() {
        logger.info("Initializing MeowMeals Mod")
        ModItems.registerModItems()
    }
}