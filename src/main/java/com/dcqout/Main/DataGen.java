package com.dcqout.Main;

import com.dcqout.DataGens.DcqRecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = minecrafd.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        System.out.print("dcqdatagen");
        event.createProvider(DcqRecipeProvider.DcqRunner::new);
    }
}