package com.android.angle;

import javax.microedition.khronos.opengles.GL10;


/**
 * Angle sprites engine
 * Can operate in direct mode (sprites) or in indirect mode (references)
 * If MaxReferences>0 then indirect mode is selected
 * 
 * @author Ivan Pajuelo
 *
 */
public class AngleSpritesEngine extends AngleAbstractEngine
{
	private int mMaxSprites;
	private AngleAbstractSprite[] mSprites;
	private int mSpritesCount;
	private int mMaxReferences;
	private AngleVisualObject[] mReferences;
	private int mReferencesCount;

	/**
	 * 
	 * @param maxSprites	Max sprites available in engine
	 * @param maxReferences Max references available in engine. If >0 references will be used instead of sprites.
	 */
	public AngleSpritesEngine (int maxSprites, int maxReferences)
	{
		mMaxSprites=maxSprites;
		mMaxReferences=maxReferences;
		mSpritesCount = 0;
		mReferencesCount = 0;
		mSprites = new AngleAbstractSprite[mMaxSprites];
		mReferences = new AngleVisualObject[mMaxReferences];
	}
	
	/**
	 * Adds a sprite
	 * @param sprite Sprite to be added
	 */
	public void addSprite(AngleAbstractSprite sprite)
	{
		if (mSpritesCount<mMaxSprites)
		{
			for (int s=0;s<mSpritesCount;s++)
				if (mSprites[mSpritesCount] == sprite)
					return;
			mSprites[mSpritesCount++] = sprite;
		}
	}

	/**
	 * Removes a sprite
	 * @param sprite Sprite to be removed
	 */
	public void removeSprite(AngleAbstractSprite sprite)
	{
		int r;
		
		for (r = 0; r < mSpritesCount; r++)
			if (mSprites[r] == sprite)
				break;

		if (r < mSpritesCount)
		{
			mSpritesCount--;
			for (int d=r;d<mSpritesCount;d++)
				mSprites[d]=mSprites[d+1];
			mSprites[mSpritesCount]=null;
		}
	}

	/**
	 * Adds a reference
	 * @param reference Reference to be added
	 */
	public void addReference(AngleAbstractSpriteReference reference)
	{
		if (mReferencesCount<mMaxReferences)
		{
			for (int s=0;s<mReferencesCount;s++)
				if (mReferences[mReferencesCount] == reference)
					return;
			reference.afterAdd();
			mReferences[mReferencesCount++] = reference;
		}
	}

	/**
	 * Removes a reference
	 * @param reference Reference to be removed
	 */
	public void removeRefernece(AngleAbstractSpriteReference reference)
	{
		int r;
		
		for (r = 0; r < mReferencesCount; r++)
			if (mReferences[r] == reference)
				break;

		if (r < mReferencesCount)
		{
			mReferencesCount--;
			for (int d=r;d<mReferencesCount;d++)
				mReferences[d]=mReferences[d+1];
			mReferences[mReferencesCount]=null;
		}
	}

	public void drawFrame(GL10 gl)
	{
		if (!AngleTextureEngine.hasChanges)
		{
			if (mMaxReferences>0)
			{
				for (int s = 0; s < mReferencesCount; s++)
					mReferences[s].draw(gl);
			}
			else
			{
				for (int s = 0; s < mSpritesCount; s++)
					mSprites[s].draw(gl);
			}
		}
	}

	public void loadTextures(GL10 gl)
	{
		for (int s = 0; s < mSpritesCount; s++)
			mSprites[s].loadTexture();
	}

	public void afterLoadTextures(GL10 gl)
	{
		for (int s = 0; s < mSpritesCount; s++)
			mSprites[s].afterLoadTexture();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	public void onDestroy(GL10 gl)
	{
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		for (int s = 0; s < mSpritesCount; s++)
			mSprites[s] = null;
		mSpritesCount = 0;
		for (int s = 0; s < mReferencesCount; s++)
			mReferences[s] = null;
		mReferencesCount = 0;
	}
}