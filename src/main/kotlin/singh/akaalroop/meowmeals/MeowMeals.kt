package singh.akaalroop.meowmeals

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import singh.akaalroop.meowmeals.items.CatFoodTin

object MeowMeals : ModInitializer {
	private const val MOD_ID = "meowmeals"
    private val logger = LoggerFactory.getLogger(MOD_ID)
	private val MEOWMEALS_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier(MOD_ID, (MOD_ID + "_group")))

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		// Initialize your mod here, e.g., register items, blocks, etc.
		val Cat_Food_Tin = Registry.register(Registries.ITEM, Identifier(MOD_ID, "cat_food_tin"), CatFoodTin(FabricItemSettings()))
		Registry.register(
			Registries.ITEM_GROUP,
			MEOWMEALS_GROUP,
			FabricItemGroup.builder().icon { ItemStack(Cat_Food_Tin) }
				.displayName(
					Text.translatable("itemGroup.meowmeals.meowmeals_group")
				)
				.build(),
		)
		ItemGroupEvents.modifyEntriesEvent(MEOWMEALS_GROUP).register(ItemGroupEvents.ModifyEntries { content ->
			content.add(Cat_Food_Tin)
		})
	}
}