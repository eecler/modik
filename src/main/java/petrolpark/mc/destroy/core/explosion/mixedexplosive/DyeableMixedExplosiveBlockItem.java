package petrolpark.mc.destroy.core.explosion.mixedexplosive;

import petrolpark.mc.destroy.legacy.LegacyRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public abstract class DyeableMixedExplosiveBlockItem extends BlockItem implements IMixedExplosiveItem {

    public DyeableMixedExplosiveBlockItem(Block block, Properties properties) {
        super(block, properties);
    };

    public int getColor(ItemStack stack) {
        return DyedItemColor.getOrDefault(stack, 0xFFFFFF);
    };

    public void setColor(ItemStack stack, int color) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
    };

    @Override
    public int getExplosiveInventorySize() {
        return 16;
    };

    public ItemStack fromStructureInfo(StructureBlockInfo info) {
        ItemStack stack = new ItemStack(this);
        setColor(stack, info.nbt().getInt("Color"));
        MixedExplosiveInventory inv = new MixedExplosiveInventory(getExplosiveInventorySize());
        LegacyRegistries.deserializeNBT(inv, info.nbt().getCompound("ExplosiveMix"));
        setExplosiveInventory(stack, inv);
        return stack;
    };

    public StructureBlockInfo toStructureInfo(BlockPos localPos, BlockState state, ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Color", getColor(stack));
        tag.put("ExplosiveMix", LegacyRegistries.serializeNBT(getExplosiveInventory(stack)));
        return new StructureBlockInfo(localPos, state, tag);
    };

};
