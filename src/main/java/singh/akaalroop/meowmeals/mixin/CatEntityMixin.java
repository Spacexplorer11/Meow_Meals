package singh.akaalroop.meowmeals.mixin;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.sound.SoundEvents.ENTITY_CAT_EAT;
import static singh.akaalroop.meowmeals.MeowMeals.MOD_ID;


@Mixin(CatEntity.class)
public abstract class CatEntityMixin {

    @Unique
    private boolean isClientSide() {
        return ((CatEntity)(Object)this).getWorld().isClient();
    }

    @Unique
    private void sendPositiveReactionParticles(CatEntity cat) {
        cat.getWorld().sendEntityStatus(cat, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
    }

    @Unique
    private void givePlayerBowl(PlayerEntity player) {
        player.getInventory().insertStack(new ItemStack(Items.BOWL));
    }

    @Unique
    private void sendMeowMealsMessage(PlayerEntity player, String text, Formatting colour) {
        MutableText message = createMeowMealsMessage(text, colour);
        player.sendMessage(message, false);
    }

    @Unique
    private MutableText createMeowMealsMessage(String text, Formatting colour) {
        MutableText prefix = Text.literal("[MeowMeals] ");
        MutableText content = Text.literal(text);
        prefix.setStyle(createGoldStyle());
        content.setStyle(content.getStyle().withColor(colour));
        return appendText(prefix, content);
    }

    @Unique
    private void sendMeowMealsBreedingMessage(PlayerEntity player) {
        sendMeowMealsMessage(player, "Your cat is now in love mode! 🥰", Formatting.RED);
    }

    @Unique
    private boolean isModItem(ItemStack stack, String path) {
        return stack.isOf(getModItem(path));
    }

    @Unique
    private Item getModItem(String path) {
        return Registries.ITEM.get(Identifier.of(MOD_ID, path));
    }

    @Unique
    private boolean isVanillaItem(ItemStack stack, String path) {
        return stack.isOf(Registries.ITEM.get(Identifier.of("minecraft", path)));
    }

    @Unique
    private Style createGoldStyle() {
        return applyGoldFormatting(getGoldStyleBase());
    }

    @Unique
--- a/src/main/java/singh/akaalroop/meowmeals/mixin/CatEntityMixin.java
@@ -99,7 +99,6 @@
-    @Unique
-    private MutableText appendText(MutableText prefix, MutableText content) {
-        return prefix.append(content);
-    }
@@ -120,7 +119,7 @@
-        return appendText(prefix, content);
+        return prefix.append(content);

    @Unique
    private Style getGoldStyleBase() {
        return Style.EMPTY;
    }

    @Unique
    private Style applyGoldFormatting(Style base) {
        return base.withColor(Formatting.GOLD);
    }


    @Inject(method = "isBreedingItem", at = @At("HEAD"), cancellable = true)
    private void isBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        String[] items = {
                "cat_food_tin",
                "fish_feast",
                "meat_feast",
                "smoked_rabbit"
        };
        for (String item : items) {
            if (isModItem(stack, item)) {
                cir.setReturnValue(true);
                return;
            }
        }
        if (isVanillaItem(stack, "tropical_fish") || isVanillaItem(stack, "cooked_rabbit")) {
            cir.setReturnValue(true);
        }
    }


    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteractMob(PlayerEntity player, net.minecraft.util.Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        CatEntity cat = (CatEntity) (Object) this;

        if (!isClientSide()) {
            boolean actioned = false;

            // Cat Food Tin
            if (isModItem(stack, "cat_food_tin")) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(6.0f);
                    actioned = true;
                } else if (!cat.isTamed()) {
                    cat.setOwner(player);
                    cat.setSitting(true);
                    sendPositiveReactionParticles(cat);
                    sendMeowMealsMessage(player, "Your cat liked that so much that it's now tamed! 🐱", Formatting.YELLOW);
                    actioned = true;
                } else if (cat.isTamed() && !cat.isInLove() && cat.getBreedingAge() == 0) {
                    cat.lovePlayer(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                }

                // Fish Feast
            } else if (isModItem(stack, "fish_feast")) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(5.0f);
                    actioned = true;
                } else if (cat.isTamed() && !cat.isInLove() && cat.getBreedingAge() == 0) {
                    cat.lovePlayer(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                } else if (!cat.hasStatusEffect(StatusEffects.SPEED)) {
                    cat.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0));
                    sendMeowMealsMessage(player, "Your cat feels energised! ✨", Formatting.GREEN);
                    actioned = true;
                }
                // Meat Feast
            } else if (isModItem(stack, "meat_feast")) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(8.0f);
                    actioned = true;
                } else if (cat.isTamed() && !cat.isInLove() && cat.getBreedingAge() == 0) {
                    cat.lovePlayer(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                } else if (!cat.hasStatusEffect(StatusEffects.SATURATION)) {
                    cat.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 400, 0));
                    sendMeowMealsMessage(player, "Your cat feels more full! 😋", Formatting.GREEN);
                    actioned = true;
                }

                // Smoked Rabbit
            } else if (isModItem(stack, "smoked_rabbit")) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(4.0f);
                    actioned = true;
                } else if (cat.isTamed() && !cat.isInLove() && cat.getBreedingAge() == 0) {
                    cat.lovePlayer(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                } else if (!cat.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
                    cat.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 400, 0));
                    sendMeowMealsMessage(player, "Your cat feels bouncier, like a rabbit! 🐇", Formatting.GREEN);
                    actioned = true;
                }
            }

            if (actioned) {
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                    if (isModItem(stack, "fish_feast") || isModItem(stack, "meat_feast")) {
                        givePlayerBowl(player);
                    }
                }
                cat.playSound(ENTITY_CAT_EAT, 1.0F, 1.0F);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}