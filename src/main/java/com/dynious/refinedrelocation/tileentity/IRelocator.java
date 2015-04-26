package com.dynious.refinedrelocation.tileentity;

import cofh.api.transport.IItemDuct;
import com.dynious.refinedrelocation.api.relocator.IItemRelocator;
import com.dynious.refinedrelocation.grid.relocator.TravellingItem;
import com.dynious.refinedrelocation.lib.Mods;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import java.util.List;

@Optional.Interface(iface = "cofh.api.transport.IItemDuct", modid = Mods.COFH_TRANSPORT_API_ID)
public interface IRelocator extends IItemRelocator, IItemDuct
{
    public IRelocator[] getConnectedRelocators();

    public boolean canConnectOnSide(int side);

    public boolean passesFilter(ItemStack itemStack, int side, boolean input, boolean simulate);

    public void receiveTravellingItem(TravellingItem item);

    public List<TravellingItem> getItems(boolean includeItemsToAdd);

    public void onAdded(EntityLivingBase player, ItemStack stack);

    public byte getOreType();

    @SideOnly(Side.CLIENT)
    public GuiScreen getGUI(int side, EntityPlayer player);

    public Container getContainer(int side, EntityPlayer player);

    public byte getRenderType();

    public ItemStack getItemStackWithId(byte id);
}
