package petrolpark.mc.destroy;

import java.util.Optional;
import java.util.function.Supplier;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import petrolpark.mc.destroy.content.processing.sieve.SievingRecipe;
import petrolpark.mc.library.compat.create.core.data.recipe.AdvancedProcessingRecipe;

/**
 * PORT (1.21.1): only the recipe types reachable from ported content so far. The 1.20.1 enum
 * declares two dozen types (Distillation, Centrifugation, Electrolysis...) which come back with
 * their machines. Recipes are now codec-based: processing recipes take
 * {@code AdvancedProcessingRecipeParams} and are serialized by
 * {@link AdvancedProcessingRecipe.Serializer}.
 */
public enum DestroyRecipeTypes implements IRecipeTypeInfo {

    SIEVING(() -> new AdvancedProcessingRecipe.Serializer<>(SievingRecipe::new));

    private final ResourceLocation id;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    private final Supplier<RecipeType<?>> type;

    DestroyRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Destroy.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        type = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
    };

    DestroyRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier) {
        String name = Lang.asId(name());
        id = Destroy.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        type = typeSupplier;
    };

    public static void register(IEventBus modEventBus) {
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    };

    @Override
    public ResourceLocation getId() {
        return id;
    };

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    };

    @SuppressWarnings("unchecked")
    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    };

    public boolean is(RecipeHolder<?> holder) {
        return holder.value().getType() == this.getType();
    };

    public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I input, Level level) {
        return level.getRecipeManager().getRecipeFor(this.<I, R>getType(), input, level);
    };

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Destroy.MOD_ID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, Destroy.MOD_ID);
    };

};
