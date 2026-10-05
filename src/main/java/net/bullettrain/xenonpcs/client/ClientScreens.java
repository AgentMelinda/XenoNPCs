package net.bullettrain.xenonpcs.client;

import net.bullettrain.xenonpcs.aero.AeroStateSnapshot;
import net.bullettrain.xenonpcs.missile.BallisticFlightPlan;
import net.bullettrain.xenonpcs.network.packet.CombatFxPacket;
import net.bullettrain.xenonpcs.network.packet.PartySyncPacket;
import net.bullettrain.xenonpcs.network.packet.PartyPingPacket;
import net.minecraft.core.BlockPos;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;

/**
 * Common-safe hooks for opening client screens.
 * Dedicated servers must never load {@code net.minecraft.client.*}.
 */
public final class ClientScreens {
    public static java.util.function.Consumer<net.bullettrain.xenonpcs.network.maker.MakerNetwork.Response> receiveMakerRace = response -> {};
    public static java.util.function.BiConsumer<java.util.UUID, CompoundTag> receiveTaotto = (id, tag) -> {};
    public static Runnable openTargetTool = () -> {
    };

    public static Consumer<GuidanceOpenData> openGuidance = data -> {
    };

    public static Consumer<BallisticFlightPlan.Result> receiveFlightPlan = result -> {
    };

    /** Authoritative Aero controller state pushed by the server; the GUI renders only this. */
    public static Consumer<AeroStateSnapshot> receiveAeroState = state -> {
    };

    /**
     * Combat impact cue from the server; bound on the client to the combat FX manager.
     *
     * <p>Left as a no-op on a dedicated server, which is the whole point of this class — the
     * binding lives in the client-only branch of {@code XenoNpcsMod} so nothing here ever
     * reaches {@code net.minecraft.client}.
     */
    public static Consumer<CombatFxPacket> receiveCombatFx = fx -> {
    };

    /** Party roster pushed by the server; the HUD renders only this. */
    public static Consumer<PartySyncPacket> receiveParty = party -> {
    };

    public static Consumer<PartyPingPacket> receivePartyPing = ping -> {
    };

    public static Runnable openParty = () -> {
    };

    public static Consumer<XenoNpcOpenData> openXenoNpcEditor = data -> {
    };

    /** The scripter tool's air menu (Player / Forge / library scripts). */
    public static Runnable openScriptHub = () -> {
    };

    /** Nearby NPCs list from the server (NPC wand, right-click the air). */
    public static Consumer<java.util.List<net.bullettrain.xenonpcs.npc.NpcNearbyList.Entry>> openNpcNearby = entries -> {
    };

    /** An NPC's full server DMZ profile (NpcProfileRefreshPacket); the client stores it. */
    public static java.util.function.BiConsumer<Integer, CompoundTag> receiveNpcProfile = (entityId, profile) -> {
    };

    /** Script screen opened by the scripting tool; carries the NPC and its current binding. */
    public static Consumer<XenoNpcScriptOpenData> openXenoNpcScript = data -> {
    };

    public record XenoNpcOpenData(int entityId, CompoundTag data) {}

    public record XenoNpcScriptOpenData(int entityId, net.minecraft.nbt.CompoundTag container) {}

    private ClientScreens() {
    }

    public record GuidanceOpenData(
            BlockPos computerPos,
            int x, int y, int z,
            String status,
            int pairedThrusters,
            int speedLevel,
            int apexY,
            int cruiseY,
            int fleetChannel,
            int salvoIntervalTicks,
            double gravitySi,
            double dragCoefficient,
            BlockPos missileBase,
            BlockPos missileCenter,
            BlockPos missileNose,
            double guidanceStopDistance
    ) {
        public GuidanceOpenData(BlockPos computerPos, int x, int y, int z, String status) {
            this(computerPos, x, y, z, status, 0, 5, 0, 0, 0, 10, 9.80665, 0.00002,
                    computerPos.below(), computerPos, computerPos.above(), 0.0);
        }

        public GuidanceOpenData(BlockPos computerPos, int x, int y, int z, String status,
                                int pairedThrusters, int speedLevel) {
            this(computerPos, x, y, z, status, pairedThrusters, speedLevel, 0, 0, 0, 10, 9.80665, 0.00002,
                    computerPos.below(), computerPos, computerPos.above(), 0.0);
        }

        public GuidanceOpenData(BlockPos computerPos, int x, int y, int z, String status,
                                int pairedThrusters, int speedLevel, int apexY) {
            this(computerPos, x, y, z, status, pairedThrusters, speedLevel, apexY, 0, 0, 10, 9.80665, 0.00002,
                    computerPos.below(), computerPos, computerPos.above(), 0.0);
        }

        public GuidanceOpenData(BlockPos computerPos, int x, int y, int z, String status,
                                int pairedThrusters, int speedLevel, int apexY, int cruiseY) {
            this(computerPos, x, y, z, status, pairedThrusters, speedLevel, apexY, cruiseY, 0, 10, 9.80665, 0.00002,
                    computerPos.below(), computerPos, computerPos.above(), 0.0);
        }

        public GuidanceOpenData(BlockPos computerPos, int x, int y, int z, String status,
                                int pairedThrusters, int speedLevel, int apexY, int cruiseY,
                                int fleetChannel, int salvoIntervalTicks) {
            this(computerPos, x, y, z, status, pairedThrusters, speedLevel, apexY, cruiseY,
                    fleetChannel, salvoIntervalTicks, 9.80665, 0.00002,
                    computerPos.below(), computerPos, computerPos.above(), 0.0);
        }
    }
}
