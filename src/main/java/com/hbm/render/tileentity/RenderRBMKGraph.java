package com.hbm.render.tileentity;

import com.hbm.blocks.machine.rbmk.RBMKMiniPanelBase;
import com.hbm.interfaces.AutoRegister;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import com.hbm.main.ResourceManager;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKGraph;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKGraph.GraphUnit;
import com.hbm.util.BobMathUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

@AutoRegister
public class RenderRBMKGraph extends TileEntitySpecialRenderer<TileEntityRBMKGraph> {
	@Override
	public void render(TileEntityRBMKGraph te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);

		EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(RBMKMiniPanelBase.FACING);
		switch(facing) {
			case NORTH: GlStateManager.rotate(90, 0F, 1F, 0F); break;
			case WEST: GlStateManager.rotate(180, 0F, 1F, 0F); break;
			case SOUTH: GlStateManager.rotate(270, 0F, 1F, 0F); break;
			case EAST: GlStateManager.rotate(0, 0F, 1F, 0F); break;
			default: break;
		}
		
		TileEntityRBMKGraph gauge = (TileEntityRBMKGraph) te;
		
		for(int i = 0; i < 2; i++) {
			GraphUnit unit = gauge.graphs[i];
			if(!unit.active) continue;
			
			GL11.glPushMatrix();
			GL11.glTranslated(0.25, i * -0.5 + 0.25, 0);

			GL11.glColor3f(1F, 1F, 1F);
			this.bindTexture(ResourceManager.rbmk_numitron_tex);
			ResourceManager.rbmk_numitron.renderAll();
			
			GL11.glPushMatrix();
			
			RenderArcFurnace.fullbright(true);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glLineWidth(2F);

			FontRenderer font = Minecraft.getMinecraft().fontRenderer;
			int height = font.FONT_HEIGHT;

			long lowest = BobMathUtil.min(unit.values);
			long highest = BobMathUtil.max(unit.values);

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buf = tess.getBuffer();
			GlStateManager.color(0,1,0);
			buf.begin(GL11.GL_LINES,DefaultVertexFormats.POSITION);
			long range = highest - lowest;
			for(int v = 0; v < unit.values.length - 1; v++) {
				for(int j = 0; j < 2; j++) {
					int k = v + j;
					long flux = unit.values[k];
					double dx = 0.03225;
					double dy = 0.5 - 0.03125 + (flux - lowest) * 0.1875D / Math.max(range, 1);
					double dz = 0.375 - k * 0.75 / (unit.values.length - 1);
					buf.pos(dx, dy, dz).endVertex();
				}
			}
			tess.draw();
			GlStateManager.color(1,1,1);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			GL11.glPushMatrix();
			String labelLower = "" + lowest;
			String labelUpper = "" + highest;
			double lineScale = 0.0025D;
			GL11.glTranslated(0.032, 0.5 - 0.03125 * 1.5, -0.375 + 0.03125);
			GL11.glScaled(lineScale, -lineScale, lineScale);
			GL11.glRotatef(90, 0, 1, 0);
			font.drawString("" + labelLower, -font.getStringWidth(labelLower), -height / 2, 0x00ff00);
			GL11.glTranslated(0, -0.03125 * 7 / lineScale, 0);
			font.drawString("" + labelUpper, -font.getStringWidth(labelUpper), -height / 2, 0x00ff00);
			GL11.glPopMatrix();
			
			RenderArcFurnace.fullbright(false);
			
			GL11.glPopMatrix();
			
			if(unit.label != null && !unit.label.isEmpty()) {

				GL11.glTranslated(0.01, 0.3125, 0);
				int width = font.getStringWidth(unit.label);
				float f3 = Math.min(0.0125F, 0.75F / Math.max(width, 1));
				GL11.glScalef(f3, -f3, f3);
				GL11.glNormal3f(0.0F, 0.0F, -1.0F);
				GL11.glRotatef(90, 0, 1, 0);

				RenderArcFurnace.fullbright(true);
				font.drawString(unit.label, - width / 2, - height / 2, 0x00ff00);
				RenderArcFurnace.fullbright(false);
			}
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
	}
}
