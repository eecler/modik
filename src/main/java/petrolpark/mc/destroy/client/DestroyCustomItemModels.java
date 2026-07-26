package petrolpark.mc.destroy.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import com.tterrag.registrate.util.entry.ItemEntry;
import petrolpark.mc.destroy.core.chemistry.hazard.protection.GasMaskModel;
import petrolpark.mc.destroy.core.chemistry.hazard.protection.GoldLaboratoryGogglesModel;
import petrolpark.mc.destroy.core.chemistry.hazard.protection.LaboratoryGogglesModel;
import petrolpark.mc.destroy.core.chemistry.hazard.protection.PaperMaskModel;
import petrolpark.mc.destroy.DestroyItems;

/**
 * Wraps the baked models of Items that need to render differently in some display context - the
 * protective headwear, which uses a separate partial model when worn on the head.
 * <p>
 * PORT (1.21.1): on 1.20.1 these were attached with {@code CreateRegistrate.itemModel(...)} on the
 * item builder. Destroy's Registrate is the Library's, not a {@code CreateRegistrate}, so the
 * wrappers are applied straight to the baking result instead - the same thing Create's own handler
 * does, minus the builder sugar.
 * </p>
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class DestroyCustomItemModels {

    private static final Map<ItemEntry<? extends Item>, Function<BakedModel, BakedModel>> WRAPPERS = new HashMap<>();

    static {
        WRAPPERS.put(DestroyItems.LABORATORY_GOGGLES, LaboratoryGogglesModel::new);
        WRAPPERS.put(DestroyItems.GOLD_LABORATORY_GOGGLES, GoldLaboratoryGogglesModel::new);
        WRAPPERS.put(DestroyItems.PAPER_MASK, PaperMaskModel::new);
        WRAPPERS.put(DestroyItems.GAS_MASK, GasMaskModel::new);
    };

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        WRAPPERS.forEach((item, wrapper) -> {
            ResourceLocation id = item.getId();
            ModelResourceLocation location = ModelResourceLocation.inventory(id);
            BakedModel template = models.get(location);
            if (template == null) return;
            models.put(location, wrapper.apply(template));
        });
    };

};
