package petrolpark.mc.destroy;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import petrolpark.mc.destroy.config.DestroyConfigs;
import petrolpark.mc.destroy.core.registrate.DestroyRegistrate;
import petrolpark.mc.destroy.data.DestroyDatagen;
import petrolpark.mc.library.shared.GetPetrolparkSharedFeatures;
import petrolpark.mc.library.shared.SharedFeatureFlag;

@Mod(Destroy.MOD_ID)
public class Destroy {

    public static final String MOD_ID = "destroy";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DestroyRegistrate REGISTRATE = new DestroyRegistrate();

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    };

    public Destroy(IEventBus modEventBus, ModContainer modContainer) {

        REGISTRATE.registerEventListeners(modEventBus);

        // Config
        DestroyConfigs.register(ModLoadingContext.get(), modContainer);

        // Registration
        DestroyAdvancementTrigger.register(); // Must run before the registry events, it is what creates the triggers
        DestroyAttachmentTypes.register(modEventBus);
        DestroyAttributes.register(modEventBus);
        DestroyBlockEntityTypes.register();
        DestroyBlocks.register();
        DestroyCreativeModeTabs.register(modEventBus);
        DestroyDataComponents.register(modEventBus);
        DestroyFluids.register();
        DestroyItems.register();
        DestroyMobEffects.register(modEventBus);
        DestroyNumberProviderTypes.register();
        DestroyPackets.register();
        DestroyPollutionTypes.register();
        DestroyRecipeTypes.register(modEventBus);
        DestroyRegistries.init();
        DestroyStats.register(modEventBus);
    
        // Events
        modEventBus.addListener(this::init);
        modEventBus.addListener(EventPriority.HIGHEST, DestroyDatagen::gatherDataHighPriority);
        modEventBus.addListener(EventPriority.LOWEST, DestroyDatagen::gatherData);

        // Compat
        // if (Mods.JEI.isLoading()) NeoForge.EVENT_BUS.register(ITickableCategory.ClientEvents.class);
        // Mods.CREATE.executeIfInstalled(() -> () -> Create.ctor(modEventBus, NeoForge.EVENT_BUS));
        // Mods.CURIOS.executeIfInstalled(() -> () -> Curios.ctor(modEventBus, NeoForge.EVENT_BUS));
    };

    // Library 1.5.0 requires this to be static, and fails mod construction otherwise.
    @GetPetrolparkSharedFeatures
    public static SharedFeatureFlag[] getEnabledSharedFeatureFlags() {
        return new SharedFeatureFlag[]{
            SharedFeatureFlag.BASIN_LID,
            SharedFeatureFlag.BLOOD,
            SharedFeatureFlag.EXTRUSION,
            SharedFeatureFlag.MILK_PRODUCTS,
            SharedFeatureFlag.SUNFLOWER_OIL
        };
    };

    private void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DestroyStats.register(); // Attaches the formatters, which needs the registry to be populated
        });
    };

    public static final <T> T runForDist(Supplier<Supplier<T>> clientSupplier, Supplier<Supplier<T>> serverSupplier) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return clientSupplier.get().get();
        } else {
            return serverSupplier.get().get();
        }
    };

    public static final <T> T unsafeCallClient(Supplier<Supplier<T>> supplier) {
        try {
            if (FMLEnvironment.dist == Dist.CLIENT) supplier.get().get();
        } catch (Exception e) {
            throw new RuntimeException();
        };
        return null;
    };

    public static final void unsafeRunClient(Supplier<Runnable> supplier) {
        try {
            if (FMLEnvironment.dist == Dist.CLIENT) supplier.get().run();
        } catch (Exception e) {
            throw new RuntimeException();
        };
    };

};

