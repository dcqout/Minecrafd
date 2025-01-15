package com.dcqout.Main;

import com.dcqout.Packets.Reload;
import net.dcqmod.*;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(minecrafd.MODID)
public class minecrafd
{
    public static final String MODID = "minecrafd";

    public minecrafd(IEventBus EventBus, ModContainer modc)
    {
        registrator.setup(EventBus);

        EventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modc.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        if (Config.logDirtBlock)
            refer.LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        refer.LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
        Config.items.forEach((item) -> refer.LOGGER.info("ITEM >> {}", item.toString()));
    }
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public static class ModEvents {
        @SubscribeEvent public static void onPayloadRegister(RegisterPayloadHandlersEvent event)
        {   var registrar = event.registrar(minecrafd.MODID); registrar.playToClient(Reload.TYPE, Reload.CODEC, (m, c) -> Reload.onCombo(m, c));  }
        @SubscribeEvent public static void modifyComponents(ModifyDefaultComponentsEvent event)
        {   event.modifyMatching( item -> item.components().has(DataComponents.DAMAGE), builder -> builder.set(registrator.DataComponents.ECHO.get(),0));   }
    }

    @SubscribeEvent public void onServerStarting(ServerStartingEvent event) {}
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT) public static class ClientModEvents {
        @SubscribeEvent public static void onClientSetup(FMLClientSetupEvent event)
        {refer.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());}
    }
}
