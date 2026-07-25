package petrolpark.mc.destroy.core.chemistry.vat;

import petrolpark.mc.library.recipe.ingredient.BlockIngredient;

public record VatMaterial(double maxPressure, double thermalConductivity, boolean transparent, BlockIngredient<?> blocks) {
    
};
