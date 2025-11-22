package btw.community.abbyread.meap.extend;

public interface EntitySlimeExtend {

    // initializedFromNBT
    boolean meap$getInitializedFromNBT();
    void meap$setInitializedFromNBT(boolean value);

    // isMagma
    boolean meap$getIsMagma();
    void meap$setIsMagma(boolean value);

    // isCore
    byte meap$getIsCore();
    void meap$setIsCore(byte id);

    // isMerging
    boolean meap$getIsMerging();
    void meap$setIsMerging(boolean value);
}
