package net.eagle.ancientartifacts.item.custom;

import net.eagle.ancientartifacts.block.custom.DragonPedestal;
import net.eagle.ancientartifacts.item.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;

public class EndStaff extends Item {
    public EndStaff(Properties props) {
        super(props);
    }

    // Called when right-clicking a block (same as Fabric's useOnBlock)
    @Override
    public InteractionResult useOn(net.minecraft.world.item.context.UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!level.isClientSide) {
            if (ctx.getPlayer() instanceof ServerPlayer sp && state.hasProperty(DragonPedestal.ORB_INFINIUM)
                    && state.getValue(DragonPedestal.ORB_INFINIUM)) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(sp, pos, ctx.getItemInHand());
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // Called when right-clicking air (same as Fabric's use)
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Only run if this IS the End Staff
        if (stack.getItem() != ModItems.END_STAFF.get()) { // if ModItems is a RegistryObject<Item>
            return InteractionResultHolder.pass(stack);
        }

        // Cooldown: 40 ticks (2 seconds)
        player.getCooldowns().addCooldown(this, 40);

        // Raycast out to 25 blocks from player's view
        final double range = 25.0;
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(look.scale(range));

        ClipContext ctx = new ClipContext(
                eyePos, endPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        );

        BlockHitResult hit = level.clip(ctx);

        // Particle trail (client-side visuals)
        if (level.isClientSide) {
            double stepSize = 0.4;
            Vec3 p = eyePos.add(look.scale(2.0)); // small offset out of the face
            Vec3 step = look.normalize().scale(stepSize);

            while (p.distanceTo(endPos) > stepSize) {
                level.addParticle(ParticleTypes.PORTAL, p.x,          p.y + 0.7, p.z, 0.5, 0.0, 0.5);
                level.addParticle(ParticleTypes.PORTAL, p.x + 0.1,    p.y + 0.8, p.z, 0.5, 0.0, 0.5);
                level.addParticle(ParticleTypes.PORTAL, p.x,          p.y + 0.8, p.z, 0.5, 0.0, 0.5);
                level.addParticle(ParticleTypes.PORTAL, p.x - 0.1,    p.y + 0.8, p.z, 0.5, 0.0, 0.5);
                p = p.add(step);
            }
        }

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = hit.getBlockPos();

            if (level.isClientSide) {
                // explosion-like impact at the end (client only)
                Vec3 end = hit.getLocation();
                level.addParticle(ParticleTypes.EXPLOSION_EMITTER, end.x, end.y, end.z, 0, 0, 0);
            } else {
                // server-side sounds so other players hear them
                level.playSound(null, hitPos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.NEUTRAL, 0.7f, 0.3f);
                level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.AMBIENT, 0.5f, 0.3f);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
