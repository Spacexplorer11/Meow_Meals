package singh.akaalroop.meowmeals.mixin;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.sound.SoundEvents.ENTITY_CAT_EAT;


@Mixin(CatEntity.class)
public abstract class CatEntityMixin {
    @Unique
    private static final String MOD_ID = "meowmeals";
    @Unique
    private static final String LOVE_MESSAGE = "Your cat is now in love mode! 🥰";

    @Shadow
    protected abstract void tryTame(PlayerEntity player);

    @Unique
    private void sendMeowMealsMessage(PlayerEntity player, String text, Formatting colour) {
        MutableText message = Text.literal("[MeowMeals] ")
                .formatted(Formatting.GOLD)
                .append(Text.literal(text).formatted(colour));
        player.sendMessage(message, false);
    }

    @Unique
    private void sendMeowMealsBreedingMessage(PlayerEntity player) {
        sendMeowMealsMessage(player, LOVE_MESSAGE, Formatting.RED);
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
            if (stack.isOf(Registries.ITEM.get(Identifier.of(MOD_ID, item)))) {
                cir.setReturnValue(true);
                return;
            }
        }
        if (stack.isOf(Registries.ITEM.get(Identifier.of("minecraft", "tropical_fish"))) ||
                stack.isOf(Registries.ITEM.get(Identifier.of("minecraft", "cooked_rabbit")))) {
            cir.setReturnValue(true);
        }
    }


    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteractMob(PlayerEntity player, net.minecraft.util.Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        CatEntity cat = (CatEntity) (Object) this;

        if (!cat.getWorld().isClient()) {
            boolean actioned = false;

            // Cat Food Tin
            if (stack.isOf(Registries.ITEM.get(Identifier.of(MOD_ID, "cat_food_tin")))) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(6.0f);
                    actioned = true;
                } else if (!cat.isTamed()) {
                    cat.setOwner(player);
                    cat.getWorld().sendEntityStatus(cat, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
                    sendMeowMealsMessage(player, "Your cat liked that so much that it's now tamed! 🐱", Formatting.GREEN);
                    actioned = true;
                } else if (!cat.isInLove()) {
                    cat.lovePlayer(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                }

                // Fish Feast
            } else if (stack.isOf(Registries.ITEM.get(Identifier.of(MOD_ID, "fish_feast")))) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(5.0f);
                    actioned = true;
                } else if (!cat.isTamed()) {
                    tryTame(player);
                    actioned = true;
                } else if (!cat.isInLove()) {
                    cat.lovePlayer(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                } else if (!cat.hasStatusEffect(StatusEffects.SPEED)) {
                    cat.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0));
                    sendMeowMealsMessage(player, "Your cat feels energised! ✨", Formatting.GREEN);
                    actioned = true;
                }
                // Meat Feast
            } else if (stack.isOf(Registries.ITEM.get(Identifier.of(MOD_ID, "meat_feast")))) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(8.0f);
                    actioned = true;
                } else if (!cat.isTamed()) {
                    tryTame(player);
                    actioned = true;
                } else if (!cat.isInLove()) {
                    cat.lovePlayer(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                } else if (!cat.hasStatusEffect(StatusEffects.SATURATION)) {
                    cat.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 400, 0));
                    sendMeowMealsMessage(player, "Your cat feels more full! 😋", Formatting.GREEN);
                    actioned = true;
                }

                // Smoked Rabbit
            } else if (stack.isOf(Registries.ITEM.get(Identifier.of(MOD_ID, "smoked_rabbit")))) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(4.0f);
                    actioned = true;
                } else if (!cat.isTamed()) {
                    tryTame(player);
                    actioned = true;
                } else if (!cat.isInLove()) {
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
                    if (stack.isOf(Registries.ITEM.get(Identifier.of(MOD_ID, "fish_feast")))
                            || stack.isOf(Registries.ITEM.get(Identifier.of(MOD_ID, "meat_feast")))) {
                        player.getInventory().insertStack(new ItemStack(Items.BOWL));
                    }
                }
                cat.playSound(ENTITY_CAT_EAT);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}