package com.dynious.refinedrelocation.container;

import com.dynious.refinedrelocation.api.tileentity.IFilterTileGUI;
import com.dynious.refinedrelocation.container.slot.SlotHopper;
import com.dynious.refinedrelocation.lib.GuiNetworkIds;
import com.dynious.refinedrelocation.network.NetworkHandler;
import com.dynious.refinedrelocation.network.packet.MessageSetFilterOption;
import com.dynious.refinedrelocation.network.packet.MessageUserFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public class ContainerFilteringHopper extends ContainerHopper implements IContainerFiltered, IContainerNetworked
{
    protected final ISidedInventory inventory;
    protected IFilterTileGUI tile;

    private String lastUserFilter = "";
    private boolean lastBlacklist = true;
    private boolean lastFilterOptions[];
    private boolean initialUpdate = true;

    @SuppressWarnings("unchecked")
    public ContainerFilteringHopper(InventoryPlayer par1InventoryPlayer, IFilterTileGUI filterTile)
    {
        super(par1InventoryPlayer, (IInventory) filterTile);
        this.tile = filterTile;
        this.inventory = (ISidedInventory) filterTile;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            Slot oldSlot = (Slot) this.inventorySlots.get(i);
            SlotHopper newSlot = new SlotHopper(inventory, oldSlot.getSlotIndex(), oldSlot.xDisplayPosition, oldSlot.yDisplayPosition);
            newSlot.slotNumber = oldSlot.slotNumber;
            this.inventorySlots.set(i, newSlot);
        }

        lastFilterOptions = new boolean[tile.getFilter().getSize()];
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < this.inventory.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.inventory.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!tile.getFilter().passesFilter(itemstack1) || !this.mergeItemStack(itemstack1, 0, this.inventory.getSizeInventory(), false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    // Need to get any filter changes while in the Hopper Gui so that the clientside slots can filter properly
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (!tile.getFilter().getUserFilter().equals(lastUserFilter) || initialUpdate)
        {
            for (Object crafter : crafters)
            {
                if (crafter instanceof EntityPlayerMP)
                {
                    NetworkHandler.INSTANCE.sendTo(new MessageUserFilter(tile.getFilter().getUserFilter()), (EntityPlayerMP) crafter);
                }
            }
            lastUserFilter = tile.getFilter().getUserFilter();
        }

        for (int i = 0; i < tile.getFilter().getSize(); i++)
        {
            if (tile.getFilter().getValue(i) != lastFilterOptions[i] || initialUpdate)
            {
                for (Object crafter : crafters)
                {
                    NetworkHandler.INSTANCE.sendTo(new MessageSetFilterOption(i, tile.getFilter().getValue(i)), (EntityPlayerMP) crafter);
                }
                lastFilterOptions[i] = tile.getFilter().getValue(i);
            }
        }

        if (tile.getFilter().isBlacklisting() != lastBlacklist || initialUpdate)
        {
            for (Object crafter : crafters)
            {
                ((ICrafting) crafter).sendProgressBarUpdate(this, GuiNetworkIds.FILTERED_BASE + 2, tile.getFilter().isBlacklisting() ? 1 : 0);
            }
            lastBlacklist = tile.getFilter().isBlacklisting();
        }

        if (initialUpdate)
            initialUpdate = false;
    }

    @Override
    public void updateProgressBar(int id, int value)
    {
        id -= GuiNetworkIds.FILTERED_BASE;

        if (id > GuiNetworkIds.FILTERED_MAX || id < GuiNetworkIds.FILTERED_BASE)
            return;

        switch (id)
        {
            case 0:
                setFilterOption(value, true);
                break;
            case 1:
                setFilterOption(value, false);
                break;
            case 2:
                setBlackList(value != 0);
                break;
        }
    }

    @Override
    public void setUserFilter(String filter)
    {
        lastUserFilter = filter;
        tile.getFilter().setUserFilter(filter);
    }

    @Override
    public void setBlackList(boolean value)
    {
        lastBlacklist = value;
        tile.getFilter().setBlacklists(value);
    }

    @Override
    public void setFilterOption(int filterIndex, boolean value)
    {
        lastFilterOptions[filterIndex] = value;
        tile.getFilter().setValue(filterIndex, value);
    }

    @Override
    public void toggleFilterOption(int filterIndex)
    {
        this.setFilterOption(filterIndex, !tile.getFilter().getValue(filterIndex));
    }

    @Override
    public void setPriority(int priority)
    {
        //NOOP
    }

    @Override
    public void onMessage(int messageId, Object value, EntityPlayer player) {
        switch(messageId) {
            case ContainerFiltered.MESSAGE_BLACKLIST: setBlackList((Boolean) value); break;
        }
    }
}
