// src/main/kotlin/singh/akaalroop/meowmeals/MeowMeals.kt
package singh.akaalroop.meowmeals

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import singh.akaalroop.meowmeals.MeowMealsConstants.MOD_ID
import singh.akaalroop.meowmeals.items.ModItems

/**
 * Main mod initializer for MeowMeals.
 * Handles registration of items and item groups.
 */
object MeowMeals : ModInitializer {
    /**
     * The unique identifier for the MeowMeals mod.
     */

    private val logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        logger.info("Initializing MeowMeals Mod")
        ModItems.registerModItems()
    }
}