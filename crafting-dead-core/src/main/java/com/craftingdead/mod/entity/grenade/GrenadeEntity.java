package com.craftingdead.mod.entity.grenade;

import org.apache.commons.lang3.tuple.Triple;
import com.craftingdead.mod.entity.BounceableProjectileEntity;
import com.craftingdead.mod.item.GrenadeItem;
import com.craftingdead.mod.util.ModDamageSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraft.world.World;

public abstract class GrenadeEntity extends BounceableProjectileEntity {

  private static final Triple<SoundEvent, Float, Float> DEFAULT_BOUNCE_SOUND =
      Triple.of(SoundEvents.BLOCK_SCAFFOLDING_BREAK, 0.5F, 2F);
  private static final DataParameter<Boolean> ACTIVATED =
      EntityDataManager.createKey(GrenadeEntity.class, DataSerializers.BOOLEAN);
  private int activatedTicksCount = 0;
  private int deactivatedTicksCount = 0;

  public GrenadeEntity(EntityType<? extends GrenadeEntity> entityIn, World worldIn) {
    super(entityIn, worldIn);
  }

  public GrenadeEntity(EntityType<? extends GrenadeEntity> entityIn, LivingEntity thrower,
      World worldIn) {
    super(entityIn, thrower, worldIn);
  }

  public abstract GrenadeItem asItem();

  /**
   * Called every <code>tick()</code> if the grenade is not marked as removed.
   * Prefer using this instead of overriding <code>tick()</code>.
   */
  public abstract void onGrenadeTick();

  /**
   * Called after the activation state is changed.
   */
  public abstract void onActivationStateChange(boolean activated);

  @Override
  public void tick() {
    super.tick();

    if (this.isAlive()) {
      if (this.isActivated()) {
        this.activatedTicksCount++;
      } else {
        this.deactivatedTicksCount++;
      }

      this.onGrenadeTick();
    }

    // Check again if it is alive.
    // Someone could kill the entity in a previous callback
    if (this.isAlive()) {
      if (this.getMinimumTicksUntilAutoActivation() != null) {
        if (!this.isActivated()
            && this.deactivatedTicksCount >= this.getMinimumTicksUntilAutoActivation()) {
          this.setActivated(true);
        }
      }
    }

    // Check again if it is alive.
    // Someone could kill the entity in a previous callback
    if (this.isAlive()) {
      if (this.getMinimumTicksUntilAutoDeactivation() != null) {
        if (this.isActivated()
            && this.activatedTicksCount >= this.getMinimumTicksUntilAutoDeactivation()) {
          this.setActivated(false);
        }
      }
    }
  }

  @Override
  public boolean attackEntityFrom(DamageSource source, float amount) {
    if (ModDamageSource.isGunDamage(source)) {
      EntityDamageSource entitySource = (EntityDamageSource) source;
      this.setMotion(entitySource.getTrueSource().getLookVec().scale(1.5D));
    }
    return super.attackEntityFrom(source, amount);
  }

  @Override
  public void onSurfaceHit(BlockRayTraceResult blockRayTraceResult) {
    Triple<SoundEvent, Float, Float> bounceSound = this.getBounceSound(blockRayTraceResult);
    if (this.world.isRemote()) {
      this.world.playSound(this.getX(), this.getY(), this.getZ(),
          bounceSound.getLeft(), SoundCategory.NEUTRAL, bounceSound.getMiddle(),
          bounceSound.getRight(), false);
    }
  }

  @Override
  public final boolean processInitialInteract(PlayerEntity playerEntity, Hand hand) {
    boolean canPickup = !playerEntity.shouldCancelInteraction() && this.canBePickedUp(playerEntity);
    if (canPickup) {
      this.remove();
      playerEntity.addItemStackToInventory(new ItemStack(this.asItem(), 1));
      this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_PICKUP,
          SoundCategory.PLAYERS, 0.2F,
          (this.rand.nextFloat() - this.rand.nextFloat()) * 1.4F + 2.0F, false);
    }
    return canPickup;
  }

  /**
   * Whether the grenade can be picked up from the ground
   */
  public boolean canBePickedUp(PlayerEntity playerFrom) {
    return false;
  }

  /**
   * First decimal value is volume, second is pitch.
   */
  public Triple<SoundEvent, Float, Float> getBounceSound(BlockRayTraceResult blockRayTraceResult) {
    return DEFAULT_BOUNCE_SOUND;
  }

  /**
   * Whether this grenade can be activated by its owner using a remote detonator
   */
  public boolean canBeRemotelyActivated() {
    return false;
  }

  /**
   * Whether this grenade is attracting zombies and maybe other entities.
   */
  public boolean isAttracting() {
    return false;
  }

  public void setActivated(boolean activated) {
    boolean previousValue = this.getDataManager().get(ACTIVATED);
    this.getDataManager().set(ACTIVATED, activated);

    // has changed
    if (activated != previousValue) {
      if (!activated) {
        this.deactivatedTicksCount = 0;
      } else {
        this.activatedTicksCount = 0;
      }

      this.onActivationStateChange(activated);
    }
  }

  /**
   * The minimum amount of ticks the grenade will take to automatically activate.
   *
   * @return {@link Integer} - An amount of ticks or <code>null</code>.
   */
  public Integer getMinimumTicksUntilAutoActivation() {
    return null;
  }

  /**
   * The minimum amount of ticks the grenade will take to automatically deactivate.
   *
   * @return {@link Integer} - An amount of ticks or <code>null</code>.
   */
  public Integer getMinimumTicksUntilAutoDeactivation() {
    return null;
  }

  public boolean isActivated() {
    return this.getDataManager().get(ACTIVATED);
  }

  public int getActivatedTicksCount() {
    return this.activatedTicksCount;
  }

  @Override
  public boolean canBeCollidedWith() {
     return true;
  }

  @Override
  protected void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putBoolean("activated", this.isActivated());
    compound.putInt("activatedTicksCount", this.activatedTicksCount);
  }

  @Override
  protected void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setActivated(compound.getBoolean("activated"));
    this.activatedTicksCount = compound.getInt("activatedTicksCount");
  }

  @Override
  public void writeSpawnData(PacketBuffer buffer) {
    super.writeSpawnData(buffer);
    buffer.writeInt(this.activatedTicksCount);
  }

  @Override
  public void readSpawnData(PacketBuffer buffer) {
    super.readSpawnData(buffer);
    this.activatedTicksCount = buffer.readInt();
  }

  @Override
  protected void registerData() {
    this.getDataManager().register(ACTIVATED, false);
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
