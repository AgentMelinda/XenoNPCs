package net.bullettrain.xenonpcs.aero.v2;

import net.minecraft.nbt.CompoundTag;

/** Per-host v2 extras persisted on the existing control-host block entities. */
public final class GuidanceV2State {
    public static final String TAG = "GuidanceV2";
    private GuidanceV2SurfaceMode surfaceMode = null;

    public GuidanceV2SurfaceMode surfaceMode() { return null; }

    public void setSurfaceMode(GuidanceV2SurfaceMode mode) { }

    public void save(CompoundTag tag) { }

    public void load(CompoundTag tag) { }
}
