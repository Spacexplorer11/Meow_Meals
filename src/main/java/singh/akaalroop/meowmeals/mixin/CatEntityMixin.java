package singh.akaalroop.meowmeals.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(CatEntity.class)
public class CatEntityMixin {

    @Inject(method = "isBreedingItem", at = @At("HEAD"), cancellable = true)
    private void isBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(Registries.ITEM.get(Identifier.of("meowmeals", "cat_food_tin")))) {
            cir.setReturnValue(true);
        }
        else if (stack.isOf(Registries.ITEM.get(Identifier.of("meowmeals", "fish_feast")))) {
            cir.setReturnValue(true);
        }
        else if (stack.isOf(Registries.ITEM.get(Identifier.of("meowmeals", "meat_feast")))) {
            cir.setReturnValue(true);
        }
        else if (stack.isOf(Registries.ITEM.get(Identifier.of("minecraft", "tropical_fish")))) {
            cir.setReturnValue(true);
        }
        else if (stack.isOf(Registries.ITEM.get(Identifier.of("meowmeals", "smoked_rabbit")))) {
            cir.setReturnValue(true);
        }
        else if (stack.isOf(Registries.ITEM.get(Identifier.of("minecraft", "cooked_rabbit")))) {
            cir.setReturnValue(true);
        }
    }

    // Hook into interactMob instead, since it has the player and hand info.
    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteractMob(PlayerEntity player, net.minecraft.util.Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(Registries.ITEM.get(Identifier.of("meowmeals", "cat_food_tin")))) {
            CatEntity cat = (CatEntity)(Object)this;

            // Heal the cat if it’s hurt
            if (cat.getHealth() < cat.getMaxHealth()) {
                cat.heal(6.0f); // 3 hearts
            }

            // Tame the cat if it’s not already tamed
            if (!cat.isTamed()) {
                cat.setTamed(true, true);
                cat.setOwner(player);
            }

            // Set in love  if tamed and adult
            if (cat.isTamed() && !cat.isBaby()) {
                cat.setLoveTicks(600); // 30 seconds of love mode, 1 tick = 50ms so 20 ticks = 1 second, so 600 ticks / 20 = 30
            }

            // Consume one item from stack
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            cir.setReturnValue(ActionResult.SUCCESS); // or ActionResult.PASS / FAIL depending on logic
            cir.cancel();
        }
    }
}