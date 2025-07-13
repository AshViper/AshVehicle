package Aru.Aru.ashvehicle.client.model.waepon;


import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.weapon.BallisticMissileEntity;
import Aru.Aru.ashvehicle.entity.weapon.TomahawkEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TomahawkModel extends GeoModel<TomahawkEntity> {
    public TomahawkModel() {
    }

    public ResourceLocation getAnimationResource(TomahawkEntity entity) {
        return null;
    }

    public ResourceLocation getModelResource(TomahawkEntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/tomahawk.geo.json");
    }

    public ResourceLocation getTextureResource(TomahawkEntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/weapon/tomahawk.png");
    }
}
