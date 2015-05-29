package com.dynious.refinedrelocation.client.gui;

import com.dynious.refinedrelocation.api.tileentity.IFilterTileGUI;
import com.dynious.refinedrelocation.api.tileentity.ISortingInventory;
import com.dynious.refinedrelocation.client.gui.widget.*;
import com.dynious.refinedrelocation.container.ContainerFiltered;
import com.dynious.refinedrelocation.grid.filter.*;
import com.dynious.refinedrelocation.helper.BlockHelper;
import com.dynious.refinedrelocation.lib.Resources;
import com.dynious.refinedrelocation.network.packet.gui.MessageGUI;
import com.dynious.refinedrelocation.tileentity.IAdvancedFilteredTile;
import com.dynious.refinedrelocation.tileentity.IAdvancedTile;
import com.dynious.refinedrelocation.tileentity.TileBlockExtender;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiFiltered extends GuiRefinedRelocationContainer {

    private IFilterTileGUI filterTile;
    private GuiTabPanel panel;
    private final List<GuiTabButton> tabButtons = new ArrayList<GuiTabButton>();
    private int lastFilterCount;
    private boolean initialUpdate = true;

    public GuiFiltered(IFilterTileGUI filterTile) {
        super(new ContainerFiltered(filterTile));
        this.filterTile = filterTile;
        lastFilterCount = filterTile.getFilter().getFilterCount();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);

        new GuiLabel(this, width / 2, height / 2 - 76, BlockHelper.getTileEntityDisplayName(filterTile.getTileEntity()));

        rebuildTabPanel(false);

        int curX = width / 2 - 75;
        int curY = height / 2 - 67;

        if(filterTile instanceof IAdvancedTile)
        {
            new GuiButtonMaxStackSize(this, curX, height / 2 - 45, (IAdvancedTile) filterTile, MessageGUI.MAX_STACK_SIZE);
            curX += 27;
            new GuiButtonSpread(this, curX, height / 2 - 45, (IAdvancedTile) filterTile, MessageGUI.SPREAD_ITEMS);
            curX += 27;
            new GuiButtonFilterExtraction(this, curX, height / 2 - 45, (IAdvancedFilteredTile) filterTile, MessageGUI.RESTRICT_EXTRACTION);

            new GuiInsertDirections(this, width / 2 + 30, height / 2 - 70, 50, 50, (IAdvancedTile) filterTile);
        }
        else
        {
            curY = height / 2 - 60;
        }
        curX = width / 2 - 75;

        new GuiButtonBlacklist(this, curX, curY, filterTile, MessageGUI.BLACKLIST);
        curX += 27;

        if (filterTile instanceof TileBlockExtender)
        {
            new GuiButtonRedstoneSignalStatus(this, curX, curY, (TileBlockExtender) filterTile, MessageGUI.REDSTONE_ENABLED);
            curX += 27;
        }

        if (filterTile instanceof ISortingInventory)
        {
            new GuiButtonPriority(this, curX, curY, 24, 20, (ISortingInventory) filterTile, MessageGUI.PRIORITY);
        }
    }

    public void rebuildTabPanel(boolean focusLast) {
        if(panel != null) {
            removeChild(panel);
        }
        for(GuiTabButton tabButton : tabButtons) {
            removeChild(tabButton);
        }
        tabButtons.clear();

        panel = new GuiTabPanel(this, width / 2 - 80, height / 2 - 18, 160, 97);
        int pageX = width / 2 - 80;
        int pageY = height / 2 - 12;
        int tabButtonX = width / 2 - 118;
        int tabButtonY = pageY  - 6;
        if(filterTile.getFilter().getFilterCount() == 0) {
            GuiTabButton emptyTabButton = new GuiTabButton(this, panel, tabButtonX, tabButtonY, new GuiEmptyFilter(this, pageX, pageY, 160, 97, filterTile.getFilter()));
            panel.setActiveTabButton(emptyTabButton);
            tabButtons.add(emptyTabButton);
        } else {
            GuiTabButton firstTabButton = null;
            for(int i = 0; i < filterTile.getFilter().getFilterCount(); i++) {
                AbstractFilter filter = filterTile.getFilter().getFilterAtIndex(i);
                IGuiWidgetBase page = null;
                switch(filter.getTypeId()) {
                    case AbstractFilter.TYPE_CUSTOM: page = new GuiUserFilter(pageX, pageY, 160, 97, true, (CustomUserFilter) filter); break;
                    case AbstractFilter.TYPE_PRESET: page = new GuiFilterList(pageX, pageY, 160, 97, (IChecklistFilter) filter); break;
                    case AbstractFilter.TYPE_CREATIVETAB: page = new GuiFilterList(pageX, pageY, 160, 97, (IChecklistFilter) filter); break;
                }
                GuiTabButton tabButton = new GuiTabButton(this, panel, tabButtonX, tabButtonY, page);
                if(firstTabButton == null) {
                    firstTabButton = tabButton;
                }
                tabButtons.add(tabButton);
                tabButtonY += 25;
            }
            if(focusLast) {
                tabButtons.get(tabButtons.size() - 1).setActive(true);
            } else if(firstTabButton != null) {
                firstTabButton.setActive(true);
            }
            if(filterTile.getFilter().getFilterCount() < 4) {
                tabButtons.add(new GuiTabButton(this, panel, tabButtonX, tabButtonY, new GuiEmptyFilter(this, pageX, pageY, 160, 97, filterTile.getFilter())));
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
    public void updateScreen()
    {
        super.updateScreen();
        if(lastFilterCount != filterTile.getFilter().getFilterCount()) {
            rebuildTabPanel(!initialUpdate);
            initialUpdate = false;
            lastFilterCount = filterTile.getFilter().getFilterCount();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        int xSize = 176;
        int ySize = 174;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(Resources.GUI_MULTIFILTER);
        int xStart = (width - xSize) / 2;
        int yStart = (height - ySize) / 2;
        this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);

        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
