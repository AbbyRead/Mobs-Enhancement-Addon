package btw.community.abbyread.meap.extend;

import net.minecraft.src.EntitySlime;
import org.spongepowered.asm.mixin.Unique;

public interface EntitySlimeExtend {

    // initializedFromNBT
    boolean meap$getInitializedFromNBT();
    void meap$setInitializedFromNBT(boolean value);

    // isMagma
    boolean meap$getIsMagma();
    void meap$setIsMagma(boolean value);

    // isCore
    byte meap$getIsCore();
    @SuppressWarnings("unused")
    void meap$setIsCore(byte id);

    // isMerging
    @SuppressWarnings("unused") boolean meap$getIsMerging();
    @SuppressWarnings("unused") void meap$setIsMerging(boolean value);
}
