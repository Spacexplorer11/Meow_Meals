package singh.akaalroop.meowmeals.items

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import singh.akaalroop.meowmeals.MeowMeals.MEOWMEALS_GROUP_KEY
import singh.akaalroop.meowmeals.MeowMeals.MOD_ID

object ModItems {

    lateinit var catFoodTin: Item
    lateinit var fishFeast: Item
    lateinit var meatFeast: Item
    lateinit var smokedRabbit: Item

    private fun registerItem(name: String, item: Item): Item {
        return Registry.register(
            BuiltInRegistries.ITEM,
            Identifier.fromNamespaceAndPath(MOD_ID, name),
            item
        )
    }

    fun registerModItems() {
        catFoodTin = registerItem("cat_food_tin", Item(Item.Properties()))
        fishFeast = registerItem("fish_feast", Item(Item.Properties()))
        meatFeast = registerItem("meat_feast", Item(Item.Properties()))
        smokedRabbit = registerItem("smoked_rabbit", Item(Item.Properties()))

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