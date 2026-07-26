package petrolpark.mc.destroy.core.chemistry.storage;

import petrolpark.mc.destroy.config.DestroyConfigs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BalloonItem extends Item implements IMixtureStorageItem {

    public BalloonItem(Properties properties) {
        super(properties);
    };

    @Override
    public int getCapacity(ItemStack stack) {
        return DestroyConfigs.server().blocks.balloonPoppingCapacity.get();
    };

    @Override
    public Component getNameRegardlessOfFluid(ItemStack stack) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNameRegardlessOfFluid'");
    };
    
};
