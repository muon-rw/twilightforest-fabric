package twilightforest.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import twilightforest.TwilightForestMod;

import org.jetbrains.annotations.Nullable;

public class FluidTagGenerator extends FabricTagProvider.FluidTagProvider {

    public static final TagKey<Fluid> FIRE_JET_FUEL = TagKey.create(Registry.FLUID_REGISTRY, TwilightForestMod.prefix("fire_jet_fuel"));

    public FluidTagGenerator(FabricDataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void generateTags() {
        tag(FIRE_JET_FUEL).addTag(FluidTags.LAVA);
    }
}
