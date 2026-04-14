package com.hbm.render.tileentity;

import com.hbm.blocks.machine.rbmk.RBMKMiniPanelBase;
import com.hbm.interfaces.AutoRegister;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import com.hbm.main.ResourceManager;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKNumitron;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKNumitron.DisplayUnit;
import com.hbm.util.BobMathUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

@AutoRegister
public class RenderRBMKNumitron extends TileEntitySpecialRenderer<TileEntityRBMKNumitron> {
	@Override
	public void render(TileEntityRBMKNumitron te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {

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
		
		TileEntityRBMKNumitron gauge = (TileEntityRBMKNumitron) te;
		
		for(int i = 0; i < 2; i++) {
			DisplayUnit unit = gauge.displays[i];
			if(!unit.active) continue;
			
			GL11.glPushMatrix();
			GL11.glTranslated(0.25, i * -0.5 + 0.25, 0);

			GL11.glColor3f(1F, 1F, 1F);
			this.bindTexture(ResourceManager.rbmk_numitron_tex);
			ResourceManager.rbmk_numitron.renderAll();
			
			GL11.glPushMatrix();
			
			RenderArcFurnace.fullbright(true);
			GL11.glEnable(GL11.GL_LIGHTING);
			
			this.bindTexture(ResourceManager.rbmk_numitron_lights_tex);

			double scale = 200D;
			double w = 8D / scale;
			double h = 13D / scale;
			double yOffset = 0.5625D;
			
			String value = BobMathUtil.getShortNumber(unit.value);
			while(value.length() < 7) value = "0" + value;

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buf = tess.getBuffer();
			buf.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION_TEX_NORMAL);
			for(int j = 0; j < 7; j++) {
				double zOffset = (j - 3) * 0.1D;
				char c = value.charAt(j);
				double u = -1;
				double v = 0;
				if(c == '.') {u = 0.9; v = 0.5;}
				if(c == '-') {u = 0.8; v = 0.5;}
				else if(c == 'k') {u = 0.0; v = 0.5;}
				else if(c == 'M') {u = 0.1; v = 0.5;}
				else if(c == 'G') {u = 0.2; v = 0.5;}
				else if(c == 'T') {u = 0.3; v = 0.5;}
				else if(c == 'P') {u = 0.4; v = 0.5;}
				else if(c == 'E') {u = 0.5; v = 0.5;} // i would love to say this sucks, but this is actually surprisingly easy to read and probably the most performant way of doing it
				int charVal = c - '0'; // no string operations, no int parsing, no nothing, we just rawdog shit shit
				if(charVal >= 0 && charVal <= 9) {u = 0.1 * charVal; v = 0.0;}
				if(u == -1) {u = 0.8; v = 0.5;}
				buf.pos(0.03135, -h + yOffset, w - zOffset).tex(u, v + 0.5).normal(0,1,0).endVertex();
				buf.pos(0.03135, h + yOffset, w - zOffset).tex(u, v).normal(0,1,0).endVertex();
				buf.pos(0.03135, h + yOffset, -w - zOffset).tex(u + 0.1, v).normal(0,1,0).endVertex();
				buf.pos(0.03135, -h + yOffset, -w - zOffset).tex(u + 0.1, v + 0.5).normal(0,1,0).endVertex();
			}
			tess.draw();
			
			RenderArcFurnace.fullbright(false);
			
			GL11.glPopMatrix();

			FontRenderer font = Minecraft.getMinecraft().fontRenderer;
			int height = font.FONT_HEIGHT;
			
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
