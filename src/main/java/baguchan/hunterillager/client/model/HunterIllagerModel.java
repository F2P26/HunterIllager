package baguchan.hunterillager.client.model;

import baguchan.hunterillager.entity.HunterIllagerEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HunterIllagerModel<T extends HunterIllagerEntity> extends EntityModel<T> implements IHasArm, IHasHead {
    public RendererModel head;
    public RendererModel rightHand;
    public RendererModel rightLeg;
    public RendererModel leftLeg;
    public RendererModel leftHand;
    public RendererModel body;
    public RendererModel body2;
    public RendererModel crossRightHand;
    public RendererModel crossHand;
    public RendererModel quivers;
    public RendererModel hat;
    public RendererModel nose;
    public RendererModel crossLeftHand;

    public HunterIllagerModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.head = new RendererModel(this, 0, 0);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, 0.0F);
        this.body2 = new RendererModel(this, 0, 38);
        this.body2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body2.addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, 0.5F);
        this.leftLeg = new RendererModel(this, 0, 22);
        this.leftLeg.mirror = true;
        this.leftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.crossHand = new RendererModel(this, 40, 38);
        this.crossHand.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.crossHand.addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, 0.0F);
        this.rightHand = new RendererModel(this, 40, 46);
        this.rightHand.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightHand.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.crossRightHand = new RendererModel(this, 44, 22);
        this.crossRightHand.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.crossRightHand.addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.crossLeftHand = new RendererModel(this, 44, 22);
        this.crossLeftHand.mirror = true;
        this.crossLeftHand.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.crossLeftHand.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.quivers = new RendererModel(this, 29, 48);
        this.quivers.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.quivers.addBox(-1.5F, 0.0F, 0.0F, 3, 11, 2, 0.6F);
        this.rightLeg = new RendererModel(this, 0, 22);
        this.rightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.nose = new RendererModel(this, 24, 0);
        this.nose.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.nose.addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, 0.0F);
        this.hat = new RendererModel(this, 32, 0);
        this.hat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat.addBox(-4.0F, -10.0F, -4.0F, 8, 12, 8, 0.1F);
        this.body = new RendererModel(this, 16, 20);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, 0.0F);
        this.leftHand = new RendererModel(this, 40, 46);
        this.leftHand.mirror = true;
        this.leftHand.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftHand.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.crossRightHand.addChild(this.crossLeftHand);
        this.head.addChild(this.nose);
        this.head.addChild(this.hat);
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.head.render(scale);
        this.body2.render(scale);
        this.leftLeg.render(scale);
        this.quivers.render(scale);
        this.rightLeg.render(scale);
        this.body.render(scale);
        AbstractIllagerEntity abstractillager = entityIn;

        if (abstractillager.getArmPose() == AbstractIllagerEntity.ArmPose.CROSSED)
        {
            this.crossRightHand.render(scale);
            this.crossHand.render(scale);
        }
        else
        {
            this.rightHand.render(scale);
            this.leftHand.render(scale);
        }
    }

    @Override
    public void setRotationAngles(T p_212844_1_, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor)
    {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;
        this.crossHand.rotationPointY = 3.0F;
        this.crossHand.rotationPointZ = -1.0F;
        this.crossHand.rotateAngleX = -0.75F;
        this.crossRightHand.rotateAngleX = -0.7499679795819634F;
        this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.rightLeg.rotateAngleY = 0.0F;
        this.leftLeg.rotateAngleY = 0.0F;

        AbstractIllagerEntity.ArmPose abstractillager$illagerarmpose = ((AbstractIllagerEntity)p_212844_1_).getArmPose();

        if (abstractillager$illagerarmpose == AbstractIllagerEntity.ArmPose.ATTACKING) {
            float f = MathHelper.sin(this.swingProgress * (float) Math.PI);
            float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
            this.rightHand.rotateAngleZ = 0.0F;
            this.leftHand.rotateAngleZ = 0.0F;
            this.rightHand.rotateAngleY = 0.15707964F;
            this.leftHand.rotateAngleY = -0.15707964F;
            if (p_212844_1_.getPrimaryHand() == HandSide.RIGHT) {
                this.rightHand.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                this.leftHand.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
                this.rightHand.rotateAngleX += f * 2.2F - f1 * 0.4F;
                this.leftHand.rotateAngleX += f * 1.2F - f1 * 0.4F;
            } else {
                this.rightHand.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
                this.leftHand.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                this.rightHand.rotateAngleX += f * 1.2F - f1 * 0.4F;
                this.leftHand.rotateAngleX += f * 2.2F - f1 * 0.4F;
            }

            this.rightHand.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.leftHand.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.rightHand.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            this.leftHand.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        } else if (abstractillager$illagerarmpose == AbstractIllagerEntity.ArmPose.SPELLCASTING)
        {
            this.rightHand.rotationPointZ = 0.0F;
            this.rightHand.rotationPointX = -5.0F;
            this.leftHand.rotationPointZ = 0.0F;
            this.leftHand.rotationPointX = 5.0F;
            this.rightHand.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
            this.leftHand.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
            this.rightHand.rotateAngleZ = 2.3561945F;
            this.leftHand.rotateAngleZ = -2.3561945F;
            this.rightHand.rotateAngleY = 0.0F;
            this.leftHand.rotateAngleY = 0.0F;
        }
        else if (abstractillager$illagerarmpose == AbstractIllagerEntity.ArmPose.BOW_AND_ARROW)
        {
            this.rightHand.rotateAngleY = -0.1F + this.head.rotateAngleY;
            this.rightHand.rotateAngleX = -((float)Math.PI / 2F) + this.head.rotateAngleX;
            this.leftHand.rotateAngleX = -0.9424779F + this.head.rotateAngleX;
            this.leftHand.rotateAngleY = this.head.rotateAngleY - 0.4F;
            this.leftHand.rotateAngleZ = ((float)Math.PI / 2F);
        }
    }

    public RendererModel getArm(HandSide p_191216_1_)
    {
        return p_191216_1_ == HandSide.LEFT ? this.leftHand : this.rightHand;
    }

    @Override
    public RendererModel func_205072_a() {
        return head;
    }

    @Override
    public void postRenderArm(float scale, HandSide side) {
        this.getArm(side).postRender(0.0625F);
    }
}
