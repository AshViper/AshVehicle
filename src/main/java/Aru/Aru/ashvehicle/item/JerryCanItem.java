package Aru.Aru.ashvehicle.item;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JerryCanItem extends Item {
    // 燃料の最大容量 (10000ユニット)
    public static final int MAX_FUEL = 1000000;

    public JerryCanItem(Properties pProperties) {
        super(pProperties.durability(MAX_FUEL).setNoRepair());
    }

    /**
     * 車両に対するインタラクションをイベントで処理します。
     * シフト＋右クリックで燃料を補充します。
     */
    @Mod.EventBusSubscriber(modid = "ashvehicle", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandler {
        @SubscribeEvent
        public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
            ItemStack stack = event.getItemStack();
            if (stack.getItem() instanceof JerryCanItem) {
                if (event.getTarget() instanceof VehicleEntity vehicle) {
                    Player player = event.getEntity();
                    
                    // シフト右クリック時のみ発動
                    if (player.isShiftKeyDown()) {
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                        
                        if (!player.level().isClientSide) {
                            double currentEnergy = vehicle.getEnergy();
                            double maxEnergy = vehicle.getMaxEnergy();
                            
                            if (currentEnergy < maxEnergy) {
                                // ジェリー缶内の燃料残量を取得 (耐久値が0に近いほど燃料が多い)
                                int fuelInCan = stack.getMaxDamage() - stack.getDamageValue();
                                double needed = maxEnergy - currentEnergy;
                                int toTransfer = (int) Math.min(fuelInCan, needed);
                                
                                if (toTransfer > 0) {
                                    // 燃料を補充。Accessor経由でProtectedメソッドを呼び出し
                                    try {
                                        ((Aru.Aru.ashvehicle.mixin.VehicleEntityAccessor) vehicle).superbwarfare$invokeSetEnergy((int)(currentEnergy + toTransfer));
                                        
                                        // ジェリー缶の燃料を消費
                                        stack.setDamageValue(stack.getDamageValue() + toTransfer);
                                        
                                        player.displayClientMessage(Component.translatable("message.ashvehicle.refilled", toTransfer).withStyle(ChatFormatting.GREEN), true);
                                    } catch (Exception e) {
                                        player.displayClientMessage(Component.literal("燃料の補充に失敗しました。").withStyle(ChatFormatting.RED), false);
                                    }
                                }
                            } else {
                                player.displayClientMessage(Component.translatable("message.ashvehicle.already_full").withStyle(ChatFormatting.RED), true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        int fuel = pStack.getMaxDamage() - pStack.getDamageValue();
        pTooltipComponents.add(Component.translatable("tooltip.ashvehicle.jerry_can.fuel", fuel, MAX_FUEL).withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("tooltip.ashvehicle.jerry_can.usage").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
