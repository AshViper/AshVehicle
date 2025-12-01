package Aru.Aru.ashvehicle.client.model;

import Aru.Aru.ashvehicle.AshVehicle;
import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.resource.ModelResource;
import com.atsuishio.superbwarfare.resource.vehicle.DefaultVehicleResource;
import com.atsuishio.superbwarfare.resource.vehicle.VehicleResource;
import com.atsuishio.superbwarfare.tools.ResourceOnceLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VehicleModel<T extends VehicleEntity & GeoAnimatable> extends GeoModel<T> {
    protected float pitch;
    protected float yaw;
    protected float roll;
    protected float leftWheelRot;
    protected float rightWheelRot;
    protected float leftTrack;
    protected float rightTrack;
    protected float turretYRot;
    protected float turretXRot;
    protected float turretYaw;
    protected float recoilShake;
    protected boolean hideForTurretControllerWhileZooming;
    protected boolean hideForPassengerWeaponStationControllerWhileZooming;
    private final ResourceOnceLogger LOGGER = new ResourceOnceLogger();
    protected ResourceLocation modelCache = null;
    protected ResourceLocation textureCache = null;
    public static final Pattern TRACK_PATTERN = Pattern.compile("^track(?<type>Mov|Rot)(?<direction>[LR])(?<id>\\d+)$");
    public static final Pattern WHEEL_PATTERN = Pattern.compile("^wheel(?<direction>[LR]).*$");
    protected boolean init = false;
    protected final List<Pair<String, com.atsuishio.superbwarfare.client.model.entity.VehicleModel.TransformContext<T>>> TRANSFORMS = new ArrayList();

    public VehicleModel() {
    }

    public ResourceLocation getAnimationResource(T vehicle) {
        return getDefault(vehicle).getModel().animation;
    }

    public ResourceLocation getModelResource(T vehicle) {
        if (RenderHelper.isInGui()) {
            return getDefault(vehicle).getModel().model;
        } else {
            int lodLevel = this.getLODLevel(vehicle);
            ResourceLocation lodModel = getDefault(vehicle).getModel().getLODModel(lodLevel);
            if (lodModel == null) {
                if (this.modelCache != null) {
                    return this.modelCache;
                } else {
                    this.LOGGER.log(vehicle, (logger) -> logger.error("failed to load model for {}!", vehicle));
                    ResourceLocation loc = new ResourceLocation(AshVehicle.MODID,"geo/" + EntityType.getKey(vehicle.getType()).getPath() + ".geo.json");
                    this.modelCache = loc;
                    return loc;
                }
            } else {
                this.modelCache = lodModel;
                return lodModel;
            }
        }
    }

    public ResourceLocation getTextureResource(T vehicle) {
        if (RenderHelper.isInGui()) {
            return getDefault(vehicle).getModel().texture;
        } else {
            int lodLevel = this.getLODLevel(vehicle);
            ResourceLocation lodTexture = getDefault(vehicle).getModel().getLODTexture(lodLevel);
            if (lodTexture == null) {
                if (this.textureCache != null) {
                    return this.textureCache;
                } else {
                    this.LOGGER.log(vehicle, (logger) -> logger.error("failed to load texture for {}!", vehicle));
                    ResourceLocation loc = new ResourceLocation(AshVehicle.MODID,"textures/entity/" + EntityType.getKey(vehicle.getType()).getPath() + ".png");
                    this.textureCache = loc;
                    return loc;
                }
            } else {
                this.textureCache = lodTexture;
                return lodTexture;
            }
        }
    }

    public int getLODLevel(T vehicle) {
        DefaultVehicleResource defaultData = getDefault(vehicle);
        ModelResource model = defaultData.getModel();
        if (defaultData.lodDistance != null && !defaultData.lodDistance.list.isEmpty() && model.hasLOD()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && !player.isScoping()) {
                double distance = player.position().distanceTo(vehicle.position());

                for(int i = 0; i < defaultData.lodDistance.list.size(); ++i) {
                    if (distance <= (Double)defaultData.lodDistance.list.get(i)) {
                        return i;
                    }
                }

                return Integer.MAX_VALUE;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private static <T extends VehicleEntity & GeoAnimatable> DefaultVehicleResource getDefault(T vehicle) {
        return VehicleResource.getDefault(vehicle);
    }

    @Nullable
    public VehicleModel.TransformContext<T> collectTransform(String boneName) {
        if (boneName.equals("root") && this.hideForTurretControllerWhileZooming()) {
            return (bone, vehicle, state) -> bone.setHidden(this.hideForTurretControllerWhileZooming);
        } else if (boneName.equals("passengerWeaponStation") && this.hideForTurretControllerWhileZooming()) {
            return (bone, vehicle, state) -> bone.setHidden(this.hideForPassengerWeaponStationControllerWhileZooming);
        } else if (boneName.equals("laser")) {
            return (bone, vehicle, state) -> {
                bone.setScaleZ(10.0F * (Float)vehicle.getEntityData().get(VehicleEntity.LASER_LENGTH));
                float scale = Math.min(Mth.lerp(state.getPartialTick(), (Float)vehicle.getEntityData().get(VehicleEntity.LASER_SCALE_O), (Float)vehicle.getEntityData().get(VehicleEntity.LASER_SCALE)), 1.2F);
                bone.setScaleX(scale);
                bone.setScaleY(scale);
            };
        } else {
            switch (boneName) {
                case "base":
                    return (bone, vehicle, state) -> {
                        float a = (Float)vehicle.getEntityData().get(VehicleEntity.YAW_WHILE_SHOOT);
                        float r = (Mth.abs(a) - 90.0F) / 90.0F;
                        float r2;
                        if (Mth.abs(a) <= 90.0F) {
                            r2 = a / 90.0F;
                        } else if (a < 0.0F) {
                            r2 = -(180.0F + a) / 90.0F;
                        } else {
                            r2 = (180.0F - a) / 90.0F;
                        }

                        bone.setPosX(r2 * this.recoilShake * 0.5F);
                        bone.setPosZ(r * this.recoilShake * 1.0F);
                        bone.setRotX(r * this.recoilShake * ((float)Math.PI / 180F));
                        bone.setRotZ(r2 * this.recoilShake * ((float)Math.PI / 180F));
                    };
                case "turret":
                    return (bone, vehicle, state) -> {
                        bone.setRotY(this.turretYRot * ((float)Math.PI / 180F));
                        CoreGeoBone turretLaser = this.getAnimationProcessor().getBone("turretLaser");
                        if (turretLaser != null) {
                            turretLaser.setRotY(bone.getRotY());
                        }

                    };
                case "barrel":
                    return (bone, vehicle, state) -> {
                        float a = this.turretYaw;
                        float r = (Mth.abs(a) - 90.0F) / 90.0F;
                        float r2;
                        if (Mth.abs(a) <= 90.0F) {
                            r2 = a / 90.0F;
                        } else if (a < 0.0F) {
                            r2 = -(180.0F + a) / 90.0F;
                        } else {
                            r2 = (180.0F - a) / 90.0F;
                        }

                        bone.setRotX(Mth.clamp(-this.turretXRot - r * this.pitch - r2 * this.roll, vehicle.getTurretMinPitch(), vehicle.getTurretMaxPitch()) * ((float)Math.PI / 180F));
                        CoreGeoBone barrelLaser = this.getAnimationProcessor().getBone("barrelLaser");
                        if (barrelLaser != null) {
                            barrelLaser.setRotX(bone.getRotX());
                        }

                    };
                case "passengerWeaponStationYaw":
                    return (bone, vehicle, state) -> bone.setRotY(Mth.lerp(state.getPartialTick(), vehicle.gunYRotO, vehicle.getGunYRot()) * ((float)Math.PI / 180F) - this.turretYRot * ((float)Math.PI / 180F));
                case "passengerWeaponStationPitch":
                    return (bone, vehicle, state) -> {
                        float a = vehicle.getTurretYaw(state.getPartialTick());
                        float r = (Mth.abs(a) - 90.0F) / 90.0F;
                        float r2;
                        if (Mth.abs(a) <= 90.0F) {
                            r2 = a / 90.0F;
                        } else if (a < 0.0F) {
                            r2 = -(180.0F + a) / 90.0F;
                        } else {
                            r2 = (180.0F - a) / 90.0F;
                        }

                        bone.setRotX(Mth.clamp(-Mth.lerp(state.getPartialTick(), vehicle.gunXRotO, vehicle.getGunXRot()) * ((float)Math.PI / 180F) - r * this.pitch * ((float)Math.PI / 180F) - r2 * this.roll * ((float)Math.PI / 180F), -0.17453292F, ((float)Math.PI / 3F)));
                    };
                default:
                    Matcher trackMatcher = TRACK_PATTERN.matcher(boneName);
                    if (trackMatcher.matches()) {
                        boolean isRot = trackMatcher.group("type").equals("Rot");
                        boolean isL = trackMatcher.group("direction").equals("L");
                        int index = Integer.parseInt(trackMatcher.group("id"));
                        if (isRot) {
                            return isL ? (bone, vehicle, state) -> {
                                float t = this.wrap(this.leftTrack + (float)(2 * index), vehicle);
                                bone.setRotX(-this.getBoneRotX(t) * ((float)Math.PI / 180F));
                            } : (bone, vehicle, state) -> {
                                float t2 = this.wrap(this.rightTrack + (float)(2 * index), vehicle);
                                bone.setRotX(-this.getBoneRotX(t2) * ((float)Math.PI / 180F));
                            };
                        } else {
                            return isL ? (bone, vehicle, state) -> {
                                float t = this.wrap(this.leftTrack + (float)(2 * index), vehicle);
                                bone.setPosY(this.getBoneMoveY(t));
                                bone.setPosZ(this.getBoneMoveZ(t));
                            } : (bone, vehicle, state) -> {
                                float t2 = this.wrap(this.rightTrack + (float)(2 * index), vehicle);
                                bone.setPosY(this.getBoneMoveY(t2));
                                bone.setPosZ(this.getBoneMoveZ(t2));
                            };
                        }
                    } else {
                        Matcher wheelMatcher = WHEEL_PATTERN.matcher(boneName);
                        if (wheelMatcher.matches()) {
                            boolean isL = wheelMatcher.group("direction").equals("L");
                            return boneName.endsWith("Turn") ? (bone, vehicle, state) -> {
                                bone.setRotX(1.5F * (isL ? this.leftWheelRot : this.rightWheelRot));
                                bone.setRotY(Mth.lerp(state.getPartialTick(), vehicle.rudderRotO, vehicle.getRudderRot()));
                            } : (bone, vehicle, state) -> bone.setRotX(1.5F * (isL ? this.leftWheelRot : this.rightWheelRot));
                        } else {
                            return null;
                        }
                    }
            }
        }
    }

    public void setCustomAnimations(T vehicle, long instanceId, AnimationState<T> animationState) {
        if (!this.init) {
            this.getAnimationProcessor().getRegisteredBones().forEach((bone) -> {
                String name = bone.getName();

                try {
                    TransformContext<T> transform = this.collectTransform(name);
                    if (transform != null) {
                        this.TRANSFORMS.add(new Pair(name, transform));
                    }
                } catch (Exception exception) {
                    Mod.LOGGER.error("failed to collect transform for vehicle {} bone {}:", vehicle, name, exception);
                }

            });
            this.init = true;
        }

        float partialTick = animationState.getPartialTick();
        this.pitch = vehicle.getPitch(partialTick);
        this.yaw = vehicle.getYaw(partialTick);
        this.roll = vehicle.getRoll(partialTick);
        this.leftWheelRot = Mth.lerp(partialTick, vehicle.leftWheelRotO, vehicle.getLeftWheelRot());
        this.rightWheelRot = Mth.lerp(partialTick, vehicle.rightWheelRotO, vehicle.getRightWheelRot());
        this.leftTrack = Mth.lerp(partialTick, vehicle.leftTrackO, vehicle.getLeftTrack());
        this.rightTrack = Mth.lerp(partialTick, vehicle.rightTrackO, vehicle.getRightTrack());
        this.turretYRot = Mth.lerp(partialTick, vehicle.turretYRotO, vehicle.getTurretYRot());
        this.turretXRot = Mth.lerp(partialTick, vehicle.turretXRotO, vehicle.getTurretXRot());
        this.turretYaw = vehicle.getTurretYaw(partialTick);
        this.recoilShake = Mth.lerp(partialTick, (float)vehicle.recoilShakeO, (float)vehicle.getRecoilShake());
        this.hideForTurretControllerWhileZooming = ClientEventHandler.zoomVehicle && vehicle.getNthEntity(vehicle.getTurretControllerIndex()) == Minecraft.getInstance().player;
        this.hideForPassengerWeaponStationControllerWhileZooming = ClientEventHandler.zoomVehicle && vehicle.getNthEntity(vehicle.getPassengerWeaponStationControllerIndex()) == Minecraft.getInstance().player;
        this.TRANSFORMS.forEach((pair) -> {
            String name = (String)pair.getA();
            CoreGeoBone bone = this.getAnimationProcessor().getBone(name);
            if (bone != null) {
                ((VehicleModel.TransformContext)pair.getB()).transform(bone, vehicle, animationState);
            }

        });
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    public float getBoneRotX(float t) {
        return t;
    }

    public float getBoneMoveY(float t) {
        return t;
    }

    public float getBoneMoveZ(float t) {
        return t;
    }

    protected float wrap(float value, int range) {
        return (value % (float)range + (float)range) % (float)range;
    }

    protected float wrap(float value, VehicleEntity vehicle) {
        return this.wrap(value, this.getDefaultWrapRange(vehicle));
    }

    public int getDefaultWrapRange(VehicleEntity vehicle) {
        return vehicle.getTrackAnimationLength();
    }

    @FunctionalInterface
    public interface TransformContext<T extends VehicleEntity & GeoAnimatable> {
        void transform(CoreGeoBone var1, T var2, AnimationState<T> var3);
    }
}
