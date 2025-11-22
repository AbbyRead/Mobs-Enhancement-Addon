package btw.community.abbyread.meap;

import btw.AddonHandler;
import btw.BTWAddon;
import net.minecraft.server.MinecraftServer;
import btw.community.abbyread.meap.core.MEAEffectManager;

public class MobsEnhancementAddon extends BTWAddon {
    public MobsEnhancementAddon() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");

        if (!MinecraftServer.getIsServer()) {
            MEAEffectManager.initEffects();
        }
    }
}
