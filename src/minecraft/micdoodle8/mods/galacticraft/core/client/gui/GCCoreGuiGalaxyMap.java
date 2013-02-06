package micdoodle8.mods.galacticraft.core.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import micdoodle8.mods.galacticraft.API.IMapPlanet;
import micdoodle8.mods.galacticraft.API.IPlanetSlotRenderer;
import micdoodle8.mods.galacticraft.core.GCCoreConfigManager;
import micdoodle8.mods.galacticraft.core.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.GLU;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GCCoreGuiGalaxyMap extends GuiScreen
{
    private static int guiMapMinX;

    private static int guiMapMinY;

    private static int guiMapMaxX;

    private static int guiMapMaxY;

    protected float mouseX = 0;

    protected float mouseY = 0;
    
    protected double field_74117_m;
    protected double field_74115_n;

    protected double guiMapX;

    protected double guiMapY;
    protected double field_74124_q;
    protected double field_74123_r;
    
    private GuiSmallButton button;
    
    private float zoom = 0.2F;
    
    EntityPlayer player;
    
    private IMapPlanet selectedPlanet = null;

    public GCCoreGuiGalaxyMap(EntityPlayer player)
    {
    	this.player = player;
    }

    @Override
	public void initGui()
    {
        this.mc.mouseHelper.grabMouseCursor();
        this.field_74117_m = this.guiMapX = this.field_74124_q = 0 - this.width / 4;
        this.field_74115_n = this.guiMapY = this.field_74123_r = 0 - this.height / 4;
        this.controlList.add(new GuiSmallButton(0, this.width - 82, this.height - 22, 80, 20, StatCollector.translateToLocal("gui.done")));
    }

    @Override
	protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 0)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
        }

        super.actionPerformed(par1GuiButton);
    }

    @Override
	protected void keyTyped(char par1, int par2)
    {
        if (par2 == this.mc.gameSettings.keyBindInventory.keyCode)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
        else
        {
            super.keyTyped(par1, par2);
        }
    }
    
    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
    	super.mouseClicked(par1, par2, par3);
    }

    @Override
	public void drawScreen(int par1, int par2, float par3)
    {
    	guiMapMinX = -25000 - this.width;
    	guiMapMaxX = 25000 + this.width;
    	guiMapMinY = -25000 - this.height;
    	guiMapMaxY = 25000 + this.height;
    	
        while (!Mouse.isButtonDown(0) && Mouse.next())
        {
        	float wheel = Mouse.getEventDWheel();
            
            if (Mouse.hasWheel() && wheel != 0)
            {
            	wheel /= 7500F;
            	
            	this.zoom = MathHelper.clamp_float(this.zoom + wheel, 0.011000001F, 2);
            }
        }
        
        if (GCCoreConfigManager.wasdMapMovement)
        {
        	if (Keyboard.isKeyDown(Keyboard.KEY_W))
        	{
        		this.mouseY = 1 * (1 / (this.zoom * 2));
        	}
        	
        	if (Keyboard.isKeyDown(Keyboard.KEY_S))
        	{
        		this.mouseY = -1 * (1 / (this.zoom * 2));
        	}
        	
        	if (Keyboard.isKeyDown(Keyboard.KEY_D))
        	{
        		this.mouseX = -1 * (1 / (this.zoom * 2));
        	}
        	
        	if (Keyboard.isKeyDown(Keyboard.KEY_A))
        	{
        		this.mouseX = 1 * (1 / (this.zoom * 2));
        	}
        	
        	this.mouseY *= 0.9;
        	this.mouseX *= 0.9;
        }
        else
        {
    		this.mouseY = ((-(this.mc.displayHeight / 2) + Mouse.getY()) / 100F) * (1 / (this.zoom * 5));
            this.mouseX = ((this.mc.displayWidth / 2 - Mouse.getX()) / 100F) * (1 / (this.zoom * 5));
        	
        	if (Mouse.getX() > this.mc.displayWidth / 2 - 90 && Mouse.getX() < this.mc.displayWidth / 2 + 90 && Mouse.getY() > this.mc.displayHeight / 2 - 90 && Mouse.getY() < this.mc.displayHeight / 2 + 90)
        	{
        		this.mouseX = 0;
        		this.mouseY = 0;
        	}
        }

        if (this.field_74124_q < guiMapMinX)
        {
            this.field_74124_q = guiMapMinX;
            
            if (this.mouseX > 0)
            {
                this.mouseX = 0;
            }
        }

        if (this.field_74123_r < guiMapMinY)
        {
            this.field_74123_r = guiMapMinY;
            
            if (this.mouseY > 0)
            {
                this.mouseY = 0;
            }
        }

        if (this.field_74124_q >= guiMapMaxX)
        {
            this.field_74124_q = (guiMapMaxX - 1);
            
            if (this.mouseX < 0)
            {
                this.mouseX = 0;
            }
        }

        if (this.field_74123_r >= guiMapMaxY)
        {
            this.field_74123_r = (guiMapMaxY - 1);
            
            if (this.mouseY < 0)
            {
                this.mouseY = 0;
            }
        }
        
        this.guiMapX -= this.mouseX;
        this.guiMapY -= this.mouseY;
        
        this.field_74124_q = this.field_74117_m = this.guiMapX;
        this.field_74123_r = this.field_74115_n = this.guiMapY;

        this.drawDefaultBackground();
        this.genAchievementBackground(par1, par2, par3);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.drawTitle();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    
    @Override
	public void updateScreen()
    {
        this.field_74117_m = this.guiMapX;
        this.field_74115_n = this.guiMapY;
        final double var1 = this.field_74124_q - this.guiMapX;
        final double var3 = this.field_74123_r - this.guiMapY;
    }

    protected void drawTitle()
    {
        this.fontRenderer.drawString("Galaxy Map", 15, 5, 4210752);
    }

    protected void genAchievementBackground(int par1, int par2, float par3)
    {
        final ScaledResolution var13 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        final int mX = var13.getScaledWidth();
        final int mY = var13.getScaledHeight();
        final int mX2 = Mouse.getX() * mX / this.mc.displayWidth;
        final int mY2 = mY - Mouse.getY() * mY / this.mc.displayHeight - 1;
        
        int var4 = MathHelper.floor_double(this.field_74117_m + (this.guiMapX - this.field_74117_m) * par3);
        int var5 = MathHelper.floor_double(this.field_74115_n + (this.guiMapY - this.field_74115_n) * par3);

        if (var4 < guiMapMinX)
        {
            var4 = guiMapMinX;
        }

        if (var5 < guiMapMinY)
        {
            var5 = guiMapMinY;
        }

        if (var4 >= guiMapMaxX)
        {
            var4 = guiMapMaxX - 1;
        }

        if (var5 >= guiMapMaxY)
        {
            var5 = guiMapMaxY - 1;
        }

        final int var10 = -var4;
        final int var11 = -var5;
        this.zLevel = 0.0F;
        GL11.glDepthFunc(GL11.GL_GEQUAL);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        final int var14 = (var4 + 288) % 256;
        final int var15 = (var5 + 288) % 256;
        
        final int var22;
        final int var25;
        final int var24;
        int var26;

		this.drawBlackBackground();
		this.renderSkybox(1);
        
        this.zoom(this.zoom);

        int var27;
        final int var30;

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        
        int var42;
        int var41;
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (final IMapPlanet planet : GalacticraftCore.mapPlanets)
        {
            var26 = 0;
            var27 = 0;
            
            final Map[] posMaps = this.computePlanetPos(var10, var11, planet.getDistanceFromCenter() / 2, 2880);
            
            if (posMaps[0] != null && posMaps[1] != null)
            {
            	if (posMaps[0].get(MathHelper.floor_float(Sys.getTime() / (720F * planet.getStretchValue()) % 2880)) != null && posMaps[1].get(MathHelper.floor_float(Sys.getTime() / 720F % 2880)) != null)
            	{
                	final int x = MathHelper.floor_float((Float) posMaps[0].get(MathHelper.floor_float((planet.getPhaseShift() + Sys.getTime() / (720F * planet.getStretchValue())) % 2880)));
                	final int y = MathHelper.floor_float((Float) posMaps[1].get(MathHelper.floor_float((planet.getPhaseShift() + Sys.getTime() / (720F * planet.getStretchValue())) % 2880)));
                	
                	var26 = x;
                	var27 = y;
            	}
            }

            final float var38;

            var42 = var10 + var26;
            var41 = var11 + var27;
            
            
            final IPlanetSlotRenderer renderer = planet.getSlotRenderer();
            
            GL11.glDisable(GL11.GL_BLEND);
            
            final float size = planet.getPlanetSize() / 2F * 1.3F * (this.zoom * 2F);
            
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            
            int width = (int) (planet.getPlanetSize() + (1 / this.zoom * 3F));

            if (Mouse.isButtonDown(0))
            {
            	int pointerMinX = this.width / 2 - 5;
            	int pointerMaxX = this.width / 2 + 5;
            	int pointerMinY = this.height / 2 - 5;
            	int pointerMaxY = this.height / 2 + 5;
            	int planetMinX = var42 - width;
            	int planetMaxX = var42 + width;
            	int planetMinY = var41 - width;
            	int planetMaxY = var41 + width;
            	
            	if (((pointerMaxX >= planetMinX && pointerMinX <= planetMinX) || (pointerMinX <= planetMinX && pointerMaxY >= planetMaxX) || (pointerMinX >= planetMinX && pointerMinX <= planetMaxX))
            			&& ((pointerMaxY >= planetMinY && pointerMinY <= planetMinY) || (pointerMinY <= planetMinY && pointerMaxY >= planetMaxY) || (pointerMinY >= planetMinY && pointerMinY <= planetMaxY)))
                {
            		if (!planet.getSlotRenderer().getPlanetName().equals("Sun"))
            		{
                    	this.selectedPlanet = planet;
            		}
                }
            }
            
            final Tessellator var3 = Tessellator.instance;

            if (renderer != null)
            {
                this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture(renderer.getPlanetSprite()));
                renderer.renderSlot(0, var42, var41, planet.getPlanetSize() + (1 / this.zoom * 3F), var3);
                
                if (selectedPlanet != null && planet.getSlotRenderer().getPlanetName().equals(selectedPlanet.getSlotRenderer().getPlanetName()))
                {
                    renderer.renderSlot(0, var42, var41, planet.getPlanetSize() + (1 / this.zoom * 3F), var3);
                }
            }
            
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
        	for (int i = 0; i < GalacticraftCore.mapMoons.size(); i++)
            {
        		IMapPlanet moon = (IMapPlanet) GalacticraftCore.mapMoons.get(String.valueOf(planet) + i);
        		

                int var26b = 0;
                int var27b = 0;
                
                int var42b = 0;
                int var41b = 0;
                
        		if (moon != null)
        		{
    				FMLLog.info("" + moon.getSlotRenderer().getPlanetName());
                    final Map[] posMaps2 = this.computePlanetPos(var42, var41, moon.getDistanceFromCenter() / 2, 2880);
                    
                    if (posMaps2[0] != null && posMaps2[1] != null)
                    {
                    	if (posMaps2[0].get(MathHelper.floor_float(Sys.getTime() / (720F * moon.getStretchValue()) % 2880)) != null && posMaps2[1].get(MathHelper.floor_float(Sys.getTime() / 720F % 2880)) != null)
                    	{
                        	final int x = MathHelper.floor_float((Float) posMaps2[0].get(MathHelper.floor_float((moon.getPhaseShift() + Sys.getTime() / (720F * moon.getStretchValue())) % 2880)));
                        	final int y = MathHelper.floor_float((Float) posMaps2[1].get(MathHelper.floor_float((moon.getPhaseShift() + Sys.getTime() / (720F * moon.getStretchValue())) % 2880)));
                        	
                        	var26b = x;
                        	var27b = y;
                    	}
                    }
                    
                    var42b = var26b;
                    var41b = var27b;

                    width = (int) (moon.getPlanetSize() + (1 / this.zoom * 3F));
                    
                    if (Mouse.isButtonDown(0))
                    {
                    	int pointerMinX = this.width / 2 - 5;
                    	int pointerMaxX = this.width / 2 + 5;
                    	int pointerMinY = this.height / 2 - 5;
                    	int pointerMaxY = this.height / 2 + 5;
                    	int planetMinX = var42b - width;
                    	int planetMaxX = var42b + width;
                    	int planetMinY = var41b - width;
                    	int planetMaxY = var41b + width;
                    	
                    	if (((pointerMaxX >= planetMinX && pointerMinX <= planetMinX) || (pointerMinX <= planetMinX && pointerMaxY >= planetMaxX) || (pointerMinX >= planetMinX && pointerMinX <= planetMaxX))
                    			&& ((pointerMaxY >= planetMinY && pointerMinY <= planetMinY) || (pointerMinY <= planetMinY && pointerMaxY >= planetMaxY) || (pointerMinY >= planetMinY && pointerMinY <= planetMaxY)))
                        {
                        	this.selectedPlanet = moon;
                        }
                    }
                    
                    final IPlanetSlotRenderer moonRenderer = moon.getSlotRenderer();

                    if (moonRenderer != null)
                    {
                        this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture(moonRenderer.getPlanetSprite()));
                        moonRenderer.renderSlot(0, var42b, var41b, (float) (moon.getPlanetSize() + (1 / Math.pow(this.zoom, -2))), var3);
                        
                        if (selectedPlanet != null && moon.getSlotRenderer().getPlanetName().equals(selectedPlanet.getSlotRenderer().getPlanetName()))
                        {
                            moonRenderer.renderSlot(0, var42b, var41b, (float) (moon.getPlanetSize() + (1 / Math.pow(this.zoom, -2))), var3);
                        }
                    }

                    if (selectedPlanet != null && moon.getSlotRenderer().getPlanetName().equals(selectedPlanet.getSlotRenderer().getPlanetName()))
                    {
                    	this.drawInfoBox(var42b, var41b, moon);
                    }
        		}
            }

            this.drawCircles(var10 + var10, var11 + var11);
            
            this.drawAsteroidBelt(var10 + var10, var11 + var11);

            if (!planet.getSlotRenderer().getPlanetName().equals("Sun"))
            {
            }

            if (selectedPlanet != null && planet.getSlotRenderer().getPlanetName().equals(selectedPlanet.getSlotRenderer().getPlanetName()))
            {
            	this.drawInfoBox(var42, var41, planet);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            GL11.glDepthMask(true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        
        GL11.glPopMatrix();
        
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        this.zLevel = 0.0F;
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

//    	this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture());
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/micdoodle8/mods/galacticraft/core/client/gui/gui.png"));
        int var5b = (this.width - this.mc.displayWidth) / 2;
        int var6b = (this.height - this.mc.displayHeight) / 2;
        this.drawTexturedModalRect(this.width / 2 - 5, this.height / 2 - 5, 123, 0, 10, 10);

        final int col = GCCoreUtil.convertTo32BitColor(255, 198, 198, 198);
        final int col2 = GCCoreUtil.convertTo32BitColor(255, 145, 145, 145);
        final int col3 = GCCoreUtil.convertTo32BitColor(255, 33, 33, 33);
        
        Gui.drawRect(0, 					0, 					this.width, 		20, 				col);
        Gui.drawRect(0,	 				this.height - 24, 	this.width, 		this.height,    	col);
        Gui.drawRect(0, 					0, 					10, 				this.height,    	col);
        Gui.drawRect(this.width - 10, 		0, 					this.width, 		this.height, 		col);

        Gui.drawRect(10, 					20, 				this.width - 10, 	22, 				col3);
        Gui.drawRect(10,	 				this.height - 26, 	this.width - 10, 	this.height - 24,   col2);
        Gui.drawRect(10, 					20, 				12, 				this.height - 24,   col3);
        Gui.drawRect(this.width - 12, 		0 + 20, 			this.width - 10, 	this.height - 24, 	col2);
        
        super.drawScreen(par1, par2, par3);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.disableStandardItemLighting();
    }
    
    public void drawCircles(float cx, float cy) 
    {
    	final float theta = (float) (2 * Math.PI / 500); 
    	final float c = (float) Math.cos(theta);
    	final float s = (float) Math.sin(theta);
    	float t;
    	
    	for (final IMapPlanet planet : GalacticraftCore.mapPlanets)
    	{
        	float x = planet.getDistanceFromCenter() / 2F; 
        	float y = 0; 
            
        	GL11.glColor4f(0.0F, 0.0F, 1.0F, 1.0F);
        	
        	GL11.glBegin(GL11.GL_LINE_LOOP); 
        	
        	for(int ii = 0; ii < 500; ii++) 
        	{ 
        		GL11.glVertex2f(x + cx, y + cy);
                
        		t = x;
        		x = c * x - s * y;
        		y = s * t + c * y;
        	} 
        	
        	GL11.glEnd(); 
    	}
    }
    
    public void drawAsteroidBelt(float cx, float cy) 
    {
        Random rand = new Random();
        rand.setSeed((1234));
    	
    	GL11.glColor4f(139 / 255F, 69 / 255F, 19 / 255F, 1.0F);
        
        for (int i = 1; i < 15; i++)
        {
        	final float theta = (float) (2 * Math.PI / (15 * i)); 
        	final float c = (float) Math.cos(theta);
        	final float s = (float) Math.sin(theta);
        	float t;

        	float x;
        	
        	if (i < 8)
        	{
        		x = (float) (4400F + Math.pow(2.5, i));
        	}
        	else
        	{
        		x = (float) (4400F - Math.pow(2.5, 15 - i));
        	}
        	
        	float y = 0; 
        	
        	for(int ii = 0; ii < (15 * i); ii++) 
        	{
        		Tessellator var0 = Tessellator.instance;
        		
        		var0.startDrawingQuads();
        		var0.addVertex(x + cx - 2 + rand.nextInt(50), y + cy + 2 + rand.nextInt(50), -90.0D);
        		var0.addVertex(x + cx + 2 + rand.nextInt(50), y + cy + 2 + rand.nextInt(50), -90.0D);
        		var0.addVertex(x + cx + 2 + rand.nextInt(50), y + cy - 2 + rand.nextInt(50), -90.0D);
        		var0.addVertex(x + cx - 2 + rand.nextInt(50), y + cy - 2 + rand.nextInt(50), -90.0D);
                var0.draw();
        		
        		t = x;
        		x = c * x - s * y;
        		y = s * t + c * y;
        	} 
        }
    }
    
    private void drawInfoBox(int cx, int cy, IMapPlanet planet)
    {
//    	for (final IMapPlanet planet : GalacticraftCore.mapPlanets)
    	{
            final float size = planet.getPlanetSize() / 2F * 1.3F * (this.zoom * 2F);
            
        	this.drawGradientRect(cx + MathHelper.floor_float(planet.getPlanetSize()), cy - 3, cx + MathHelper.floor_float(planet.getPlanetSize()) + 8 * 10, cy + 10, GCCoreUtil.convertTo32BitColor(100, 50, 50, 50), GCCoreUtil.convertTo32BitColor(100, 50, 50, 50));
        	this.fontRenderer.drawStringWithShadow(planet.getSlotRenderer().getPlanetName(), cx + MathHelper.floor_float(planet.getPlanetSize()) + 3, cy - 1, GCCoreUtil.convertTo32BitColor(255, 200, 200, 200));
        	
        	for (int i = 0; i < GCCoreUtil.getPlayersOnPlanet(planet).size(); i++)
        	{
                this.drawGradientRect(cx + MathHelper.floor_float(planet.getPlanetSize()), cy + 10 * (i + 1), cx + MathHelper.floor_float(planet.getPlanetSize()) + 80, cy + 10 * (i + 1) + 10, GCCoreUtil.convertTo32BitColor(255, 50, 50, 50), GCCoreUtil.convertTo32BitColor(255, 50, 50, 50));
            	this.fontRenderer.drawStringWithShadow(String.valueOf(GCCoreUtil.getPlayersOnPlanet(planet).get(i)), cx + MathHelper.floor_float(planet.getPlanetSize() * 2), cy + 1 + 10 * (i + 1), GCCoreUtil.convertTo32BitColor(255, 220, 220, 220));
            	this.width = Math.max(this.width, String.valueOf(GCCoreUtil.getPlayersOnPlanet(planet).get(i)).length());
        	}
    	}
    }
    
    public Map[] computePlanetPos(float cx, float cy, float r, float stretch)
    {
    	final Map mapX = new HashMap();
    	final Map mapY = new HashMap();

    	final float theta = (float) (2 * Math.PI / stretch); 
    	final float c = (float) Math.cos(theta);
    	final float s = (float) Math.sin(theta);
    	float t;

    	float x = r;
    	float y = 0; 
        
    	for(int ii = 0; ii < stretch; ii++) 
    	{ 
    		mapX.put(ii, x + cx);
    		mapY.put(ii, y + cy);
            
    		t = x;
    		x = c * x - s * y;
    		y = s * t + c * y;
    	}
    	
    	return new Map[] {mapX, mapY};
    }

    @Override
	public boolean doesGuiPauseGame()
    {
        return true;
    }
    
    private void zoom(float f)
    {
        final ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        final int x = var5.getScaledWidth();
        final int y = var5.getScaledHeight();
        
        GL11.glTranslatef(x / 2, y / 2, 0);
        GL11.glScalef(f, f, 0);
        GL11.glTranslatef(-(x / 2), -(y / 2), 0);
    }

    public void drawBlackBackground()
    {
        final ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        final int var6 = var5.getScaledWidth();
        final int var7 = var5.getScaledHeight();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/micdoodle8/mods/galacticraft/core/client/backgrounds/black.png"));
        final Tessellator var3 = Tessellator.instance;
        var3.startDrawingQuads();
        var3.addVertexWithUV(0.0D, var7, -90.0D, 0.0D, 1.0D);
        var3.addVertexWithUV(var6, var7, -90.0D, 1.0D, 1.0D);
        var3.addVertexWithUV(var6, 0.0D, -90.0D, 1.0D, 0.0D);
        var3.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        var3.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    private void drawPanorama2(float par1)
    {
        final Tessellator var4 = Tessellator.instance;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        final byte var5 = 1;
        

        for (int var6 = 0; var6 < var5 * var5; ++var6)
        {
            GL11.glPushMatrix();
            final float var7 = ((float)(var6 % var5) / (float)var5 - 0.5F) / 128.0F;
            final float var8 = ((float)(var6 / var5) / (float)var5 - 0.5F) / 128.0F;
            final float var9 = 0.0F;
            
        	float mY;
        	float mX;

    	  	if (Mouse.getY() < this.height)
    	  	{
    	  		mY = (-this.height + Mouse.getY()) / 100F;
    	  	}
    	  	else
    	  	{
      			mY = (-this.height + Mouse.getY()) / 100F;
      		}
          
      		mX = (this.width - Mouse.getX()) / 100F;
      		
            GL11.glTranslatef(var7 - mX / (50F / this.zoom), var8 - mY / (50F / this.zoom), var9 + 0.5F);

            final float i = MathHelper.clamp_float(7 * (this.zoom / 1.1F), 3F, 9F);
            
            GL11.glScalef(i, i, i);

            for (int var10 = 0; var10 < 9; ++var10)
            {
                GL11.glPushMatrix();

                if (var10 == 1)
                {
                	GL11.glTranslatef(1.96F, 0.0F, 0.0F);
                }

                if (var10 == 2)
                {
                	GL11.glTranslatef(-1.96F, 0.0F, 0.0F);
                }

                if (var10 == 3)
                {
                	GL11.glTranslatef(0.0F, 1.96F, 0.0F);
                }

                if (var10 == 4)
                {
                	GL11.glTranslatef(0.0F, -1.96F, 0.0F);
                }

                if (var10 == 5)
                {
                	GL11.glTranslatef(-1.96F, -1.96F, 0.0F);
                }

                if (var10 == 6)
                {
                	GL11.glTranslatef(-1.96F, 1.96F, 0.0F);
                }

                if (var10 == 7)
                {
                	GL11.glTranslatef(1.96F, -1.96F, 0.0F);
                }

                if (var10 == 8)
                {
                	GL11.glTranslatef(1.96F, 1.96F, 0.0F);
                }

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/micdoodle8/mods/galacticraft/core/client/backgrounds/bg3.png"));
                var4.startDrawingQuads();
                var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
                var4.addVertexWithUV(-1.0D, -1.0D, 1.0D, 0.0F + 1, 0.0F + 1);
                var4.addVertexWithUV(1.0D, -1.0D, 1.0D, 1.0F - 1, 0.0F + 1);
                var4.addVertexWithUV(1.0D, 1.0D, 1.0D, 1.0F - 1, 1.0F - 1);
                var4.addVertexWithUV(-1.0D, 1.0D, 1.0D, 0.0F + 1, 1.0F - 1);
                var4.draw();
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
        }

        var4.setTranslation(0.0D, 0.0D, 0.0D);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void drawPanorama(float par1)
    {
        final Tessellator var4 = Tessellator.instance;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        final byte var5 = 1;
        

        for (int var6 = 0; var6 < var5 * var5; ++var6)
        {
            GL11.glPushMatrix();
            final float var7 = ((float)(var6 % var5) / (float)var5 - 0.5F) / 64.0F;
            final float var8 = ((float)(var6 / var5) / (float)var5 - 0.5F) / 64.0F;
            final float var9 = 0.0F;
            
        	float mY;
        	float mX;

    	  	if (Mouse.getY() < this.height)
    	  	{
    	  		mY = (-this.height + Mouse.getY()) / 100F;
    	  	}
    	  	else
    	  	{
      			mY = (-this.height + Mouse.getY()) / 100F;
      		}
          
      		mX = (this.width - Mouse.getX()) / 100F;
      		
            GL11.glTranslatef(var7 - mX / (200F / this.zoom), var8 - mY / (200F / this.zoom), var9 + 0.5F);

            final float i = MathHelper.clamp_float(7 * (this.zoom / 1.1F), 3F, 9F);
            
            GL11.glScalef(i / 3F, i / 3F, i / 3F);
            
            GL11.glRotatef(MathHelper.sin(par1 / 1000.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-par1 * 0.005F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(41, 0, 0, 1);

            for (int var10 = 0; var10 < 6; ++var10)
            {
                GL11.glPushMatrix();
                

                if (var10 == 1)
                {
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var10 == 2)
                {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var10 == 3)
                {
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var10 == 4)
                {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var10 == 5)
                {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/micdoodle8/mods/galacticraft/core/client/backgrounds/bg3.png"));
                var4.startDrawingQuads();
                var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
                var4.addVertexWithUV(-1.0D, -1.0D, 1.0D, 0.0F + 1, 0.0F + 1);
                var4.addVertexWithUV(1.0D, -1.0D, 1.0D, 1.0F - 1, 0.0F + 1);
                var4.addVertexWithUV(1.0D, 1.0D, 1.0D, 1.0F - 1, 1.0F - 1);
                var4.addVertexWithUV(-1.0D, 1.0D, 1.0D, 0.0F + 1, 1.0F - 1);
                var4.draw();
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
        }

        var4.setTranslation(0.0D, 0.0D, 0.0D);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void rotateAndBlurSkybox()
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/micdoodle8/mods/galacticraft/core/client/backgrounds/bg3.png"));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColorMask(true, true, true, false);
        GL11.glPushMatrix();
        GL11.glPopMatrix();
        GL11.glColorMask(true, true, true, true);
    }
    
    public void renderSkybox(float par1)
    {
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glPushMatrix();
        GL11.glScalef(1.0F, 0.0F, 1.0F);
        this.drawPanorama(par1);
        this.drawPanorama2(par1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        this.rotateAndBlurSkybox();
        final Tessellator var4 = Tessellator.instance;
        var4.startDrawingQuads();
        final float var5 = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
        final float var6 = this.height * var5 / 256.0F;
        final float var7 = this.width * var5 / 256.0F;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        final int var8 = this.width;
        final int var9 = this.height;
        var4.addVertexWithUV(0.0D, var9, this.zLevel, 0.5F - var6, 0.5F + var7);
        var4.addVertexWithUV(var8, var9, this.zLevel, 0.5F - var6, 0.5F - var7);
        var4.addVertexWithUV(var8, 0.0D, this.zLevel, 0.5F + var6, 0.5F - var7);
        var4.addVertexWithUV(0.0D, 0.0D, this.zLevel, 0.5F + var6, 0.5F + var7);
        var4.draw();
        GL11.glPopMatrix();
    }
}