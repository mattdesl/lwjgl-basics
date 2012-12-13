package mdesl.graphics;

/**
 * An Image is a base type which Texture and TextureRegion implement. To create
 * an image, you would initialize a new Texture. To render the image, you would 
 * then use a SpriteBatch.
 * 
 * @author davedes 
 */
public interface ITexture {

	public Texture getTexture();
	public float getWidth();
	public float getHeight();
	public float getU();
	public float getV();
	public float getU2();
	public float getV2();
}