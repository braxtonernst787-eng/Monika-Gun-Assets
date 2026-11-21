package com.voidix.monikaguns;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

/**
 * Attaches the gun attack goal to Monicraft's Monika whenever she loads into a world.
 * Goals are not saved to disk, so this must run every time she joins.
 */
@Mod.EventBusSubscriber(modid = MonikaGuns.MOD_ID)
public class MonikaGunHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation MONIKA = new ResourceLocation("monicraft", "monika");

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());
        if (!MONIKA.equals(id)) return;

        if (!(event.getEntity() instanceof PathfinderMob monika)) {
            LOGGER.warn("[MonikaGuns] Found {} but it is not a PathfinderMob ({}); gun AI not attached.",
                    id, event.getEntity().getClass().getName());
            return;
        }

        // Priority 0 = highest, so her own goals cannot outrank the gun goal.
        monika.goalSelector.addGoal(0, new TaczGunAttackGoal(monika));
        LOGGER.debug("[MonikaGuns] Attached gun goal to {}", id);
    }
}
