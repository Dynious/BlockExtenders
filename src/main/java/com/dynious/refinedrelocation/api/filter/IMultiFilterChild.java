package com.dynious.refinedrelocation.api.filter;

import com.dynious.refinedrelocation.api.gui.IGuiWidgetWrapped;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public interface IMultiFilterChild
{

	String getTypeName();
	void setParentFilter(IMultiFilter parentFilter, int filterIndex);
	IMultiFilter getParentFilter();
	int getFilterIndex();
    boolean canFilterBeUsedOnTile(TileEntity tile);
	void setFilterString(int optionId, String value);
	void setFilterBoolean(int optionId, boolean value);
	void setFilterBooleanArray(int optionId, boolean[] values);
	void passesFilter(ItemStack itemStack, FilterResult outResult);
	void writeToNBT(NBTTagCompound compound);
	void readFromNBT(NBTTagCompound compound);
	void markDirty(boolean isDirty);
	boolean isDirty();
	void sendUpdate(EntityPlayerMP playerMP);

	@SideOnly(Side.CLIENT)
	IGuiWidgetWrapped getGuiWidget(int x, int y, int width, int height);

	@SideOnly(Side.CLIENT)
	ResourceLocation getIconSheet();

	@SideOnly(Side.CLIENT)
	int getIconX();

	@SideOnly(Side.CLIENT)
	int getIconY();

	@SideOnly(Side.CLIENT)
	String getNameLangKey();

	@SideOnly(Side.CLIENT)
	String getDescriptionLangKey();
}
