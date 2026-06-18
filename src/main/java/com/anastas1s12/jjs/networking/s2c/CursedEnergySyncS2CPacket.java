package com.anastas1s12.jjs.networking.s2c;

import com.anastas1s12.jjs.capability.ICursedEnergy;
import com.anastas1s12.jjs.client.ClientCEData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server -> Client packet that syncs all Cursed Energy data to the player.
 * Received on the client to update the local HUD and cached values.
 */
public class CursedEnergySyncS2CPacket {

    private final float currentCE;
    private final float maxCE;
    private final float baseMaxCE;
    private final float regenRate;
    private final float efficiency;
    private final float output;
    private final int masteryLevel;
    private final int masteryPoints;
    private final int masteryXP;
    private final int fingersConsumed;
    private final boolean rctActive;
    private final boolean sixEyes;

    public CursedEnergySyncS2CPacket(ICursedEnergy ce) {
        this.currentCE = ce.getCurrentCE();
        this.maxCE = ce.getMaxCE();
        this.baseMaxCE = ce.getBaseMaxCE();
        this.regenRate = ce.getRegenRate();
        this.efficiency = ce.getEfficiency();
        this.output = ce.getOutput();
        this.masteryLevel = ce.getMasteryLevel();
        this.masteryPoints = ce.getMasteryPoints();
        this.masteryXP = ce.getMasteryXP();
        this.fingersConsumed = ce.getFingersConsumed();
        this.rctActive = ce.isRCTActive();
        this.sixEyes = ce.hasSixEyes();
    }

    public CursedEnergySyncS2CPacket(FriendlyByteBuf buf) {
        this.currentCE = buf.readFloat();
        this.maxCE = buf.readFloat();
        this.baseMaxCE = buf.readFloat();
        this.regenRate = buf.readFloat();
        this.efficiency = buf.readFloat();
        this.output = buf.readFloat();
        this.masteryLevel = buf.readInt();
        this.masteryPoints = buf.readInt();
        this.masteryXP = buf.readInt();
        this.fingersConsumed = buf.readInt();
        this.rctActive = buf.readBoolean();
        this.sixEyes = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(currentCE);
        buf.writeFloat(maxCE);
        buf.writeFloat(baseMaxCE);
        buf.writeFloat(regenRate);
        buf.writeFloat(efficiency);
        buf.writeFloat(output);
        buf.writeInt(masteryLevel);
        buf.writeInt(masteryPoints);
        buf.writeInt(masteryXP);
        buf.writeInt(fingersConsumed);
        buf.writeBoolean(rctActive);
        buf.writeBoolean(sixEyes);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // This runs on the client thread
            ClientCEData.set(currentCE, maxCE, baseMaxCE, regenRate, efficiency, output,
                    masteryLevel, masteryPoints, masteryXP, fingersConsumed, rctActive, sixEyes);
        });
        return true;
    }
}
