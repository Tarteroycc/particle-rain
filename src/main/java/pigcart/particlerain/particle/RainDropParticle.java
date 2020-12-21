package pigcart.particlerain.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

public class RainDropParticle extends SpriteBillboardParticle {

    protected RainDropParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider provider) {
        super(clientWorld, d, e, f);
        this.setSprite(provider);

        this.gravityStrength = 0.7F;
        this.maxAge = 200;

        this.velocityX = 0.0F;
        this.velocityY = -0.7F;
        this.velocityZ = 0.0F;

        this.scale = 0.15F;
    }

    public void tick() {
        super.tick();
        if (this.age > this.maxAge - 1/0.06F || this.onGround || this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.WATER)) {
                this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() { return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT; }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider provider;

        public DefaultFactory(SpriteProvider provider) {
            this.provider = provider;
        }

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new RainDropParticle(world, x, y, z, this.provider);
        }
    }
}