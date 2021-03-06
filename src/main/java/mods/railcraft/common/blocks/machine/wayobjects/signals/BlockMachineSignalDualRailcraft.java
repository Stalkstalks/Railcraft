/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.wayobjects.signals;

import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.state.BlockStateContainer;

/**
 * Created by CovertJaguar on 7/5/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = SignalDualVariant.class)
public class BlockMachineSignalDualRailcraft extends BlockMachineSignal<SignalDualVariant> {

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty(), FRONT, CONNECTION_NORTH, CONNECTION_SOUTH, CONNECTION_EAST, CONNECTION_WEST);
    }

    @Override
    public void defineRecipes() {
        SignalDualVariant.BLOCK.ifAvailable(v -> CraftingPlugin.addRecipe(v.getStack(),
                "LCI",
                " BI",
                "LRI",
                'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.SIGNAL,
                'R', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                'I', "ingotIron",
                'L', RailcraftItems.SIGNAL_LAMP,
                'B', "dyeBlack"));

        SignalDualVariant.DISTANT.ifAvailable(v -> CraftingPlugin.addRecipe(v.getStack(),
                "LRI",
                " BI",
                "LRI",
                'R', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                'I', "ingotIron",
                'L', RailcraftItems.SIGNAL_LAMP,
                'B', "dyeBlack"));
    }
}
