package net.bullettrain.xenonpcs.capability;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Optional;

/**
 * Persistent Xeno player state (1.20.1): a Forge capability where 1.21.1 uses a data attachment.
 * Same id ({@code xenonpcs:xeno_data}), same {@link #get} contract, copied to the new body on death.
 */
@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID)
public final class XenoCapabilities {
    public static final Capability<XenoDataProvider> XENO_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    private static final ResourceLocation ID = new ResourceLocation(XenoNpcsMod.MOD_ID, "xeno_data");

    private XenoCapabilities() {}

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(XenoCapabilities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(XenoDataProvider.class);
    }

    public static Optional<XenoPlayerData> get(Entity entity) {
        if (!(entity instanceof Player player)) return Optional.empty();
        return player.getCapability(XENO_DATA).resolve().map(XenoDataProvider::data);
    }

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ID, new Holder());
        }
    }

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        get(event.getOriginal()).ifPresent(oldData ->
                get(event.getEntity()).ifPresent(newData -> newData.copyFrom(oldData)));
        event.getOriginal().invalidateCaps();
    }

    private static final class Holder implements ICapabilitySerializable<CompoundTag> {
        private final XenoDataProvider provider = new XenoDataProvider();
        private final LazyOptional<XenoDataProvider> optional = LazyOptional.of(() -> provider);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return XENO_DATA.orEmpty(cap, optional);
        }

        @Override public CompoundTag serializeNBT() { return provider.serializeNBT(); }
        @Override public void deserializeNBT(CompoundTag nbt) { provider.deserializeNBT(nbt); }
    }
}
