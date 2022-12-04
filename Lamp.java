import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

/**
 * Class represents a lamp
 * in the scene
 */

public class Lamp {

	private Model baseCube, armSphere, jointSphere,headCube;
	private SGNode lampRoot;
	private Texture[] textures;
	private double startTime;


	private void loadTextures(GL3 gl) {
		textures = new Texture[3];
		textures[0] = TextureLibrary.loadTexture(gl, "textures/tabletop.jpg");
		textures[1] = TextureLibrary.loadTexture(gl, "textures/table_legs.jpg");
		textures[2] = TextureLibrary.loadTexture(gl, "textures/soil.jpg");
	}

	public Lamp(GL3 gl, Camera camera, Light light) {

		loadTextures(gl);
		startTime = getSeconds();


		// Define our base & head info
		Mesh cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
		Shader shader = new Shader(gl, "shaders/table_vs.glsl", "shaders/table_fs.glsl");
		Material baseMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

		// Define the arms & joints
		Mesh sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
		Material armMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

		// Create our models
		baseCube = new Model(gl, camera, light, shader, baseMaterial, new Mat4(1), cubeMesh, textures[1]);
		headCube = new Model(gl, camera, light, shader, baseMaterial, new Mat4(1), cubeMesh, textures[1]);
		armSphere = new Model(gl, camera, light, shader, armMaterial, new Mat4(1), sphereMesh, textures[0]);
		jointSphere = new Model(gl, camera, light, shader, armMaterial, new Mat4(1), sphereMesh, textures[2]);

		// ================== Transformations ====================

		float baseWidth = 1.2f;
		float baseDepth = 0.5f;
		float baseHeight = 0.25f;

		float armWidth = 0.35f;
		float armHeight = 2.5f;
		float armDepth = 0.35f;

		float jointRadius = 0.4f;

		float headWidth = 0.8f;
		float headDepth = 0.3f;
		float headHeight = 0.3f;



		// Create root
		lampRoot = new NameNode("table");

		// Move the lamp into position
		TransformNode lampMoveTransform = new TransformNode("Move the lamp", Mat4Transform.translate(-3,0,0));


		// Create the base of the lamp
		NameNode base = new NameNode("base");

			/*
			 - Translate our cube to the surface
			 - Scale it into a base
			 - move into position
			 */
			Mat4 m = Mat4.multiply(Mat4Transform.scale(baseWidth,baseHeight,baseDepth), Mat4Transform.translate(0,0.5f,0));
				TransformNode baseTransform = new TransformNode("base transform", m);
					ModelNode baseShape = new ModelNode("Base of lamp", baseCube);

		// Create the arm of the lamp
		NameNode arm1 = new NameNode("arm 1");

			/*
			 - Translate our sphere to the surface
			 - Scale it into an arm
			 - move into position
			 */
			m = Mat4.multiply(Mat4Transform.scale(armWidth,armHeight,armDepth), Mat4Transform.translate(0,0.5f,0));
				TransformNode arm1Transform = new TransformNode("arm 1 transform", m);
					ModelNode armShape = new ModelNode("Arm 1 of lamp", armSphere);

		// Create the joint of the lamp
		NameNode joint1 = new NameNode("Joint 1");

			/*
			 - Translate our sphere to the surface
			 - Scale it into an arm
			 - move into position
			 */
			m = Mat4.multiply(Mat4Transform.scale(jointRadius,jointRadius,jointRadius), Mat4Transform.translate(0,0.5f,0));
			m = Mat4.multiply(Mat4Transform.translate(0,armHeight-(jointRadius/2),0), m);
				TransformNode joint1Transform = new TransformNode("joint 1 transform", m);
					ModelNode joint1Shape = new ModelNode("Joint 1 of lamp", jointSphere);

		// Create the arm of the lamp
		NameNode arm2 = new NameNode("arm 2");

			/*
			 - Translate our sphere to the surface
			 - Scale it into an arm
			 - move into position
			 */
			m = Mat4.multiply(Mat4Transform.scale(armWidth,armHeight,armDepth), Mat4Transform.translate(0,0.5f,0));
			m = Mat4.multiply(Mat4Transform.translate(0,armHeight,0), m);
			TransformNode arm2Transform = new TransformNode("arm 2 transform", m);
				ModelNode arm2Shape = new ModelNode("Arm 2 of lamp", armSphere);


		// Create the arm of the lamp
		NameNode head = new NameNode("Head");

			/*
			 - Translate our cube to the surface
			 - Scale it into an arm
			 - move into position
			 */
			m = Mat4.multiply(Mat4Transform.scale(headWidth,headHeight,headDepth), Mat4Transform.translate(0,0.5f,0));
			m = Mat4.multiply(Mat4Transform.translate(0,armHeight*2-(jointRadius/2),0), m);
				TransformNode headTransform = new TransformNode("Head transform", m);
				ModelNode headShape = new ModelNode("Arm 2 of lamp", headCube);


		// Add nodes hierarchy
		lampRoot.addChild(lampMoveTransform);
			lampMoveTransform.addChild(base);
				base.addChild(baseTransform);
					baseTransform.addChild(baseShape);
			lampMoveTransform.addChild(arm1);
				arm1.addChild(arm1Transform);
					arm1Transform.addChild(armShape);
				arm1.addChild(joint1);
					joint1.addChild(joint1Transform);
							joint1Transform.addChild(joint1Shape);
					joint1.addChild(arm2);
						arm2.addChild(arm2Transform);
							arm2Transform.addChild(arm2Shape);
						arm2.addChild(head);
							head.addChild(headTransform);
								headTransform.addChild(headShape);




		lampRoot.update();  // IMPORTANT - don't forget this

	}

	public SGNode getRoot(){
		return lampRoot;
	}


	private double getSeconds() {
		return System.currentTimeMillis()/1000.0;
	}

	public void dispose(GL3 gl) {
		baseCube.dispose(gl);
		jointSphere.dispose(gl);
		armSphere.dispose(gl);
	}
}