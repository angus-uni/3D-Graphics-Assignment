import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

/**
 * Class represents the table
 * in the scene
 */

public class Egg {

	private Model eggModel;

	public Egg(GL3 gl, Camera camera, Light light, int[] eggTexture, int[] eggSpecular) {

		// Specify the size of our egg
		float height = 4;
		float width = 2;
		float depth = 2;

		// Define our egg info
		Mesh sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
		Shader eggShader = new Shader(gl, "shaders/egg_vs.glsl", "shaders/egg_fs.glsl");
		Material eggMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

		// Move egg to surface and scale
		Mat4 m = Mat4.multiply(Mat4Transform.scale(width,height,depth), Mat4Transform.translate(0,0.5f,0));

		// Create our models
		eggModel = new Model(gl, camera, light, eggShader, eggMaterial, m, sphereMesh, eggTexture, eggSpecular);


	}

	public Model getEggModel(){
		return eggModel;
	}

	public void render(GL3 gl) {
		eggModel.render(gl);
	}

	public void dispose(GL3 gl) {
		eggModel.dispose(gl);
	}
}