package baguchan.hunterillager.huntertype;

import baguchan.hunterillager.init.HunterTypes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class HunterTypeUtils {
    public static HunterType getHunterTypeFromNBT(@Nullable CompoundNBT tag) {
        return tag == null ? null : HunterTypes.getRegistry().getValue(ResourceLocation.tryCreate(tag.getString("HunterType")));
    }

    public static HunterType getHunterFromString(@Nullable String id) {
        return id == null ? null : HunterTypes.getRegistry().getValue(ResourceLocation.tryCreate(id));
    }

    public static CompoundNBT setHunterType(CompoundNBT nbt, HunterType type) {
        ResourceLocation resourcelocation = HunterTypes.getRegistry().getKey(type);
        if (resourcelocation != null) {
            nbt.putString("HunterType", resourcelocation.toString());
        }

        return nbt;
    }
}
