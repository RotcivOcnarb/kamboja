package com.mygdx.game.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

/** A helper class that manager multiple FrameBuffer draws using a stack.
 * Mostly used when some objects need to render in frame buffer when already rendering in a frame buffer
 * kind of recursively
 * 
 * @author Rotciv
 *
 */

public class FrameBufferStack {

	static ArrayList<FrameBuffer> frame;

	static Texture lastTexture;
	
	/** Begins drawing at a new framebuffer. If there was already a frame buffer beign drawn,
	 * it stops drawing in that one, and starts drawing in a new one. When this frame buffer
	 * calls the end(); method, it goes back to drawing from the last frame buffed started before this.
	 * (if there is any)
	 * 
	 * @param frame2 the new FrameBuffer to be drawn
	 */
	public static void begin(FrameBuffer frame2) {
		if(frame == null) {
			frame = new ArrayList<FrameBuffer>();
		}
		frame.add(frame2);
		if(frame.size() > 1) {
			frame.get(frame.size() - 2).end();
		}
		frame.get(frame.size() - 1).begin();
	}
	
	/** Finished drawing to the current binded frame buffer, returning to the last frame buffer drawn 
	 * (if there is any), and binds the texture to the {@code getTexture()} method
	 * 
	 */
	public static void end() {
		frame.get(frame.size() - 1).end();
		lastTexture = frame.get(frame.size() - 1).getColorBufferTexture();
		frame.remove(frame.size() - 1);
		if(frame.size() > 0) {
			frame.get(frame.size() - 1).begin();
		}
	}
	
	/** Returns the texture of the last frameBuffer ended
	 * 
	 * @return
	 */
	public static Texture getTexture() {
		return lastTexture;
	}
	
}
