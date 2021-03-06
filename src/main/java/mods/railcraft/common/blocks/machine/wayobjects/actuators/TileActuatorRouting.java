/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.actuators;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.items.ItemRoutingTable;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.routing.IRouter;
import mods.railcraft.common.util.routing.ITileRouting;
import mods.railcraft.common.util.routing.RoutingLogic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

public class TileActuatorRouting extends TileActuatorSecured implements IRouter, ITileRouting {

    private final StandaloneInventory inv = new StandaloneInventory(1, this);
    private final MultiButtonController<RoutingButtonState> routingController = MultiButtonController.create(0, RoutingButtonState.values());
    private RoutingLogic logic;

    @Override
    public MultiButtonController<RoutingButtonState> getRoutingController() {
        return routingController;
    }

    @Nonnull
    @Override
    public IEnumMachine<?> getMachineType() {
        return ActuatorVariant.ROUTING;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            ItemStack table = inv.getStackInSlot(0);
            if (!InvTools.isEmpty(table)) {
                Railcraft.getProxy().openRoutingTableGui(player, this, table);
                return true;
            }
            return false;
        }
        if (Game.isHost(worldObj)) {
            ItemStack current = player.inventory.getCurrentItem();
            if (!InvTools.isEmpty(current) && current.getItem() instanceof ItemRoutingTable)
                if (inv.getStackInSlot(0) == null) {
                    ItemStack copy = current.copy();
                    copy.stackSize = 1;
                    inv.setInventorySlotContents(0, copy);
                    if (!player.capabilities.isCreativeMode) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
                        player.inventory.markDirty();
                    }
                    if (Game.isHost(theWorld())) {
                        if (isLogicValid())
                            return true;
                    } else
                        return true;
                }
        }
        return super.blockActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.ROUTING, player, worldObj, getPos());
        return true;
    }

    @Nullable
    @Override
    public ItemStack getRoutingTable() {
        return inv.getStackInSlot(0);
    }

    @Override
    public void setRoutingTable(ItemStack stack) {
        inv.setInventorySlotContents(0, stack);
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        InvTools.dropInventory(inv, worldObj, getPos());
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block neighborBlock) {
        super.onNeighborBlockChange(state, neighborBlock);
        boolean power = isBeingPoweredByRedstone();
        if (isPowered() != power)
            setPowered(power);
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLivingBase, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLivingBase, stack);
        boolean power = isBeingPoweredByRedstone();
        if (isPowered() != power)
            setPowered(power);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        resetLogic();
        sendUpdateToClient();
    }

    @Override
    public Optional<RoutingLogic> getLogic() {
        refreshLogic();
        return Optional.ofNullable(logic);
    }

    @Override
    public void resetLogic() {
        logic = null;
    }

    private void refreshLogic() {
        if (logic == null && inv.getStackInSlot(0) != null)
            logic = ItemRoutingTable.getLogic(inv.getStackInSlot(0));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("inv", data);
        routingController.writeToNBT(data, "railwayType");
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inv.readFromNBT("inv", data);
        routingController.readFromNBT(data, "railwayType");
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeItemStack(inv.getStackInSlot(0));
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        inv.setInventorySlotContents(0, data.readItemStack());
    }

    @Override
    public IInventory getInventory() {
        return inv;
    }

    @Override
    public boolean shouldSwitch(@Nullable EntityMinecart cart) {
        return getLogic().map(l -> cart != null && l.isValid() && l.matches(this, cart)).orElse(false);
    }
}
