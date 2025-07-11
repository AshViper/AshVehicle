package tech.lq0.ashvehicle.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import tech.lq0.ashvehicle.entity.Class.BaseTankEntity;
import tech.lq0.ashvehicle.init.ModEntities;


public class M1A1AbramsEntity extends BaseTankEntity {
    public M1A1AbramsEntity(EntityType<?> type, Level world) {
        super(type, world);
    }
    public M1A1AbramsEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.M1A1_ABRAMS.get(), level);
    }
}
