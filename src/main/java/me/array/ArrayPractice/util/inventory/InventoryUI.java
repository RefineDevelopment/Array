package me.array.ArrayPractice.util.inventory;

import me.array.ArrayPractice.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class InventoryUI {

    private final List<Inventory2D> inventories;
    private final String title;
    private final int rowOffset;
    private final int rows;
    private int offset;
    private int page;

    public InventoryUI(final String title, final int rows) {
        this(title, rows, 0);
    }

    public InventoryUI(final String title, final boolean bool, final int rows) {
        this(title, rows, 0);
    }

    public InventoryUI(final String title, final int rows, final int rowOffset) {
        this.inventories = new LinkedList<Inventory2D>();
        this.title = title;
        this.rows = rows;
        this.rowOffset = rowOffset;
    }

    public Inventory2D getCurrentUI() {
        return this.inventories.get(this.page);
    }

    public Inventory getCurrentPage() {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }
        return this.inventories.get(this.page).toInventory();
    }

    public ClickableItem getItem(final int slot) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }
        final Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        return lastInventory.getItem(slot);
    }

    public int getSize() {
        return this.rows * 9;
    }

    private void createNewInventory() {
        final Inventory2D inventory = new Inventory2D(this.title, this.rows, this.rowOffset);
        if (this.inventories.size() > 0) {
            inventory.setItem(0, this.rows - 1, new AbstractClickableItem(ItemUtil.createItem(Material.ARROW, ChatColor.RED + "Page #" + this.inventories.size())) {
                @Override
                public void onClick(final InventoryClickEvent event) {
                    InventoryUI.this.page--;
                    try {
                        final Inventory2D inventory2D = InventoryUI.this.inventories.get(InventoryUI.this.page);
                        if (inventory2D == null) {
                            InventoryUI.this.page++;
                        } else {
                            event.getWhoClicked().openInventory(InventoryUI.this.getCurrentPage());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        InventoryUI.this.page++;
                    }
                }
            });
            if (inventory.currentY == this.rows - 1 && inventory.currentX == -1) {
                inventory.currentX++;
            }
        }
        this.inventories.add(inventory);
    }

    public void setItem(final int x, final int y, final ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }
        final Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        lastInventory.setItem(x - 1, y - 1, item);
    }

    public void setItem(final int slot, final ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }
        final Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        lastInventory.setItem(slot, item);
    }

    public void addItem(final ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }
        final Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        if (lastInventory.currentY == this.rows - 1 && lastInventory.currentX >= 7 - this.offset) {
            lastInventory.setItem(8, this.rows - 1, new AbstractClickableItem(ItemUtil.createItem(Material.ARROW, ChatColor.RED + "Page #" + (this.inventories.size() + 1))) {
                @Override
                public void onClick(final InventoryClickEvent event) {
                    InventoryUI.this.page++;
                    try {
                        final Inventory2D inventory2D = InventoryUI.this.inventories.get(InventoryUI.this.page);
                        if (inventory2D == null) {
                            InventoryUI.this.page--;
                        } else {
                            event.getWhoClicked().openInventory(InventoryUI.this.getCurrentPage());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        InventoryUI.this.page--;
                    }
                }
            });
            this.createNewInventory();
            this.addItem(item);
        } else {
            lastInventory.setItem(++lastInventory.currentX + this.offset, lastInventory.currentY, item);
        }
        if (lastInventory.currentX >= 8 - this.offset) {
            lastInventory.currentX = this.offset - 1;
            lastInventory.currentY++;
        }
    }

    public void removeItem(final int slot) {
        final Inventory2D inventory2D = this.inventories.get(this.page);
        this.setItem(slot, null);
        for (int i = slot + 1; i < this.getSize(); ++i) {
            final ClickableItem item = this.getItem(i);
            this.setItem(i - 1, item);
            this.setItem(i, null);
        }
        if (inventory2D.currentX >= 0) {
            inventory2D.currentX--;
        } else if (inventory2D.currentY > 0) {
            inventory2D.currentY--;
            inventory2D.currentX = 7;
        }
    }

    public String getTitle() {
        return this.title;
    }

    public int getRowOffset() {
        return this.rowOffset;
    }

    public int getRows() {
        return this.rows;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(final int offset) {
        this.offset = offset;
    }

    public int getPage() {
        return this.page;
    }

    public List<Inventory2D> getInventories() {
        return this.inventories;
    }

    public interface ClickableItem {
        void onClick(final InventoryClickEvent p0);

        ItemStack getItemStack();

        void setItemStack(final ItemStack p0);

        ItemStack getDefaultItemStack();
    }

    public static class EmptyClickableItem implements ClickableItem {
        private final ItemStack defaultItemStack;
        private ItemStack itemStack;

        public EmptyClickableItem(final ItemStack itemStack) {
            this.itemStack = itemStack;
            this.defaultItemStack = itemStack;
        }

        @Override
        public void onClick(final InventoryClickEvent event) {
        }

        @Override
        public ItemStack getDefaultItemStack() {
            return this.defaultItemStack;
        }

        @Override
        public ItemStack getItemStack() {
            return this.itemStack;
        }

        @Override
        public void setItemStack(final ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }

    public abstract static class AbstractClickableItem implements ClickableItem {
        private final ItemStack defaultItemStack;
        private ItemStack itemStack;

        public AbstractClickableItem(final ItemStack itemStack) {
            this.itemStack = itemStack;
            this.defaultItemStack = itemStack;
        }

        @Override
        public ItemStack getDefaultItemStack() {
            return this.defaultItemStack;
        }

        @Override
        public ItemStack getItemStack() {
            return this.itemStack;
        }

        @Override
        public void setItemStack(final ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }

    public static class InventoryUIHolder implements InventoryHolder {
        private InventoryUI inventoryUI;
        private String title;
        private int slots;

        private InventoryUIHolder(final InventoryUI inventoryUI, final String title, final int slots) {
            this.inventoryUI = inventoryUI;
            this.title = title;
            this.slots = slots;
        }

        public Inventory getInventory() {
            return this.inventoryUI.getCurrentPage();
        }

        public InventoryUI getInventoryUI() {
            return this.inventoryUI;
        }

        public String getTitle() {
            return this.title;
        }

        public int getSlots() {
            return this.slots;
        }
    }

    public class Inventory2D {
        private final ClickableItem[][] items;
        private final String title;
        private final int rows;
        private Inventory cachedInventory;
        private int currentX;
        private int currentY;

        public Inventory2D(final String title, final int rows, final int rowOffset) {
            this.currentX = -1;
            this.currentY = rowOffset;
            this.title = title;
            this.rows = rows;
            this.items = new ClickableItem[9][this.rows];
        }

        public void setItem(final int x, final int y, final ClickableItem clickableItem) {
            this.items[x][y] = clickableItem;
            if (this.cachedInventory != null) {
                final int slot = y * 9 + x;
                this.cachedInventory.setItem(slot, (clickableItem != null) ? clickableItem.getItemStack() : null);
            }
        }

        public void setItem(final int slot, final ClickableItem clickableItem) {
            final int y = Math.abs(slot / 9);
            final int x = -(y * 9 - slot);
            this.setItem(x, y, clickableItem);
        }

        public ClickableItem getItem(final int slot) {
            final int y = Math.abs(slot / 9);
            final int x = -(y * 9 - slot);
            if (this.items.length <= x) {
                return null;
            }
            final ClickableItem[] items = this.items[x];
            if (items.length <= y) {
                return null;
            }
            return items[y];
        }

        public Inventory toInventory() {
            if (this.cachedInventory != null) {
                return this.cachedInventory;
            }
            final Inventory inventory = Bukkit.getServer().createInventory(new InventoryUIHolder(InventoryUI.this, this.title, this.rows * 9), this.rows * 9, this.title);
            for (int y = 0; y < this.rows; ++y) {
                for (int x = 0; x < 9; ++x) {
                    final int slot = y * 9 + x;
                    final ClickableItem item = this.items[x][y];
                    if (item != null) {
                        inventory.setItem(slot, item.getItemStack());
                    }
                }
            }
            return this.cachedInventory = inventory;
        }

        public ClickableItem[][] getItems() {
            return this.items;
        }

        public String getTitle() {
            return this.title;
        }

        public int getRows() {
            return this.rows;
        }

        public Inventory getCachedInventory() {
            return this.cachedInventory;
        }

        public int getCurrentX() {
            return this.currentX;
        }

        public int getCurrentY() {
            return this.currentY;
        }
    }
}