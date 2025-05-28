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
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object MeowMeals : ModInitializer {
    private const val MOD_ID = "meowmeals"
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
        }
        val CAT_FOOD_TAG = TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "cat_food"))

        fun isCatFood(item: Item): Boolean {
            return ItemStack(item).isIn(CAT_FOOD_TAG)
        }
        print("Is Cat Food: ${isCatFood(catFoodTin)}")
    }
}