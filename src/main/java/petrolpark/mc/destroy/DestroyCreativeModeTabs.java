package petrolpark.mc.destroy;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * PORT (1.21.1): a plain vanilla tab holding what is ported, so the items can actually be picked
 * up in game.
 * <p>
 * The 1.20.1 tab is built on the Library's {@code CustomTab}, with hand-laid-out rows and section
 * headers referencing most of Destroy's content (Vats, goggles, the Pumpjack...). That layout
 * comes back once the content it points at exists; recreating it now would be a list of holes.
 * </p>
 */
public class DestroyCreativeModeTabs {

    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Destroy.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("base",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.destroy.base"))
            .icon(() -> new ItemStack(DestroyItems.POLYETHENE.get()))
            .displayItems((params, output) -> {
                // Polymers
                output.accept(DestroyItems.ABS.get());
                output.accept(DestroyItems.NYLON.get());
                output.accept(DestroyItems.POLYACRYLONITRILE.get());
                output.accept(DestroyItems.POLYETHENE.get());
                output.accept(DestroyItems.POLYISOPRENE.get());
                output.accept(DestroyItems.POLYMETHYL_METHACRYLATE.get());
                output.accept(DestroyItems.POLYPROPENE.get());
                output.accept(DestroyItems.POLYSTYRENE.get());
                output.accept(DestroyItems.POLYSTYRENE_BUTADIENE.get());
                output.accept(DestroyItems.POLYTETRAFLUOROETHENE.get());
                output.accept(DestroyItems.POLYURETHANE.get());
                output.accept(DestroyItems.POLYVINYL_CHLORIDE.get());

                // Minerals and powders
                output.accept(DestroyItems.BORAX.get());
                output.accept(DestroyItems.CALCIUM_CARBIDE.get());
                output.accept(DestroyItems.CHALK_DUST.get());
                output.accept(DestroyItems.CRUSHED_RAW_CHROMIUM.get());
                output.accept(DestroyItems.FLUORITE.get());
                output.accept(DestroyItems.NETHER_CROCOITE.get());
                output.accept(DestroyItems.SILICA.get());
                output.accept(DestroyItems.ZEOLITE.get());

                // Explosives
                output.accept(DestroyItems.ACETONE_PEROXIDE.get());
                output.accept(DestroyItems.FULMINATED_MERCURY.get());
                output.accept(DestroyItems.NICKEL_HYDRAZINE_NITRATE.get());
                output.accept(DestroyItems.NITROCELLULOSE.get());
                output.accept(DestroyItems.TOUCH_POWDER.get());
                output.accept(DestroyBlocks.CORDITE_BLOCK.get());
                output.accept(DestroyBlocks.MECHANICAL_SIEVE.get());

                // Sodium
                output.accept(DestroyItems.SODIUM_INGOT.get());
                output.accept(DestroyItems.OXIDIZED_SODIUM_INGOT.get());
                output.accept(DestroyItems.SODIUM_HYDRIDE.get());

                // Other products
                output.accept(DestroyItems.BABY_BLUE_CRYSTAL.get());
                output.accept(DestroyItems.MESH.get());
                output.accept(DestroyItems.CREATINE.get());
                output.accept(DestroyItems.IODINE.get());
            })
            .build()
    );

    public static final void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    };
};
