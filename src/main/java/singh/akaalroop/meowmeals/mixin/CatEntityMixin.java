package singh.akaalroop.meowmeals.mixin;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
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


@Mixin(CatEntity.class)
public abstract class CatEntityMixin {

    @Shadow
    protected abstract void tryTame(PlayerEntity player);

    @Unique
    private void sendMeowMealsMessage(PlayerEntity player, String text, Formatting colour) {
        MutableText message = Text.literal("[MeowMeals] ")
                .formatted(Formatting.GOLD)
                .append(Text.literal(text).formatted(colour));
        player.sendMessage(message, false);
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
            if (stack.isOf(Registries.ITEM.get(Identifier.of("meowmeals", item)))) {
                cir.setReturnValue(true);
                return;
            }
        }
        if (stack.isOf(Registries.ITEM.get(Identifier.of("minecraft", "tropical_fish"))) ||
                stack.isOf(Registries.ITEM.get(Identifier.of("minecraft", "cooked_rabbit")))) {
            cir.setReturnValue(true);
        }
    }

    // Hook into interactMob instead, since it has the player and hand info.
    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteractMob(PlayerEntity player, net.minecraft.util.Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        CatEntity cat = (CatEntity) (Object) this;
        if (cat.getHealth() < cat.getMaxHealth() || !cat.isTamed() || cat.isBaby() || !cat.hasStatusEffect(StatusEffects.SATURATION) || !cat.hasStatusEffect(StatusEffects.JUMP_BOOST) || !cat.hasStatusEffect(StatusEffects.SPEED)) {
            if (!cat.getWorld().isClient()) {
                if (stack.isOf(Registries.ITEM.get(Identifier.of("meowmeals", "cat_food_tin")))) {
                    var actioned = false;
                    // Heal the cat if it’s hurt
                    if (cat.getHealth() < cat.getMaxHealth()) {
                        cat.heal(6.0f); // 3 hearts
                        actioned = true;
                    }

                    // Tame the cat if it’s not already tamed
                    if (!cat.isTamed()) {
                        cat.setOwner(player);
                        cat.setSitting(true);
                        cat.getWorld().sendEntityStatus(cat, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
                        sendMeowMealsMessage(player, "Your cat liked that so much that it's now tamed! 🐱", Formatting.GREEN);
                        actioned = true;
                    }

                    // Set in love if tamed and adult
                    if (cat.isTamed() && !cat.isBaby() && cat.isReadyToBreed()) {
                        cat.setLoveTicks(600); // 30 seconds of love mode, 1 tick = 50 ms, so 20 ticks = 1 second, so 600 ticks / 20 = 30
                        sendMeowMealsMessage(player, "Your cat is now in love mode! 🥰", Formatting.RED);
                        actioned = true;
                    }

                    // Consume one item from the stack
                    if (!player.getAbilities().creativeMode && actioned) {
                        stack.decrement(1);
                    }

                    cir.setReturnValue(ActionResult.SUCCESS); // or ActionResult.PASS / FAIL depending on logic
                    cir.cancel();
                } else if (stack.isOf(Registries.ITEM.get(Identifier.of("meowmeals", "fish_feast")))) {
                    var actioned = false;
                    // Heal the cat if it’s hurt
                    if (cat.getHealth() < cat.getMaxHealth()) {
                        cat.heal(5.0f); // 2.5 hearts
                        actioned = true;
                    }

                    // Tame the cat if it’s not already tamed
                    if (!cat.isTamed()) {
                        tryTame(player);
                        actioned = true;
                    }

                    // Breed
                    if (cat.isTamed() && !cat.isBaby() && cat.isReadyToBreed()) {
                        cat.setLoveTicks(600);
                        sendMeowMealsMessage(player, "Your cat is now in love mode! 🥰", Formatting.RED);
                        actioned = true;
                    }

                    // ✨ Add potion effects
                    if (!cat.hasStatusEffect(StatusEffects.SPEED) && cat.isTamed()) {
                        cat.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0));
                        sendMeowMealsMessage(player, "Your cat feels energised! ✨", Formatting.GREEN);
                        actioned = true;
                    } else if (actioned) {
                        cat.getWorld().addParticle(
                                ParticleTypes.HEART,
                                cat.getX(), cat.getY() + 1, cat.getZ(),
                                0.0, 0.1, 0.0
                        );
                        sendMeowMealsMessage(player, "The cat enjoyed that! 🐟", Formatting.YELLOW);
                    }

                    // Consume item
                    if (!player.getAbilities().creativeMode && actioned) {
                        stack.decrement(1);
                        player.getInventory().insertStack(new ItemStack(Items.BOWL));
                    }

                    cir.setReturnValue(ActionResult.SUCCESS);
                    cir.cancel();
                } else if (stack.isOf(Registries.ITEM.get(Identifier.of("meowmeals", "meat_feast")))) {
                    var actioned = false;
                    // Heal the cat if it’s hurt
                    if (cat.getHealth() < cat.getMaxHealth()) {
                        cat.heal(8.0f); // 4 hearts
                        actioned = true;
                    }

                    // Tame the cat if it’s not already tamed
                    if (!cat.isTamed()) {
                        tryTame(player);
                        actioned = true;
                    }

                    // Breed
                    if (cat.isTamed() && !cat.isBaby() && cat.isReadyToBreed()) {
                        cat.setLoveTicks(600);
                        sendMeowMealsMessage(player, "Your cat is now in love mode! 🥰", Formatting.RED);
                        actioned = true;
                    }

                    // ✨ Add potion effects
                    if (!cat.hasStatusEffect(StatusEffects.SATURATION) && cat.isTamed()) {
                        cat.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 400, 0));
                        sendMeowMealsMessage(player, "Your cat feels more full! 😋", Formatting.GREEN);
                        actioned = true;
                    } else if (actioned) {
                        cat.getWorld().addParticle(
                                ParticleTypes.HEART,
                                cat.getX(), cat.getY() + 1, cat.getZ(),
                                0.0, 0.1, 0.0
                        );
                        sendMeowMealsMessage(player, "The cat enjoyed that! 🍗", Formatting.YELLOW);
                    }

                    // Consume item
                    if (!player.getAbilities().creativeMode && actioned) {
                        stack.decrement(1);
                        player.getInventory().insertStack(new ItemStack(Items.BOWL));
                    }
                    cir.setReturnValue(ActionResult.SUCCESS);
                    cir.cancel();
                } else if (stack.isOf(Registries.ITEM.get(Identifier.of("meowmeals", "smoked_rabbit")))) {
                    var actioned = false;
                    // Heal the cat if it’s hurt
                    if (cat.getHealth() < cat.getMaxHealth()) {
                        cat.heal(4.0f); // 2 hearts
                        actioned = true;
                    }

                    // Tame the cat if it’s not already tamed
                    if (!cat.isTamed()) {
                        tryTame(player);
                        actioned = true;
                    }

                    // Breed
                    if (cat.isTamed() && !cat.isBaby() && cat.isReadyToBreed()) {
                        cat.setLoveTicks(600);
                        sendMeowMealsMessage(player, "Your cat is now in love mode! 🥰", Formatting.RED);
                        actioned = true;
                    }

                    // ✨ Add potion effects
                    if (!cat.hasStatusEffect(StatusEffects.JUMP_BOOST) && cat.isTamed()) {
                        cat.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 400, 0));
                        sendMeowMealsMessage(player, "Your cat feels bouncier, like a rabbit! 🐇", Formatting.GREEN);
                        actioned = true;
                    } else if (actioned) {
                        cat.getWorld().addParticle(
                                ParticleTypes.HEART,
                                cat.getX(), cat.getY() + 1, cat.getZ(),
                                0.0, 0.1, 0.0
                        );
                        sendMeowMealsMessage(player, "The cat enjoyed that! 😋", Formatting.YELLOW);
                    }

                    // Consume item
                    if (!player.getAbilities().creativeMode && actioned) {
                        stack.decrement(1);
                    }

                    cir.setReturnValue(ActionResult.SUCCESS);
                    cir.cancel();
                }
            }
        }
    }
}