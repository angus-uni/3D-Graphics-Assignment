import gmaths.Vec3;

/**
 * A material that changes
 * colour
 */
public class MagicMaterial extends Material{

	public MagicMaterial(Vec3 ambient, Vec3 diffuse, Vec3 specular, float shininess) {
		super(ambient, diffuse, specular, shininess);
	}

	private double getSeconds() {
		return System.currentTimeMillis()/1000.0;
	}

	private Vec3 transformColour(Vec3 colourVector){
		double seconds = getSeconds();
		float rScalar = (float) Math.abs(Math.sin(seconds));
		float gScalar = (float) Math.abs(Math.cos(seconds));
		float bScalar = rScalar > gScalar ? rScalar/gScalar : gScalar/rScalar;
		bScalar = (float) Math.abs(Math.sin(bScalar));

		return new Vec3(colourVector.x*rScalar,colourVector.y*gScalar,colourVector.z*bScalar);
	}

	public Vec3 getAmbient(){
		return transformColour(ambient);
	}

	public Vec3 getDiffuse(){
		return transformColour(diffuse);
	}

	public Vec3 getSpecular(){
		return transformColour(specular);
	}
}
