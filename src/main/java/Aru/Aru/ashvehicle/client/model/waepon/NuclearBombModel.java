package Aru.Aru.ashvehicle.client.model.waepon;


import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.weapon.NuclearBombEntity;

public class NuclearBombModel extends GeoModel<NuclearBombEntity> {
    public NuclearBombModel() {
    }

    public ResourceLocation getAnimationResource(NuclearBombEntity entity) {
        return null;
    }

    public ResourceLocation getModelResource(NuclearBombEntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/gbu-57.geo.json");
    }

    public ResourceLocation getTextureResource(NuclearBombEntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/weapon/gbu-57.png");
    }
}
