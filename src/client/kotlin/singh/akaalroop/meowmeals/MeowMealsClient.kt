package singh.akaalroop.meowmeals

import net.fabricmc.api.ClientModInitializer

/**
 * Handles client-side initialisation for the MeowMeals mod.
 * Used to register client-only features such as rendering or client event handlers.
 */
object MeowMealsClient : ClientModInitializer { // This is to set up client-only logic
    override fun onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
    }
}