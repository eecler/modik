package petrolpark.mc.destroy.legacy;

import java.util.List;
import java.util.Optional;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 1.20.1-style recipe lookups: unwraps the {@link RecipeHolder} that 1.21.1's RecipeManager
 * returns, so call sites keep receiving the recipe itself. Argument order matches the 1.20.1
 * {@code level.getRecipeManager().getRecipeFor(type, input, level)} call, letting the rewrite
 * script drop the receiver and keep the arguments untouched.
 */
public class LegacyRecipes {

    public static <I extends RecipeInput, R extends Recipe<I>> Optional<R> getRecipeFor(RecipeType<R> type, I input, Level level) {
        return level.getRecipeManager().getRecipeFor(type, input, level).map(RecipeHolder::value);
    };

    public static <I extends RecipeInput, R extends Recipe<I>> List<R> getAllRecipesFor(RecipeType<R> type, Level level) {
        return level.getRecipeManager().getAllRecipesFor(type).stream().map(RecipeHolder::value).toList();
    };

};
