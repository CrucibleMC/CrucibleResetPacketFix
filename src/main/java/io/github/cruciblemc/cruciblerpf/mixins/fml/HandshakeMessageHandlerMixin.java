package io.github.cruciblemc.cruciblerpf.mixins.fml;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.handshake.FMLHandshakeMessage;
import cpw.mods.fml.common.network.handshake.HandshakeMessageHandler;
import cpw.mods.fml.common.network.handshake.IHandshakeState;
import cpw.mods.fml.common.registry.GameData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HandshakeMessageHandler.class, remap = false)
public class HandshakeMessageHandlerMixin<S extends Enum<S> & IHandshakeState<S>> {

    @Shadow
    private AttributeKey<S> fmlHandshakeState;

    @Shadow
    private Class<S> stateType;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lcpw/mods/fml/common/network/handshake/FMLHandshakeMessage;)V", at = @At("HEAD"), cancellable = true)
    public void channelRead0(ChannelHandlerContext context, FMLHandshakeMessage fmlHandshakeMessage, CallbackInfo callbackInfo){
        if(fmlHandshakeMessage instanceof FMLHandshakeMessage.HandshakeReset){
            FMLLog.info("Detected handshake reset packet, enforcing HELLO state...");

            GameData.revertToFrozen();
            S state = Enum.valueOf(stateType, "HELLO");
            context.attr(fmlHandshakeState).set(state);

            FMLLog.info("New handshake state: " + state.name());
            callbackInfo.cancel();
        }
    }

}
