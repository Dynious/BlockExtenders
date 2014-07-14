package com.dynious.refinedrelocation.api.relocator;

import com.dynious.refinedrelocation.helper.IOHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public abstract class RelocatorModuleBase implements IRelocatorModule
{
    @Override
    public void init(IItemRelocator relocator, int side)
    {
    }

    @Override
    public boolean onActivated(IItemRelocator relocator, EntityPlayer player, int side, ItemStack stack)
    {
        return false;
    }

    @Override
    public void onUpdate(IItemRelocator relocator, int side)
    {
    }

    @Override
    public ItemStack outputToSide(IItemRelocator relocator, int side, TileEntity inventory, ItemStack stack, boolean simulate)
    {
        return IOHelper.insert(inventory, stack, ForgeDirection.getOrientation(side).getOpposite(), simulate);
    }

    @Override
    public void onRedstonePowerChange(boolean isPowered)
    {
    }

    @Override
    public int strongRedstonePower(int side)
    {
        return 0;
    }

    @Override
    public boolean connectsToRedstone()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getGUI(IItemRelocator relocator, int side, EntityPlayer player)
    {
        return null;
    }

    @Override
    public Container getContainer(IItemRelocator relocator, int side, EntityPlayer player)
    {
        return null;
    }

    @Override
    public boolean passesFilter(IItemRelocator relocator, int side, ItemStack stack, boolean input, boolean simulate)
    {
        return true;
    }

    @Override
    public void readFromNBT(IItemRelocator relocator, int side, NBTTagCompound compound)
    {
    }

    @Override
    public void writeToNBT(IItemRelocator relocator, int side, NBTTagCompound compound)
    {
    }

    @Override
    public abstract List<ItemStack> getDrops(IItemRelocator relocator, int side);

    @Override
    @SideOnly(Side.CLIENT)
    public abstract IIcon getIcon(IItemRelocator relocator, int side);

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {

    }

    @Override
    public String getDisplayName()
    {
        return "";
    }
}
