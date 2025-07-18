package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.KV2Entity;

public class KV2Model extends GeoModel<KV2Entity> {

    @Override
    public ResourceLocation getAnimationResource(KV2Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(KV2Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/kv-2.geo.json");}

    @Override
    public ResourceLocation getTextureResource(KV2Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/kv-2.png");}
}