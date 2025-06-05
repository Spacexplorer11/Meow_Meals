// src/main/kotlin/singh/akaalroop/meowmeals/MeowMeals.kt
package singh.akaalroop.meowmeals

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

// This is all the code run when the mod is initialised
object MeowMeals : ModInitializer {
    const val MOD_ID: String = "meowmeals"
    private val logger = LoggerFactory.getLogger(MOD_ID)
    private val MEOWMEALS_GROUP = RegistryKey.of(
        RegistryKeys.ITEM_GROUP,
        Identifier.of(MOD_ID, "${MOD_ID}_group")
    )

    override fun onInitialize() {
        logger.info("Initializing MeowMeals Mod")

        val catFoodTin = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "cat_food_tin"),
            Item(Item.Settings())
        )

        val fishFeast = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "fish_feast"),
            Item(Item.Settings())
        )

        val meatFeast = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "meat_feast"),
            Item(Item.Settings())
        )

        val smokedRabbit = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "smoked_rabbit"),
            Item(Item.Settings())
        )

        Registry.register(
            Registries.ITEM_GROUP,
            MEOWMEALS_GROUP,
            FabricItemGroup.builder()
                .icon { ItemStack(catFoodTin) }
                .displayName(Text.translatable("itemGroup.meowmeals.meowmeals_group"))
                .build()
        )

        ItemGroupEvents.modifyEntriesEvent(MEOWMEALS_GROUP).register { content ->
            content.add(catFoodTin)
            content.add(fishFeast)
            content.add(meatFeast)
            content.add(smokedRabbit)
        }
    }
}