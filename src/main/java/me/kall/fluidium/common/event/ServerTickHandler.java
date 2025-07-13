package me.kall.fluidium.common.event;

import me.kall.fluidium.common.cache.PlayerRegionCache;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerTickHandler {
    private static final PlayerRegionCache CACHE = new PlayerRegionCache();

    public static PlayerRegionCache getCache() {
        return CACHE;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            CACHE.update();
        }
    }
}
