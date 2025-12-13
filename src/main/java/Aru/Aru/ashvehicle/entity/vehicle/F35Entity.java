package Aru.Aru.ashvehicle.entity.vehicle;

import com.atsuishio.superbwarfare.data.DataLoader;
import com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.tools.VectorTool;
import com.google.gson.JsonObject;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Math;

public class F35Entity extends GeoVehicleEntity {

    public static boolean vtolMode = true;

    DefaultVehicleData computed = this.computed();
    JsonObject engineInfo = computed.engineInfo;
    EngineInfo.Aircraft aircraft = DataLoader.GSON.fromJson(engineInfo, EngineInfo.Aircraft.class);

    public F35Entity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        aircraftEngine(this, aircraft);
        if (this.level().isClientSide) {
            if (this.engineRunning()) {
                playSwimSound.accept(this);
            }
        }
    }

    // Vキー用（Packet から呼ばれる）
    public void toggleVtolMode() {
        vtolMode = !vtolMode;
    }

    public static void aircraftEngine(VehicleEntity vehicle, EngineInfo.Aircraft engineInfo) {
        float powerAdd = engineInfo.increment;
        float powerReduce = engineInfo.decrement;
        float pitchSpeed = engineInfo.pitchSpeed;
        float yawSpeed = engineInfo.yawSpeed;
        float rollSpeed = engineInfo.rollSpeed;
        float lift = engineInfo.liftSpeed;
        float speedRate = engineInfo.speedRate;
        float gearRotateAngle = engineInfo.gearRotateAngle;
        int energyCost = (int)(engineInfo.energyCostRate * (double)Mth.abs((Float)vehicle.getEntityData().get(VehicleEntity.POWER)));
        float f = (float)Mth.clamp(Math.max((double)(vehicle.onGround() ? 0.819F : 0.82F) - 0.005 * vehicle.getDeltaMovement().length(), (double)0.5F) + (double)(0.001F * Mth.abs(90.0F - (float)VehicleVecUtils.calculateAngle(vehicle.getDeltaMovement(), vehicle.getViewVector(1.0F))) / 90.0F), 0.01, 0.99);
        boolean forward = vehicle.getDeltaMovement().dot(vehicle.getViewVector(1.0F)) > (double)0.0F;
        vehicle.setDeltaMovement(vehicle.getDeltaMovement().add(vehicle.getViewVector(1.0F).scale((forward ? 0.227 : 0.1) * vehicle.getDeltaMovement().dot(vehicle.getViewVector(1.0F)))));
        vehicle.setDeltaMovement(vehicle.getDeltaMovement().multiply((double)f, (double)f, (double)f));
        if (vehicle.isInFluidType() && vehicle.tickCount % 4 == 0) {
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            if (vehicle.lastTickSpeed > 0.4) {
                vehicle.hurt(ModDamageTypes.causeVehicleStrikeDamage(vehicle.level().registryAccess(), vehicle, (Entity)(vehicle.getFirstPassenger() == null ? vehicle : vehicle.getFirstPassenger())), (float)((double)20.0F * (vehicle.lastTickSpeed - 0.4) * (vehicle.lastTickSpeed - 0.4)));
            }
        }

        Entity passenger = vehicle.getFirstPassenger();
        if (vehicle.getEnergy() >= energyCost && (vehicle.getMaxEnergy() <= 0 || vehicle.getEnergy() > 0)) {
            vehicle.consumeEnergy(energyCost);
        } else {
            vehicle.setForwardInputDown(false);
            vehicle.setBackInputDown(false);
            vehicle.engineStart = false;
            vehicle.engineStartOver = false;
            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.95F);
        }

        if (vehicle.getHealth() > 0.1F * vehicle.getMaxHealth()) {
            if (passenger != null && !vehicle.isInFluidType()) {
                if (passenger instanceof Player) {
                    if (!vehicle.engineStart && vehicle.forwardInputDown() && (double)(Float)vehicle.getEntityData().get(VehicleEntity.POWER) > 0.01) {
                        vehicle.engineStart = true;
                        vehicle.level().playSound((Player)null, vehicle, engineInfo.engineStartSound, vehicle.getSoundSource(), 3.0F, 1.0F);
                    }

                    if (vehicle.getEnergy() >= energyCost) {
                        if (vehicle.forwardInputDown()) {
                            vehicle.getEntityData().set(VehicleEntity.POWER, (float)Mth.clamp((double)((Float)vehicle.getEntityData().get(VehicleEntity.POWER) + 0.0045F * powerAdd), -0.1, (double)1.0F));
                        }

                        if (vehicle.backInputDown()) {
                            vehicle.getEntityData().set(VehicleEntity.POWER, Math.max((Float)vehicle.getEntityData().get(VehicleEntity.POWER) - 0.006F * powerReduce, vehicle.onGround() ? -0.2F : 0.4F));
                        }
                    }

                    if (!vehicle.forwardInputDown() && !vehicle.backInputDown()) {
                        vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.995F);
                    }

                    if (!vehicle.onGround()) {
                        if (vehicle.rightInputDown()) {
                            vehicle.getEntityData().set(VehicleEntity.DELTA_ROT, (Float)vehicle.getEntityData().get(VehicleEntity.DELTA_ROT) - 0.6F);
                        } else if (vehicle.leftInputDown()) {
                            vehicle.getEntityData().set(VehicleEntity.DELTA_ROT, (Float)vehicle.getEntityData().get(VehicleEntity.DELTA_ROT) + 0.6F);
                        }
                    }

                    if (vehicle.downInputDown()) {
                        if (vehicle.onGround()) {
                            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.92F);
                            vehicle.setDeltaMovement(vehicle.getDeltaMovement().multiply(0.97, (double)1.0F, 0.97));
                        } else {
                            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.97F);
                            vehicle.setDeltaMovement(vehicle.getDeltaMovement().multiply(0.994, (double)1.0F, 0.994));
                        }

                        vehicle.getEntityData().set(VehicleEntity.PLANE_BREAK, Math.min((Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK) + 10.0F, 60.0F));
                    }
                }
            } else {
                vehicle.setLeftInputDown(false);
                vehicle.setRightInputDown(false);
                vehicle.setForwardInputDown(false);
                vehicle.setBackInputDown(false);
                vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.95F);
                if (vehicle.onGround()) {
                    vehicle.setDeltaMovement(vehicle.getDeltaMovement().multiply(0.94, (double)1.0F, 0.94));
                } else {
                    vehicle.setXRot(Mth.clamp(vehicle.getXRot() + 0.1F, -89.0F, 89.0F));
                }
            }

            float rotSpeed = 1.5F + 1.2F * Mth.abs(VectorTool.calculateY(vehicle.getRoll()));
            float addY = Mth.clamp(Math.max((vehicle.onGround() ? 0.6F : 0.2F) * (float)vehicle.getDeltaMovement().length(), 0.0F) * vehicle.getMouseMoveSpeedX(), -rotSpeed, rotSpeed);
            float addX = Mth.clamp(Math.min((float)Math.max(vehicle.getDeltaMovement().dot(vehicle.getViewVector(1.0F)) - 0.24, 0.15), 0.4F) * vehicle.getMouseMoveSpeedY(), -3.5F, 3.5F);
            float addZ = (Float)vehicle.getEntityData().get(VehicleEntity.DELTA_ROT) - (vehicle.onGround() ? 0.0F : 0.004F) * vehicle.getMouseMoveSpeedX() * (float)vehicle.getDeltaMovement().dot(vehicle.getViewVector(1.0F));
            vehicle.setYRot(vehicle.getYRot() + yawSpeed * addY);
            if (!vehicle.onGround()) {
                vehicle.setXRot(vehicle.getXRot() + pitchSpeed * addX);
                vehicle.setZRot(vehicle.getRoll() - rollSpeed * addZ);
            }

            if (!vehicle.onGround()) {
                float xSpeed = 1.0F + 20.0F * Mth.abs(vehicle.getXRot() / 180.0F);
                float speed = Mth.clamp(Mth.abs(vehicle.getRoll()) / (90.0F / xSpeed), 0.0F, 1.0F);
                if (vehicle.getRoll() > 0.0F) {
                    vehicle.setZRot(vehicle.getRoll() - Math.min(speed, vehicle.getRoll()));
                } else if (vehicle.getRoll() < 0.0F) {
                    vehicle.setZRot(vehicle.getRoll() + Math.min(speed, -vehicle.getRoll()));
                }
            }

            vehicle.setPropellerRot(vehicle.getPropellerRot() + 30.0F * (Float)vehicle.getEntityData().get(VehicleEntity.POWER));
            if (engineInfo.hasGear) {
                if (vehicle.upInputDown()) {
                    vehicle.setUpInputDown(false);
                    if ((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) == 0.0F && !vehicle.onGround()) {
                        vehicle.getEntityData().set(VehicleEntity.GEAR_UP, true);
                    } else if ((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) == 1.0F) {
                        vehicle.getEntityData().set(VehicleEntity.GEAR_UP, false);
                    }
                }

                if (vehicle.onGround()) {
                    vehicle.getEntityData().set(VehicleEntity.GEAR_UP, false);
                }

                if ((Boolean)vehicle.getEntityData().get(VehicleEntity.GEAR_UP)) {
                    vehicle.getEntityData().set(VehicleEntity.GEAR_ROT, Math.min((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) + 0.05F, 1.0F));
                } else {
                    vehicle.getEntityData().set(VehicleEntity.GEAR_ROT, Math.max((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) - 0.05F, 0.0F));
                }

                vehicle.setGearRot((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) * gearRotateAngle);
            }

            float flapX = (1.0F - Mth.abs(vehicle.getRoll()) / 90.0F) * Mth.clamp(vehicle.getMouseMoveSpeedY(), -22.5F, 22.5F) - VectorTool.calculateY(vehicle.getRoll()) * Mth.clamp(vehicle.getMouseMoveSpeedX(), -22.5F, 22.5F);
            vehicle.setFlap1LRot(Mth.clamp(-flapX - 4.0F * addZ - (Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK), -22.5F, 22.5F));
            vehicle.setFlap1RRot(Mth.clamp(-flapX + 4.0F * addZ - (Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK), -22.5F, 22.5F));
            vehicle.setFlap1L2Rot(Mth.clamp(-flapX - 4.0F * addZ + (Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK), -22.5F, 22.5F));
            vehicle.setFlap1R2Rot(Mth.clamp(-flapX + 4.0F * addZ + (Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK), -22.5F, 22.5F));
            vehicle.setFlap2LRot(Mth.clamp(flapX - 4.0F * addZ, -22.5F, 22.5F));
            vehicle.setFlap2RRot(Mth.clamp(flapX + 4.0F * addZ, -22.5F, 22.5F));
            float flapY = (1.0F - Mth.abs(vehicle.getRoll()) / 90.0F) * Mth.clamp(vehicle.getMouseMoveSpeedX(), -22.5F, 22.5F) + VectorTool.calculateY(vehicle.getRoll()) * Mth.clamp(vehicle.getMouseMoveSpeedY(), -22.5F, 22.5F);
            vehicle.setFlap3Rot(flapY * 5.0F);
        } else if (!vehicle.onGround()) {
            vehicle.getEntityData().set(VehicleEntity.POWER, Math.max((Float)vehicle.getEntityData().get(VehicleEntity.POWER) - 3.0E-4F, 0.02F));
            vehicle.destroyRot += 0.1F;
            float diffX = 90.0F - vehicle.getXRot();
            vehicle.setXRot(vehicle.getXRot() + diffX * 0.001F * vehicle.destroyRot);
            vehicle.setZRot(vehicle.getRoll() - vehicle.destroyRot);
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().add((double)0.0F, -0.03, (double)0.0F));
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().add((double)0.0F, (double)(-vehicle.destroyRot) * 0.005, (double)0.0F));
        }

        vehicle.getEntityData().set(VehicleEntity.DELTA_ROT, (Float)vehicle.getEntityData().get(VehicleEntity.DELTA_ROT) * 0.85F);
        vehicle.getEntityData().set(VehicleEntity.PLANE_BREAK, (Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK) * 0.8F);
        if (vehicle.onGround()) {
            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.995F);
        }

        if ((Boolean)vehicle.getEntityData().get(VehicleEntity.MAIN_ENGINE_DAMAGED)) {
            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.96F);
        }

        if ((Boolean)vehicle.getEntityData().get(VehicleEntity.SUB_ENGINE_DAMAGED)) {
            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.96F);
        }

        double flapAngle = (double)((vehicle.getFlap1LRot() + vehicle.getFlap1RRot() + vehicle.getFlap1L2Rot() + vehicle.getFlap1R2Rot()) / 4.0F);
        vehicle.setDeltaMovement(vehicle.getDeltaMovement().add(vehicle.getUpVec(1.0F).scale(vehicle.getDeltaMovement().dot(vehicle.getViewVector(1.0F)) * 0.022 * (double)lift * ((double)1.0F + Math.sin((vehicle.onGround() ? (double)25.0F : flapAngle + (double)25.0F) * (double)((float)java.lang.Math.PI / 180F))))));
        if (vtolMode){
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().add(vehicle.getViewVector(1.0F).lerp(vehicle.getUpVec(1.0F), 1.0F).normalize().scale(0.25 * speedRate * (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * (vehicle.sprintInputDown() ? 2.2 : 1.0))));
        }else{
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().add(vehicle.getViewVector(1.0F).scale(0.03 * (double)speedRate * (double)(Float)vehicle.getEntityData().get(VehicleEntity.POWER) * (vehicle.sprintInputDown() ? 2.2 : (double)1.0F))));
        }
        if ((Float)vehicle.getEntityData().get(VehicleEntity.POWER) > 0.2F) {
            vehicle.engineStartOver = true;
        }

        if ((Float)vehicle.getEntityData().get(VehicleEntity.POWER) < 4.0E-4F) {
            vehicle.engineStart = false;
            vehicle.engineStartOver = false;
        }
    }
}