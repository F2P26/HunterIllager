package baguchan.hunterillager.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

/**
 * ModelHunterIllager - bagu
 * Created using Tabula 7.0.0
 */
public class ModelHunterIllager extends ModelBase {
    public ModelRenderer head;
    public ModelRenderer rightHand;
    public ModelRenderer rightLeg;
    public ModelRenderer leftLeg;
    public ModelRenderer leftHand;
    public ModelRenderer body;
    public ModelRenderer body2;
    public ModelRenderer crossRightHand;
    public ModelRenderer crossHand;
    public ModelRenderer quivers;
    public ModelRenderer hat;
    public ModelRenderer nose;
    public ModelRenderer crossLeftHand;

    public ModelHunterIllager() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, 0.0F);
        this.body2 = new ModelRenderer(this, 0, 38);
        this.body2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body2.addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, 0.5F);
        this.leftLeg = new ModelRenderer(this, 0, 22);
        this.leftLeg.mirror = true;
        this.leftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.crossHand = new ModelRenderer(this, 40, 38);
        this.crossHand.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.crossHand.addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, 0.0F);
        this.setRotateAngle(crossHand, -0.7499679795819634F, 0.0F, 0.0F);
        this.rightHand = new ModelRenderer(this, 40, 46);
        this.rightHand.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightHand.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.crossRightHand = new ModelRenderer(this, 44, 22);
        this.crossRightHand.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.crossRightHand.addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.setRotateAngle(crossRightHand, -0.7499679795819634F, 0.0F, 0.0F);
        this.crossLeftHand = new ModelRenderer(this, 44, 22);
        this.crossLeftHand.mirror = true;
        this.crossLeftHand.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.crossLeftHand.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.quivers = new ModelRenderer(this, 29, 48);
        this.quivers.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.quivers.addBox(-1.5F, 0.0F, 0.0F, 3, 11, 2, 0.6F);
        this.rightLeg = new ModelRenderer(this, 0, 22);
        this.rightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.nose = new ModelRenderer(this, 24, 0);
        this.nose.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.nose.addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, 0.0F);
        this.hat = new ModelRenderer(this, 32, 0);
        this.hat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat.addBox(-4.0F, -10.0F, -4.0F, 8, 12, 8, 0.1F);
        this.body = new ModelRenderer(this, 16, 20);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, 0.0F);
        this.leftHand = new ModelRenderer(this, 40, 46);
        this.leftHand.mirror = true;
        this.leftHand.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftHand.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.crossRightHand.addChild(this.crossLeftHand);
        this.head.addChild(this.nose);
        this.head.addChild(this.hat);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.head.render(scale);
        this.body2.render(scale);
        this.leftLeg.render(scale);
        this.quivers.render(scale);
        this.rightLeg.render(scale);
        this.body.render(scale);
        AbstractIllager abstractillager = (AbstractIllager)entityIn;

        if (abstractillager.getArmPose() == AbstractIllager.IllagerArmPose.CROSSED)
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

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;
        this.crossHand.rotationPointY = 3.0F;
        this.crossHand.rotationPointZ = -1.0F;
        this.crossHand.rotateAngleX = -0.75F;
        this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.rightLeg.rotateAngleY = 0.0F;
        this.leftLeg.rotateAngleY = 0.0F;
        AbstractIllager.IllagerArmPose abstractillager$illagerarmpose = ((AbstractIllager)entityIn).getArmPose();

        if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.ATTACKING)
        {
            float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
            float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
            this.rightHand.rotateAngleZ = 0.0F;
            this.leftHand.rotateAngleZ = 0.0F;
            this.rightHand.rotateAngleY = 0.15707964F;
            this.leftHand.rotateAngleY = -0.15707964F;

            if (((EntityLivingBase)entityIn).getPrimaryHand() == EnumHandSide.RIGHT)
            {
                this.rightHand.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                this.leftHand.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
                this.rightHand.rotateAngleX += f * 2.2F - f1 * 0.4F;
                this.leftHand.rotateAngleX += f * 1.2F - f1 * 0.4F;
            }
            else
            {
                this.rightHand.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
                this.leftHand.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                this.rightHand.rotateAngleX += f * 1.2F - f1 * 0.4F;
                this.leftHand.rotateAngleX += f * 2.2F - f1 * 0.4F;
            }

            this.rightHand.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.leftHand.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.rightHand.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            this.leftHand.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        }
        else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.SPELLCASTING)
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
        else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW)
        {
            this.rightHand.rotateAngleY = -0.1F + this.head.rotateAngleY;
            this.rightHand.rotateAngleX = -((float)Math.PI / 2F) + this.head.rotateAngleX;
            this.leftHand.rotateAngleX = -0.9424779F + this.head.rotateAngleX;
            this.leftHand.rotateAngleY = this.head.rotateAngleY - 0.4F;
            this.leftHand.rotateAngleZ = ((float)Math.PI / 2F);
        }
    }

    public ModelRenderer getArm(EnumHandSide p_191216_1_)
    {
        return p_191216_1_ == EnumHandSide.LEFT ? this.leftHand : this.rightHand;
    }
    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
