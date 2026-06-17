package com.anastas1s12.jjs.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides the CursedEnergy capability to player entities.
 * This is attached to players via the AttachCapabilitiesEvent.
 */
public class CursedEnergyProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final CursedEnergy cursedEnergy = new CursedEnergy();
    private final LazyOptional<ICursedEnergy> optional = LazyOptional.of(() -> cursedEnergy);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CursedEnergyCapability.CURSED_ENERGY_CAPABILITY.orEmpty(cap, optional);
    }

    public void invalidate() {
        optional.invalidate();
    }

    @Override
    public CompoundTag serializeNBT() {
        return cursedEnergy.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        cursedEnergy.deserializeNBT(nbt);
    }
}
