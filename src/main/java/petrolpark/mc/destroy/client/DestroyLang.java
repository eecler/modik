package petrolpark.mc.destroy.client;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.lang.FontHelper.Palette;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.lang.LangNumberFormat;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import petrolpark.mc.destroy.Destroy;

/**
 * Localisation helpers for Destroy.
 * <p>
 * This holds the general-purpose helpers the chemistry engine needs. The methods which
 * describe content - Vat materials, Mixture Fluid Ingredients, Reaction rate constants -
 * come back as that content is ported to 1.21.1.
 * </p>
 */
public class DestroyLang {

    public static final Palette WHITE_AND_WHITE = Palette.ofColors(ChatFormatting.WHITE, ChatFormatting.WHITE);

    private static DecimalFormat df = new DecimalFormat();
    static {
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
    };

    private static String[] subscriptNumbers = new String[]{"₀", "₁", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉"};
    private static String[] superscriptNumbers = new String[]{"⁰", "¹", "²", "³", "⁴", "⁵", "⁶", "⁷", "⁸", "⁹"};

    public static String pascal(String string) {
        String s = Lang.asId(string);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    };

    public static LangBuilder builder() {
        return new LangBuilder(Destroy.MOD_ID);
    };

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    };

    public static MutableComponent translateDirect(String langKey, Object... args) {
        Object[] args1 = LangBuilder.resolveBuilders(args);
        return Component.translatable(Destroy.MOD_ID + "." + langKey, args1);
    };

    public static List<Component> translatedOptions(String prefix, String... keys) {
        List<Component> result = new ArrayList<>(keys.length);
        for (String key : keys)
            result.add(translate((prefix != null ? prefix + "." : "") + key).component());
        return result;
    };

    public static LangBuilder number(double d) {
        return builder().text(LangNumberFormat.format(d));
    };

    public static LangBuilder fluidName(FluidStack stack) {
        return builder().add(stack.getHoverName().copy());
    };

    public static LangBuilder direction(Direction direction) {
        return translate("generic.direction." + Lang.asId(direction.name()));
    };

    /**
     * Converts a number to Unicode subscript.
     */
    public static String toSubscript(int value) {
        String string = "";
        for (char c : String.valueOf(value).toCharArray()) {
            if (c == '-') string += "₋";
            string += subscriptNumbers[Integer.valueOf(String.valueOf(c))];
        };
        return string;
    };

    /**
     * Converts a number to Unicode superscript.
     * @param value Should only be passed strings containing numbers, {@code +} and {@code -}.
     */
    public static String toSuperscript(String value) {
        String string = "";
        for (char c : value.toCharArray()) {
            if (c == '-') string += "⁻";
            if (c == '+') string += "⁺";
            try {
                string += superscriptNumbers[Integer.valueOf(String.valueOf(c))];
            } catch (Throwable e) {};
        };
        return string;
    };

    public static enum TemperatureUnit {

        KELVINS(t -> t, "K"),
        DEGREES_CELCIUS(t -> t - 273f, "°C"),
        DEGREES_FARENHEIT(t -> (t - 273f) * 9/5 + 32, "°F");

        private static final DecimalFormat df = new DecimalFormat();
        static {
            df.setMinimumFractionDigits(1);
            df.setMaximumFractionDigits(1);
        };

        private UnaryOperator<Float> conversionFromKelvins;
        private String symbol;

        TemperatureUnit(UnaryOperator<Float> conversionFromKelvins, String symbol) {
            this.conversionFromKelvins = conversionFromKelvins;
            this.symbol = symbol;
        };

        public String of(float temperature) {
            return df.format(conversionFromKelvins.apply(temperature)) + symbol;
        };

        public String of(float temperature, DecimalFormat df) {
            return df.format(conversionFromKelvins.apply(temperature)) + symbol;
        };
    };

    public static LangBuilder quantity(float quantity, boolean useMoles, DecimalFormat concentrationFormatter) {
        String translationKey = useMoles ? "tooltip.mixture_contents.moles" : "tooltip.mixture_contents.concentration";
        double smallestVisibleQuantity = Math.pow(10, -concentrationFormatter.getMaximumFractionDigits());
        if (quantity != 0f) {
            if (Math.abs(quantity) >= 1000f) {
                quantity /= 1000f;
                translationKey += ".kilo";
            } else if (Math.abs(quantity) <= smallestVisibleQuantity / 1000f) {
                quantity *= 1000000f;
                translationKey += ".micro";
            } else if (Math.abs(quantity) <= smallestVisibleQuantity) {
                quantity *= 1000f;
                translationKey += ".milli";
            };
        };
        return translate(translationKey, concentrationFormatter.format(quantity));
    };


    public static void tankInfoTooltip(List<Component> tooltip, LangBuilder tankName, FluidTank tank) {
        tankInfoTooltip(tooltip, tankName, tank.getFluid(), tank.getCapacity());
    };

    public static void tankInfoTooltip(List<Component> tooltip, LangBuilder tankName, FluidStack contents, int capacity) {
        LangBuilder mb = CreateLang.builder().translate("generic.unit.millibuckets");

        tankName
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip, 0);

        if (contents.isEmpty()) {
            CreateLang.builder().translate("gui.goggles.fluid_container.capacity")
                .add(DestroyLang.number(capacity)
                    .add(mb)
                    .style(ChatFormatting.GOLD))
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);
        } else {
            DestroyLang.fluidName(contents)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

            DestroyLang.builder()
                .add(DestroyLang.number(contents.getAmount())
                    .add(mb)
                    .style(ChatFormatting.GOLD))
                .text(ChatFormatting.GRAY, " / ")
                .add(DestroyLang.number(capacity)
                    .add(mb)
                    .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
        };
    };

    public static MutableComponent tickOrCross(boolean tick) {
        return tick ? tick() : cross();
    };

    public static MutableComponent tick() {
        return Component.literal("\u2714").withStyle(ChatFormatting.GREEN).copy();
    };

    public static MutableComponent cross() {
        return Component.literal("\u2718").withStyle(ChatFormatting.RED).copy();
    };

};
