package com.grizz.inventoryapp.inventory.repository;

import com.grizz.inventoryapp.inventory.repository.entity.InventoryEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {
    @NotNull Optional<InventoryEntity> findByItemId(@NotNull String itemId);

    @Modifying
    @Query("update InventoryEntity i set i.stock = i.stock - :quantity " +
            "where i.itemId = :itemId")
    @NotNull Integer decreaseStock(@NotNull @Param("itemId") String itemId,
                                   @NotNull @Param("quantity") Long quantity);
}
