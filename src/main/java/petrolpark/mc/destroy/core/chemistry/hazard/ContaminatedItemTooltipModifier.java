package petrolpark.mc.destroy.core.chemistry.hazard;

import petrolpark.mc.destroy.client.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.fluids.FluidStack;

public class ContaminatedItemTooltipModifier implements TooltipModifier {

    public static final Palette DARK_GRAY_AND_WHITE = Palette.ofColors(ChatFormatting.DARK_GRAY, ChatFormatting.WHITE);

    @Override
    public void modify(ItemTooltipEvent context) {
        FluidStack fluid = ChemistryHazardHelper.getContamination(context.getItemStack());
        if (fluid.isEmpty()) return;
        context.getToolTip().addAll(1, TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.contamination_description").component(), Screen.hasAltDown() ? DARK_GRAY_AND_WHITE: Palette.GRAY));
        if (Screen.hasAltDown()) {
            context.getToolTip().add(2, Component.literal(" "));
            context.getToolTip().add(3, Component.literal(" "));
            context.getToolTip().addAll(3, TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.contamination", fluid.getDisplayName()).component(), Palette.RED));
        };
    };
    
};
