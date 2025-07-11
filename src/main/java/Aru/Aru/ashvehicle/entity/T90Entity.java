package Aru.Aru.ashvehicle.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import Aru.Aru.ashvehicle.entity.Class.BaseTankEntity;
import Aru.Aru.ashvehicle.init.ModEntities;

public class T90Entity extends BaseTankEntity {
    public T90Entity(EntityType<?> type, Level world) {
        super(type, world);
    }
    public T90Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.T_90.get(), level);
    }
}
