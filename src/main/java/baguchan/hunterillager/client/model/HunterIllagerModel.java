package baguchan.hunterillager.client.model;

import baguchan.hunterillager.entity.HunterIllagerEntity;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.Iterator;

public class HunterIllagerModel<T extends HunterIllagerEntity> extends IllagerModel<T> {
	private ModelRenderer _body;

	private void setBodyRenderer() {
		Iterator<ModelRenderer> pieces = this.getParts().iterator();
		pieces.next();
		this._body = pieces.next();
	}

	private final ModelRenderer quiver;
	private final ModelRenderer cape;
	private final ModelRenderer capeLower;

	public HunterIllagerModel(float scaleFactor, float p_i47227_2_, int textureWidthIn, int textureHeightIn) {
		super(scaleFactor, p_i47227_2_, textureWidthIn, textureHeightIn);
		this.setBodyRenderer();

		//Mojang made the original hood 12 pixels for some reason, so this had to be done
		this.getModelHead().addBox("hood_fixed", -4F, -10F, -4F, 8, 10, 8, scaleFactor + 0.45F, 32, 0);

		this.cape = new ModelRenderer(this, 0, 0);
		this.cape.setTextureSize(textureWidthIn, textureHeightIn);
		this.cape.setRotationPoint(0F, 0.5F, 3F);
		this.cape.setTextureOffset(0, 64).addBox(-4.5F, 0F, -0.5F, 9, 11, 1, 0.1F + scaleFactor);
		this._body.addChild(this.cape);

		this.quiver = new ModelRenderer(this);
		this.quiver.setTextureSize(textureWidthIn, textureHeightIn);
		this.quiver.setRotationPoint(3F, 0F, 2.5F);
		this.quiver.setTextureOffset(20, 64).addBox(-2.5F, 0F, -2.5F, 5, 13, 3, -0.5F + scaleFactor);
		this.quiver.setTextureOffset(36, 64).addBox(-2.5F, 0F, -2.5F, 5, 13, 3, scaleFactor);
		this.cape.addChild(this.quiver);

		this.capeLower = new ModelRenderer(this, 0, 0);
		this.capeLower.setTextureSize(textureWidthIn, textureHeightIn);
		this.capeLower.setRotationPoint(0F, 11F, 0F);
		this.capeLower.setTextureOffset(0, 76).addBox(-4.5F, 0F, -0.5F, 9, 8, 1, 0.1F + scaleFactor);
		this.cape.addChild(this.capeLower);
	}

	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

		this.quiver.rotateAngleZ = 0.5235987755982988F;

		this.cape.rotateAngleX = ((float) (Math.PI) / 18) * (limbSwingAmount * 1.75F);
		this.capeLower.rotateAngleX = ((float) (Math.PI) / 32) * (limbSwingAmount * 1.75F);

	}

}
