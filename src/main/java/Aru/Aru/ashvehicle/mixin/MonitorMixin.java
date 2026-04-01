package Aru.Aru.ashvehicle.mixin;

import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import Aru.Aru.ashvehicle.tools.DroneFindUtil;
import com.atsuishio.superbwarfare.item.Monitor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin для Monitor чтобы он не сбрасывал Using когда связан с нашим RemoteDroneEntity
 */
@Mixin(Monitor.class)
public class MonitorMixin {

    /**
     * Перехватываем inventoryTick и предотвращаем сброс Using для наших дронов
     */
    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true, remap = false)
    private void onInventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        // Проверяем, связан ли монитор с нашим RemoteDroneEntity
        // String linkedDroneUUID = itemstack.getOrCreateTag().getString(Monitor.LINKED_DRONE);
        CompoundTag monitorTag = itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String linkedDroneUUID = monitorTag.getString(Monitor.LINKED_DRONE);
        if (linkedDroneUUID != null && !linkedDroneUUID.isEmpty() && !linkedDroneUUID.equals("none")) {
            RemoteDroneEntity remoteDrone = DroneFindUtil.findRemoteDrone(entity.level(), linkedDroneUUID);
            if (remoteDrone != null) {
                // Это наш дрон - отменяем оригинальный метод чтобы он не сбросил Using
                ci.cancel();
            }
        }
    }
}
