package com.dynious.refinedrelocation.item;

import com.dynious.refinedrelocation.lib.Strings;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemRelocationController extends ItemBlock
{
    public ItemRelocationController(Block block)
    {
        super(block);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean b)
    {
        list.add(StatCollector.translateToLocalFormatted(Strings.REL_CONT_DESC, 3, 4, 3));
    }
}
