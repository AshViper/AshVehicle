package Aru.Aru.ashvehicle.client.model.waepon;


import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.weapon.NapalmBombEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NapalmBombModel extends GeoModel<NapalmBombEntity> {
    public NapalmBombModel() {
    }

    public ResourceLocation getAnimationResource(NapalmBombEntity entity) {
        return null;
    }

    public ResourceLocation getModelResource(NapalmBombEntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/gbu-57.geo.json");
    }

    public ResourceLocation getTextureResource(NapalmBombEntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/weapon/gbu-57.png");
    }
}
