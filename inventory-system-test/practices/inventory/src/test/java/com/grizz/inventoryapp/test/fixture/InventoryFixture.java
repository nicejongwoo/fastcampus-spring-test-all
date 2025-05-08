package com.grizz.inventoryapp.test.fixture;

import com.grizz.inventoryapp.inventory.service.domain.Inventory;
import org.jetbrains.annotations.Nullable;

public class InventoryFixture {

    public static Inventory simpleInventory(
            @Nullable String itemId,
            @Nullable Long stock
    ) {
        String inventoryItemId = itemId;
        if(inventoryItemId == null) {
            inventoryItemId = "1";
        }

        Long inventoryStock = stock;
        if(inventoryStock == null) {
            inventoryStock = 10L;
        }

        return new Inventory(inventoryItemId, inventoryStock);
    }

}
