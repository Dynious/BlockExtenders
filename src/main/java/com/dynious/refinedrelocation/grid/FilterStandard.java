package com.dynious.refinedrelocation.grid;

import com.dynious.refinedrelocation.api.filter.IFilterGUI;
import com.dynious.refinedrelocation.api.tileentity.IFilterTileGUI;
import com.dynious.refinedrelocation.helper.LogHelper;
import com.dynious.refinedrelocation.helper.StringHelper;
import com.dynious.refinedrelocation.lib.Strings;
import com.google.common.primitives.Booleans;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FilterStandard implements IFilterGUI
{
    public static final int FILTER_SIZE = 14;
    private static CreativeTabs[] syncedTabs;

    private IFilterTileGUI tile;
    private CreativeTabs[] creativeTabArray;
    private boolean[] creativeTabs;
    private boolean[] customFilters = new boolean[FILTER_SIZE];
    private String userFilter = "";

    private boolean blacklists = false;

    public FilterStandard(IFilterTileGUI tile)
    {
        this.tile = tile;
        creativeTabArray = syncedTabs != null ? syncedTabs : CreativeTabs.creativeTabArray;
        creativeTabs = new boolean[creativeTabArray.length];
    }

    public static boolean stringMatchesWildcardPattern(String string, String wildcardPattern)
    {
        if (wildcardPattern.startsWith("*") && wildcardPattern.length() > 1)
        {
            if (wildcardPattern.endsWith("*") && wildcardPattern.length() > 2)
            {
                if (string.contains(wildcardPattern.substring(1, wildcardPattern.length() - 1)))
                    return true;
            }
            else if (string.endsWith(wildcardPattern.substring(1)))
                return true;
        }
        else if (wildcardPattern.endsWith("*") && wildcardPattern.length() > 1)
        {
            if (string.startsWith(wildcardPattern.substring(0, wildcardPattern.length() - 1)))
                return true;
        }
        else
        {
            if (string.equalsIgnoreCase(wildcardPattern))
                return true;
        }
        return false;
    }

    public static String[] getOreNames(ItemStack itemStack)
    {
        int[] ids = OreDictionary.getOreIDs(itemStack);

        String[] oreNames = new String[ids.length];
        for (int i = 0; i < ids.length; i++)
        {
            oreNames[i] = OreDictionary.getOreName(ids[i]).toLowerCase().replaceAll("\\s+", "");
        }
        return oreNames;
    }

    public static void syncTabs(String[] tabLabels)
    {
        syncedTabs = new CreativeTabs[tabLabels.length];
        for (int i = 0; i < tabLabels.length; i++)
        {
            String label = tabLabels[i];
            if (label != null)
            {
                for (CreativeTabs tab : CreativeTabs.creativeTabArray)
                {
                    if (label.equalsIgnoreCase(tab.getTabLabel()))
                    {
                        syncedTabs[i] = tab;
                    }
                }
            }
            if (syncedTabs[i] == null)
                syncedTabs[i] = createNewFakeTab(label);
        }
    }

    public static String[] getLabels()
    {
        String[] labels = new String[CreativeTabs.creativeTabArray.length];
        CreativeTabs[] creativeTabArray = CreativeTabs.creativeTabArray;
        for (int i = 0; i < creativeTabArray.length; i++)
        {
            labels[i] = creativeTabArray[i].tabLabel;
        }
        return labels;
    }

    public static CreativeTabs createNewFakeTab(String tabName)
    {
        CreativeTabs oldTab = CreativeTabs.creativeTabArray[0];
        CreativeTabs tab = new CreativeTabs(0, tabName)
        {
            @Override
            public Item getTabIconItem()
            {
                return null;
            }
        };
        CreativeTabs.creativeTabArray[0] = oldTab;
        return tab;
    }

    public int getSize()
    {
        return creativeTabs.length - 2 + FILTER_SIZE;
    }

    public boolean passesFilter(ItemStack itemStack)
    {
        return isBlacklisting() ? !isInFilter(itemStack) : isInFilter(itemStack);
    }

    private boolean isInFilter(ItemStack itemStack)
    {
        if (itemStack != null)
        {
            String[] oreNames = null;

            if (getUserFilter() != null && !getUserFilter().isEmpty())
            {
                String filter = getUserFilter().toLowerCase().replaceAll("\\s+", "");
                String itemName = null;
                for (String s : filter.split(","))
                {
                    if (s.contains("!"))
                    {
                        if (oreNames == null)
                        {
                            oreNames = getOreNames(itemStack);
                        }
                        s = s.replace("!", "");
                        for (String oreName : oreNames)
                        {
                            if (stringMatchesWildcardPattern(oreName, s))
                                return true;
                        }
                    }
                    else
                    {
                        if (itemName == null)
                        {
                            try
                            {
                                itemName = itemStack.getDisplayName().toLowerCase().replaceAll("\\s+", "");
                            }
                            catch (Exception e)
                            {
                                LogHelper.error("Encountered an error when retrieving item name of: " + itemStack.toString());
                                break;
                            }
                        }
                        if (stringMatchesWildcardPattern(itemName, s))
                            return true;
                    }
                }
            }

            if (Booleans.contains(customFilters, true))
            {
                if (oreNames == null)
                {
                    oreNames = getOreNames(itemStack);
                }

                for (String oreName : oreNames)
                {
                    if (customFilters[0] && (oreName.contains("ingot") || itemStack.getItem() == Items.iron_ingot || itemStack.getItem() == Items.gold_ingot))
                        return true;
                    if (customFilters[1] && oreName.contains("ore"))
                        return true;
                    if (customFilters[2] && oreName.contains("log"))
                        return true;
                    if (customFilters[3] && oreName.contains("plank"))
                        return true;
                    if (customFilters[4] && oreName.contains("dust"))
                        return true;
                    if (customFilters[5] && oreName.contains("crushed") && !oreName.contains("purified"))
                        return true;
                    if (customFilters[6] && oreName.contains("purified"))
                        return true;
                    if (customFilters[7] && oreName.contains("plate"))
                        return true;
                    if (customFilters[8] && oreName.contains("gem"))
                        return true;
                    if (customFilters[10] && oreName.contains("dye"))
                        return true;
                    if (customFilters[11] && oreName.contains("nugget"))
                        return true;
                }
                if (customFilters[9] && itemStack.getItem() instanceof ItemFood)
                    return true;
                if (customFilters[12] && itemStack.getItem() instanceof ItemBlock && Block.getBlockFromItem(itemStack.getItem()) instanceof IPlantable)
                    return true;
                if (customFilters[13] && TileEntityFurnace.getItemBurnTime(itemStack) > 0)
                    return true;
            }

            if (Booleans.contains(creativeTabs, true))
            {
                CreativeTabs tab;
                if (itemStack.getItem() instanceof ItemBlock)
                {
                    tab = Block.getBlockById(ItemBlock.getIdFromItem(itemStack.getItem())).displayOnCreativeTab;
                }
                else
                {
                    tab = itemStack.getItem().tabToDisplayOn;
                }
                if (tab != null)
                {
                    int index = tab.tabIndex;

                    // TODO I might be silly, but um...isn't this the same as creativeTabs[index]? o_o
                    for (int i = 0; i < creativeTabs.length; i++)
                    {
                        if (creativeTabs[i] && index == i)
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void setValue(int place, boolean value)
    {
        if (place < customFilters.length)
        {
            customFilters[place] = value;
        }
        else
        {
            creativeTabs[getCreativeTab(place)] = value;
        }
        tile.onFilterChanged();
    }

    public boolean getValue(int place)
    {
        if (place < customFilters.length)
        {
            return customFilters[place];
        }
        else
        {
            return creativeTabs[getCreativeTab(place)];
        }
    }

    public String getName(int place)
    {
        switch (place)
        {
            case 0:
                return StatCollector.translateToLocal(Strings.INGOT_FILTER);
            case 1:
                return StatCollector.translateToLocal(Strings.ORE_FILTER);
            case 2:
                return StatCollector.translateToLocal(Strings.LOG_FILTER);
            case 3:
                return StatCollector.translateToLocal(Strings.PLANK_FILTER);
            case 4:
                return StatCollector.translateToLocal(Strings.DUST_FILTER);
            case 5:
                return StatCollector.translateToLocal(Strings.CRUSHED_ORE_FILTER);
            case 6:
                return StatCollector.translateToLocal(Strings.PURIFIED_ORE_FILTER);
            case 7:
                return StatCollector.translateToLocal(Strings.PLATE_FILTER);
            case 8:
                return StatCollector.translateToLocal(Strings.GEM_FILTER);
            case 9:
                return StatCollector.translateToLocal(Strings.FOOD_FILTER);
            case 10:
                return StatCollector.translateToLocal(Strings.DYE_FILTER);
            case 11:
                return StatCollector.translateToLocal(Strings.NUGGET_FILTER);
            case 12:
                return StatCollector.translateToLocal(Strings.PLANT_FILTER);
            case 13:
                return StatCollector.translateToLocal(Strings.FUEL_FILTER);
            default:
                return I18n.format(creativeTabArray[getCreativeTab(place)].getTranslatedTabLabel()).replace("itemGroup.", "");
        }
    }

    public int getCreativeTab(int place)
    {
        int index = place - FILTER_SIZE;
        if (index >= 5)
            index++;
        if (index >= 11)
            index++;
        return index;
    }

    public boolean isBlacklisting()
    {
        return blacklists;
    }

    public void setBlacklists(boolean blacklists)
    {
        this.blacklists = blacklists;
        tile.onFilterChanged();
    }

    public String getUserFilter()
    {
        return userFilter;
    }

    public void setUserFilter(String userFilter)
    {
        this.userFilter = userFilter;
        tile.onFilterChanged();
    }

    public List<String> getWAILAInformation(NBTTagCompound nbtData)
    {
        List<String> filter = new ArrayList<String>();

        filter.add(StringHelper.getLocalizedString(Strings.MODE) + ": " + (nbtData.getBoolean("blacklists") ? StringHelper.getLocalizedString(Strings.BLACKLIST) : StringHelper.getLocalizedString(Strings.WHITELIST)));

        if (nbtData.hasKey("userFilter") && StringUtils.isNotBlank(nbtData.getString("userFilter")))
        {
            filter.add(StatCollector.translateToLocalFormatted(Strings.WAILA_USER_FILTER, StringUtils.abbreviate(nbtData.getString("userFilter"), 40)));
        }

        int usedPresets = 0;

        for (int i = 0; i < customFilters.length; i++)
        {
            if (nbtData.getBoolean("cumstomFilters" + i))
            {
                filter.add(" " + getName(i) + (usedPresets == 0 ? " " + StringHelper.getLocalizedString(Strings.ELLIPSE) : ""));
                ++usedPresets;
                if (usedPresets == 2)
                    return filter;
            }
        }

        String filters = nbtData.getString("filters");

        loop:
        for (String string : filters.split("\\^\\$"))
        {
            for (int i = 0; i < creativeTabArray.length && i < creativeTabs.length; i++)
            {
                if (string.equals(creativeTabArray[i].tabLabel))
                {
                    filter.add(" " + I18n.format(creativeTabArray[i].getTranslatedTabLabel()).replace("itemGroup.", "") + (usedPresets == 0 ? " " + StringHelper.getLocalizedString(Strings.ELLIPSE) : ""));
                    ++usedPresets;
                    if (usedPresets == 2)
                        break loop;
                }
            }
        }
        return filter;
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setString("userFilter", getUserFilter());
        compound.setBoolean("blacklists", isBlacklisting());
        for (int i = 0; i < customFilters.length; i++)
        {
            compound.setBoolean("cumstomFilters" + i, customFilters[i]);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < creativeTabArray.length && i < creativeTabs.length; i++)
        {
            if (creativeTabs[i])
            {
                builder.append(creativeTabArray[i].tabLabel).append("^$");
            }
        }
        compound.setString("filters", builder.toString());
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        this.userFilter = compound.getString("userFilter");
        this.blacklists = compound.getBoolean("blacklists");
        for (int i = 0; i < customFilters.length; i++)
        {
            customFilters[i] = compound.getBoolean("cumstomFilters" + i);
        }

        String filters = compound.getString("filters");
        for (String string : filters.split("\\^\\$"))
        {
            for (int i = 0; i < creativeTabArray.length && i < creativeTabs.length; i++)
            {
                if (string.equals(creativeTabArray[i].tabLabel))
                    creativeTabs[i] = true;
            }
        }

        //1.0.7- compat
        for (int i = 0; compound.hasKey("creativeTabs" + i) && i < creativeTabs.length; i++)
        {
            creativeTabs[i] = compound.getBoolean("creativeTabs" + i);
        }
    }
}
