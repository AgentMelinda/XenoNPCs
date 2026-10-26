package net.bullettrain.xenonpcs.compat.npc;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class NpcPursuitCurveTest {
    @Test void lateralChoiceHoldsTwelveTicksAndBlendsAcrossFour() {
        NpcPursuitCurve curve = new NpcPursuitCurve();
        UUID target = UUID.randomUUID();
        assertEquals(0.25, curve.update(target, 0, 8, 1.2, false, 1), 1e-9);
        assertEquals(0.5, curve.update(target, 1, 8, 1.2, false, -1), 1e-9);
        for (int tick = 2; tick < 12; tick++) assertTrue(curve.update(target, tick, 8, 1.2, false, -1) > 0);
        assertEquals(0.5, curve.update(target, 12, 8, 1.2, false, -1), 1e-9);
        assertEquals(0, curve.update(target, 13, 8, 1.2, false, -1), 1e-9);
        assertEquals(-0.5, curve.update(target, 14, 8, 1.2, false, -1), 1e-9);
        assertEquals(-1, curve.update(target, 15, 8, 1.2, false, -1), 1e-9);
    }
    @Test void reachingTargetHoldsUntilOutsideHysteresisBand() {
        NpcPursuitCurve curve = new NpcPursuitCurve();
        UUID target = UUID.randomUUID();
        curve.update(target, 0, 1, 1.2, true, 1);
        assertFalse(curve.chasing());
        curve.update(target, 1, 1.3, 1.2, false, 1);
        assertFalse(curve.chasing());
        curve.update(target, 2, 1.7, 1.2, false, 1);
        assertTrue(curve.chasing());
    }
    @Test void newTargetOrInterruptedClockRestartsBlend() {
        NpcPursuitCurve curve = new NpcPursuitCurve();
        UUID target = UUID.randomUUID();
        for (int tick=0;tick<6;tick++) curve.update(target,tick,8,1,false,1);
        assertEquals(-0.25,curve.update(UUID.randomUUID(),6,8,1,false,-1),1e-9);
        assertEquals(0.25,curve.update(target,40,8,1,false,1),1e-9);
    }

    /** The obstacle response: flipping is a decision, so the coin must not undo it next re-roll. */
    @Test void obstacleFlipHoldsItsSideAgainstAFriendlyCoin() {
        NpcPursuitCurve curve = new NpcPursuitCurve();
        UUID target = UUID.randomUUID();
        for (int tick = 0; tick < 6; tick++) curve.update(target, tick, 8, 1.2, false, 1);
        assertTrue(curve.update(target, 6, 8, 1.2, false, 1) > 0, "committed to one side");
        assertTrue(curve.switchSide(6) < 0, "the flip turns the arc the other way");
        for (int tick = 7; tick < 18; tick++)
            assertTrue(curve.update(target, tick, 8, 1.2, false, 1) < 0,
                    "the new side survives every re-issued choice, tick " + tick);
    }

    /**
     * The caller steers the waypoint from the value the flip returns, so that value has to be the
     * one the curve keeps reporting - otherwise the NPC jumps to the far side and back in two ticks.
     */
    @Test void flipAndTheFollowingUpdateAgree() {
        NpcPursuitCurve curve = new NpcPursuitCurve();
        UUID target = UUID.randomUUID();
        for (int tick = 0; tick < 20; tick++) curve.update(target, tick, 8, 1.2, false, 1);
        double flipped = curve.switchSide(20);
        assertEquals(flipped, curve.update(target, 21, 8, 1.2, false, -1), 1e-9);
        assertEquals(flipped, curve.update(target, 22, 8, 1.2, false, -1), 1e-9);
    }
}
