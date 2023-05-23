package cx.lexi.wyoming.mixin;

import cx.lexi.wyoming.Wyoming;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(method = "applyWaterBuoyancy()V", at = @At("HEAD"), cancellable = true)
    private void applyWaterBuoyancy(CallbackInfo ci) {
        if (Wyoming.Companion.getConfig().visual.disableBuoyancy()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;

        // so this `BlockPos` juggling seems a bit stupid but `net.minecraft.entity.ItemEntity.tick` does it too
        // there are better methods for this but im not 100% sure if they do the same thing (which is important)
        // also non-full blocks are not handled at all, but ItemEntity.tick doesn't either lol

        Block below = self.world.getBlockState(BlockPos.ofFloored(self.getX(), self.getY() - 1.0, self.getZ())).getBlock();

        if (self.isTouchingWater()) {
            boolean nearGround = self.getY() % 1.0f < 0.2f;
            if (nearGround && below.getDefaultState().getMaterial().isSolid()) {
                // To emulate the old physics multiplying by 0.73 works. I don't know why. I just tried values until it worked.
                self.setVelocity(self.getVelocity().multiply(0.73, 1.0, 0.73));
            }
        }
    }
}
