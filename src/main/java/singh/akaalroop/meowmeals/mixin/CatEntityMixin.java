package singh.akaalroop.meowmeals.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static singh.akaalroop.meowmeals.MeowMealsConstants.MOD_ID;


@Mixin(Cat.class)
public abstract class CatEntityMixin {

    @Unique
    private void sendMeowMealsMessage(Player player, String text, ChatFormatting colour) {
        MutableComponent message = Component.literal("[MeowMeals] ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(text).withStyle(colour));
        player.sendSystemMessage(message);
    }

    @Unique
    private void sendMeowMealsBreedingMessage(Player player) {
        sendMeowMealsMessage(player, "Your cat is now in love mode! 🥰", ChatFormatting.RED);
    }

    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void isFood(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        String[] items = {
                "cat_food_tin",
                "fish_feast",
                "meat_feast.json",
                "smoked_rabbit.json"
        };
        for (String item : items) {
            if (itemStack.is(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MOD_ID, item)))) {
                cir.setReturnValue(true);
                return;
            }
        }
        if (itemStack.is(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("minecraft", "tropical_fish"))) ||
                itemStack.is(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("minecraft", "cooked_rabbit")))) {
            cir.setReturnValue(true);
        }
    }


    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, net.minecraft.world.InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        Cat cat = (Cat) (Object) this;

        if (!cat.level().isClientSide()) {
            boolean actioned = false;

            // Cat Food Tin
            if (stack.is(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MOD_ID, "cat_food_tin")))) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(6.0f);
                    actioned = true;
                } else if (!cat.isTame()) {
                    cat.tame(player);
                    cat.setOrderedToSit(true);
                    cat.level().broadcastEntityEvent(cat, EntityEvent.TAMING_SUCCEEDED);
                    sendMeowMealsMessage(player, "Your cat liked that so much that it's now tamed! 🐱", ChatFormatting.YELLOW);
                    actioned = true;
                } else if (cat.isTame() && !cat.isInLove() && cat.getAge() == 0) {
                    cat.setInLove(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                }

                // Fish Feast
            } else if (stack.is(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MOD_ID, "fish_feast")))) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(5.0f);
                    actioned = true;
                } else if (cat.isTame() && !cat.isInLove() && cat.getAge() == 0) {
                    cat.setInLove(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                } else if (!cat.hasEffect(MobEffects.SPEED)) {
                    cat.addEffect(new MobEffectInstance(MobEffects.SPEED, 400, 0));
                    sendMeowMealsMessage(player, "Your cat feels energised! ✨", ChatFormatting.GREEN);
                    actioned = true;
                }
                // Meat Feast
            } else if (stack.is(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MOD_ID, "meat_feast.json")))) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(8.0f);
                    actioned = true;
                } else if (cat.isTame() && !cat.isInLove() && cat.getAge() == 0) {
                    cat.setInLove(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                } else if (!cat.hasEffect(MobEffects.SATURATION)) {
                    cat.addEffect(new MobEffectInstance(MobEffects.SATURATION, 400, 0));
                    sendMeowMealsMessage(player, "Your cat feels more full! 😋", ChatFormatting.GREEN);
                    actioned = true;
                }

                // Smoked Rabbit
            } else if (stack.is(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MOD_ID, "smoked_rabbit.json")))) {
                if (cat.getHealth() < cat.getMaxHealth()) {
                    cat.heal(4.0f);
                    actioned = true;
                } else if (cat.isTame() && !cat.isInLove() && cat.getAge() == 0) {
                    cat.setInLove(player);
                    sendMeowMealsBreedingMessage(player);
                    actioned = true;
                } else if (!cat.hasEffect(MobEffects.JUMP_BOOST)) {
                    cat.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 400, 0));
                    sendMeowMealsMessage(player, "Your cat feels bouncier, like a rabbit! 🐇", ChatFormatting.GREEN);
                    actioned = true;
                }
            }

            if (actioned) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    if (stack.is(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MOD_ID, "fish_feast")))
                            || stack.is(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MOD_ID, "meat_feast.json")))) {
                        player.getInventory().add(new ItemStack(Items.BOWL));
                    }
                }
                cat.playSound(SoundEvents.CAT_EAT_BABY.value(), 1.0F, 1.0F);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}