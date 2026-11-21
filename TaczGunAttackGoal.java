package com.voidix.monikaguns;

import com.mojang.logging.LogUtils;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.EnumSet;

/** Shoots her current target with the TACZ gun in her main hand. Logs lines tagged [MonikaGuns]. */
public class TaczGunAttackGoal extends Goal {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Tuning
    private static final int FIRE_COOLDOWN_TICKS = 4;   // lower = faster fire rate
    private static final int RELOAD_TICKS = 40;         // pause when empty before refilling
    private static final int DRAW_TICKS = 20;           // wait for draw animation

    private final PathfinderMob mob;
    private int cooldown;
    private int drawDelay;
    private int reloadTimer;
    private int maxAmmo = -1;
    private int idleLogTimer;
    private int tickLogTimer;
    private boolean drawn;

    public TaczGunAttackGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private boolean validTarget(LivingEntity t) {
        return t != null && t.isAlive() && !(t instanceof Player);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        boolean hasGun = IGun.getIGunOrNull(mob.getMainHandItem()) != null;
        boolean ok = validTarget(target) && hasGun;

        if (++idleLogTimer >= 100) {
            idleLogTimer = 0;
            LOGGER.debug("[MonikaGuns] idle check: target={} mainHand={} isGun={} canUse={}",
                    target == null ? "none" : target.getName().getString(),
                    ForgeRegistries.ITEMS.getKey(mob.getMainHandItem().getItem()), hasGun, ok);
        }
        return ok;
    }

    @Override
    public boolean canContinueToUse() {
        return validTarget(mob.getTarget()) && IGun.getIGunOrNull(mob.getMainHandItem()) != null;
    }

    @Override
    public void start() {
        LOGGER.debug("[MonikaGuns] goal STARTED");
    }

    @Override
    public void stop() {
        LOGGER.debug("[MonikaGuns] goal STOPPED");
        drawn = false;
        drawDelay = 0;
        reloadTimer = 0;
        maxAmmo = -1; // re-learn the gun's capacity next time
        mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        // Aim from her eyes to the middle of the target's body
        double dx = target.getX() - mob.getX();
        double dz = target.getZ() - mob.getZ();
        double dy = (target.getY() + target.getBbHeight() * 0.5) - mob.getEyeY();
        float yaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        float pitch = (float) (-(Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * (180.0 / Math.PI)));

        // Turn her body/head right now so the shot origin matches (look control alone lags a few ticks)
        mob.setYRot(yaw);
        mob.setYHeadRot(yaw);
        mob.setYBodyRot(yaw);
        mob.setXRot(pitch);

        IGunOperator op = IGunOperator.fromLivingEntity(mob);

        if (!drawn) {
            op.draw(mob::getMainHandItem);
            drawn = true;
            drawDelay = DRAW_TICKS;
            LOGGER.debug("[MonikaGuns] draw() called");
        }

        ItemStack stack = mob.getMainHandItem();
        IGun gun = IGun.getIGunOrNull(stack);
        if (gun == null) return;

        int ammo = gun.getCurrentAmmoCount(stack);
        if (maxAmmo < 0) {
            maxAmmo = ammo > 0 ? ammo : 30; // learn capacity from the gun as given; give her a FULL gun
            LOGGER.debug("[MonikaGuns] learned magazine size = {}", maxAmmo);
        }

        boolean reloading = false;
        if (ammo <= 0) {
            reloading = true;
            if (reloadTimer <= 0) reloadTimer = RELOAD_TICKS;
            if (--reloadTimer <= 0) {
                gun.setCurrentAmmoCount(stack, maxAmmo);
                reloading = false;
            }
        }

        boolean canSee = mob.getSensing().hasLineOfSight(target);
        if (mob.distanceToSqr(target) > 400 || !canSee) {
            mob.getNavigation().moveTo(target, 1.0);
        } else {
            mob.getNavigation().stop();
        }

        if (drawDelay > 0) { drawDelay--; return; }

        final float shotPitch = pitch;
        final float shotYaw = yaw;
        boolean tryShoot = (--cooldown <= 0) && canSee && !reloading;
        int before = gun.getCurrentAmmoCount(stack);
        if (tryShoot) {
            op.shoot(() -> shotPitch, () -> shotYaw);
            cooldown = FIRE_COOLDOWN_TICKS;
        }
        int after = gun.getCurrentAmmoCount(stack);

        if (++tickLogTimer >= 20) {
            tickLogTimer = 0;
            LOGGER.debug("[MonikaGuns] tick: target={} dist={} canSee={} reloading={} ammo {}->{} yaw={} pitch={}",
                    target.getName().getString(),
                    String.format("%.1f", Math.sqrt(mob.distanceToSqr(target))),
                    canSee, reloading, before, after,
                    String.format("%.0f", shotYaw), String.format("%.0f", shotPitch));
        }
    }
}
