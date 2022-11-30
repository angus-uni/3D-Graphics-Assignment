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

public class Table {

	private Model tableCube, legCube;
	private SGNode tableRoot;

	public Table(GL3 gl, Camera camera, Light light, int[] topTexture, int[] legTexture) {

		// Specify the size of our table
		float legHeight = 2f;
		float legWidth = 0.2f;
		float legDepth = 0.2f;

		float topHeight = 0.2f;
		float topWidth = 3f;
		float topDepth = 3f;

		// Define our table info
		Mesh cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
		Shader tableShader = new Shader(gl, "shaders/table_vs.glsl", "shaders/table_fs.glsl");
		Material tableMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

		// Generate a matrix of a basic scale & transformation
		Mat4 tableMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));

		// Create our models
		tableCube = new Model(gl, camera, light, tableShader, tableMaterial, tableMatrix, cubeMesh, topTexture);
		legCube = new Model(gl, camera, light, tableShader, tableMaterial, tableMatrix, cubeMesh, legTexture);

		// ================== Transformations ====================

		// Create root
		tableRoot = new NameNode("table");

		// Create the top of our table & move it to the top
		NameNode top = new NameNode("top");

			/*
			 - Translate our cube to the surface
			 - Scale it into a table-top
			 - Translate it to above the table legs
			 */
			Mat4 m = Mat4.multiply(Mat4Transform.scale(topWidth,topHeight,topDepth), Mat4Transform.translate(0,0.5f,0));
			m = Mat4.multiply(Mat4Transform.translate(0,legHeight,0), m);
				TransformNode topTransform = new TransformNode("top transform", m);
				ModelNode topShape = new ModelNode("Cube(top of table)", tableCube);

		// Create the legs of our table
		NameNode leg1 = new NameNode("leg 1");

			/*
			 - Translate our cube to the surface
			 - Scale it into a leg
			 - Move it to the corner of our table
			 */
			m = Mat4.multiply(Mat4Transform.scale(legWidth,legHeight,legDepth), Mat4Transform.translate(0,0.5f,0));
			m = Mat4.multiply(Mat4Transform.translate(-((topWidth/2)-(legWidth/2)), 0, (topDepth/2)-(legDepth/2)), m);
				TransformNode leg1Transform = new TransformNode("leg 1 transform", m);
				ModelNode legShape = new ModelNode("Cube (leg 1)", legCube);

		tableRoot.addChild(top);
				top.addChild(topTransform);
					topTransform.addChild(topShape);
		tableRoot.addChild(leg1);
			leg1.addChild(leg1Transform);
				leg1Transform.addChild(legShape);

		tableRoot.update();  // IMPORTANT - don't forget this

	}

	public void render(GL3 gl) {
		tableRoot.draw(gl);
	}

	public void dispose(GL3 gl) {
		tableCube.dispose(gl);
		legCube.dispose(gl);
	}
}