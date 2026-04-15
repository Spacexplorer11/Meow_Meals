package singh.akaalroop.meowmeals.items

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import singh.akaalroop.meowmeals.MeowMealsConstants.MEOWMEALS_GROUP_KEY
import singh.akaalroop.meowmeals.MeowMealsConstants.MOD_ID

object ModItems {

    private fun registerItem(name: String, factory: (Item.Properties) -> Item): Item {
        // 1. Create the Registry Key first
        val key = ResourceKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(MOD_ID, name)
        )

        // 2. Initialize properties with that key
        val properties = Item.Properties().setId(key)

        // 3. Register the item created by the factory
        return Registry.register(BuiltInRegistries.ITEM, key, factory(properties))
    }

    fun registerModItems() {
        // Pass a lambda that creates the item using the prepared properties
        val catFoodTin = registerItem("cat_food_tin") { prop -> Item(prop) }
        val fishFeast = registerItem("fish_feast") { prop -> Item(prop) }
        val meatFeast = registerItem("meat_feast") { prop -> Item(prop) }
        val smokedRabbit = registerItem("smoked_rabbit") { prop -> Item(prop) }

        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            MEOWMEALS_GROUP_KEY,
            FabricCreativeModeTab.builder()
                .icon { ItemStack(catFoodTin) }
                .title(Component.translatable("itemGroup.meowmeals.meowmeals_group"))
                .build()
        )

        CreativeModeTabEvents.modifyOutputEvent(MEOWMEALS_GROUP_KEY).register { content ->
            content.accept(catFoodTin)
            content.accept(fishFeast)
            content.accept(meatFeast)
            content.accept(smokedRabbit)
        }
    }
}