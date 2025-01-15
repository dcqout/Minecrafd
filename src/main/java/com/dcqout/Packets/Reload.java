package com.dcqout.Packets;

import com.dcqout.Main.IPlayer;
import com.dcqout.Main.minecrafd;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record Reload(int packetType) implements CustomPacketPayload {

        public static final Type<Reload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(minecrafd.MODID, "client"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Reload> CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, Reload::packetType,
                Reload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return Reload.TYPE;
        }
    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final CustomPacketPayload.Type data, final IPayloadContext context) {
            // Do something with the data, on the main thread
            ((IPlayer) context.player()).resetAttackTicker();
        }
    }
        @OnlyIn(Dist.CLIENT)
        public static void onCombo(Reload data, IPayloadContext context) {
            ((IPlayer) context.player()).resetAttackTicker();
        }
    }
