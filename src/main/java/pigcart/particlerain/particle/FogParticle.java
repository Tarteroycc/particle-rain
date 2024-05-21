package pigcart.particlerain.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import pigcart.particlerain.ParticleRainClient;

public class FogParticle extends WeatherParticle {

    private FogParticle(ClientLevel level, double x, double y, double z, SpriteSet provider) {
        super(level, x, y, z, ParticleRainClient.config.rainDropGravity / 2, provider);
        this.lifetime = ParticleRainClient.config.particleRadius * 5;
        this.quadSize = 8F;

        this.rCol = ParticleRainClient.config.color.snowRed;
        this.gCol = ParticleRainClient.config.color.snowGreen;
        this.bCol = ParticleRainClient.config.color.snowBlue;

        this.roll = level.random.nextFloat() * Mth.PI;
        this.oRoll = this.roll;

        this.xd = gravity / 3;
        this.zd = gravity / 3;
    }

    public void tick() {
        super.tick();
        BlockState fallingTowards = level.getBlockState(this.pos.offset(3, -8, 3));
        BlockPos blockPos = this.pos.offset(3, -8, 3);
        if (level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockPos.getX(), blockPos.getZ()) >= blockPos.getY() || !fallingTowards.getFluidState().isEmpty()) {
            if (!shouldFade) {
                shouldFade = true;
                fadeTime = (int) (alpha * 10);
            }
        }
        if (onGround) {
            remove();
        } else {
            this.xd = gravity / 3;
            this.zd = gravity / 3;
        }
        double distance = Minecraft.getInstance().cameraEntity.position().distanceTo(this.pos.getCenter()) - 8;
        double cameraAlpha = Mth.clamp(distance / 20, 0, 1);
        if (distance < 20) {
            if (cameraAlpha < 0.1) remove();
            else if (alpha > cameraAlpha) alpha = (float) cameraAlpha;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet provider;

        public DefaultFactory(SpriteSet provider) {
            this.provider = provider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new FogParticle(level, x, y, z, this.provider);
        }
    }
}