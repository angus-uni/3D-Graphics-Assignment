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


	enum Size {
		SMALL(6),
		MEDIUM(9),
		LARGE(12);

		private final int height;

		Size(int height) {
			this.height = height;
		}

		public int getHeight() {
			return height;
		}
	}


	private Model baseCube, armSphere, jointSphere, headCube;
	private SGNode lampRoot;
	private Texture[] textures;
	private PointLight headLight;
	private TransformNode jointRotate, headRotate;


	private void loadTextures(GL3 gl) {
		textures = new Texture[3];
		textures[0] = TextureLibrary.loadTexture(gl, "textures/tabletop.jpg");
		textures[1] = TextureLibrary.loadTexture(gl, "textures/table_legs.jpg");
		textures[2] = TextureLibrary.loadTexture(gl, "textures/soil.jpg");
	}

	public PointLight getPointLight() {
		return headLight;
	}

	public Lamp(GL3 gl, Camera camera, Light[] worldLights, Shader multiShader, Size size) {

		loadTextures(gl);

		// Define our base & head info
		Mesh cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
		Material baseMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

		// Create the light
		headLight = new PointLight(gl);
		headLight.setCamera(camera);

		// Define the arms & joints
		Mesh sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
		Material armMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

		// Create our models
		baseCube = new Model(gl, camera, worldLights, multiShader, baseMaterial, new Mat4(1), cubeMesh, textures[1]);
		headCube = new Model(gl, camera, worldLights, multiShader, baseMaterial, new Mat4(1), cubeMesh, textures[1]);
		armSphere = new Model(gl, camera, worldLights, multiShader, armMaterial, new Mat4(1), sphereMesh, textures[0]);
		jointSphere = new Model(gl, camera, worldLights, multiShader, armMaterial, new Mat4(1), sphereMesh, textures[2]);

		// ================== Transformations ====================

		int height = size.getHeight();

		float baseWidth = 0.2f*height;
		float baseHeight = 0.04f*height;
		float baseDepth = 0.083f*height;

		float armWidth = 0.058f*height;
		float armHeight = 0.41f*height;
		float armDepth = 0.058f*height;

		float jointRadius = 0.1f*height;

		float headWidth = 0.13f*height;
		float headHeight = 0.05f*height;
		float headDepth = 0.05f*height;

		float lightWidth = 0.033f*height;
		float lightHeight = 0.033f*height;
		float lightDepth = 0.033f*height;


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
				TransformNode makeBase = new TransformNode("make the base for the lamp", m);
					ModelNode baseShape = new ModelNode("Base of lamp", baseCube);

		// Create the arm of the lamp
		NameNode arm1 = new NameNode("arm 1");

			/*
			 - Translate our sphere to the surface
			 - Scale it into an arm
			 - move into position
			 */

			m = Mat4Transform.translate(0,baseHeight/2,0);
			TransformNode positionArm1 = new TransformNode("Move arm 1 into position", m);

			m = Mat4.multiply(Mat4Transform.scale(armWidth,armHeight,armDepth), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeArm1 = new TransformNode("Make arm 1", m);
					ModelNode arm1Shape = new ModelNode("Arm 1 of lamp", armSphere);

		// Create the joint of the lamp
		NameNode joint1 = new NameNode("Joint 1");

			/*
			 - Translate our sphere to the surface
			 - Scale it into an arm
			 - move into position
			 */

			m = Mat4Transform.translate(0,armHeight-(jointRadius/2),0);
			TransformNode positionJoint = new TransformNode("Move joint 1 into position", m);

			// Rotate transform
			jointRotate = new TransformNode("Rotate the joint",Mat4Transform.rotateAroundZ(90));

			m = Mat4.multiply(Mat4Transform.scale(jointRadius,jointRadius,jointRadius), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeJoint = new TransformNode("Create joint 1", m);
					ModelNode joint1Shape = new ModelNode("Joint 1 of lamp", jointSphere);



		// Create the arm of the lamp
		NameNode arm2 = new NameNode("arm 2");

			/*
			 - Translate our sphere to the surface
			 - Scale it into an arm
			 - move into position
			 */

		m = Mat4Transform.translate(0,0,0);
		TransformNode positionArm2 = new TransformNode("Move arm into position", m);

		m = Mat4.multiply(Mat4Transform.scale(armWidth,armHeight,armDepth), Mat4Transform.translate(0,0.5f,0));
			TransformNode makeArm2 = new TransformNode("Make arm 2", m);
				ModelNode arm2Shape = new ModelNode("Arm 2 of lamp", armSphere);


		// Create the head of the lamp
		NameNode head = new NameNode("Head");

			/*
			 - Translate our cube to the surface
			 - Scale it into an arm
			 - move into position
			 */

			m = Mat4Transform.translate(0,armHeight,0);
			TransformNode positionHead = new TransformNode("Move head into position", m);

			headRotate = new TransformNode("Tilt the head of the lamp",Mat4Transform.rotateAroundZ(15));

			m = Mat4.multiply(Mat4Transform.scale(headWidth,headHeight,headDepth), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeHead = new TransformNode("Make the head", m);
				ModelNode headShape = new ModelNode("Head of lamp", headCube);

		// Create the light for the lamp
		NameNode lampLight = new NameNode("Lamp light");

			// Move light into position
			m = Mat4Transform.translate((headWidth/2)+(lightWidth/2),(headHeight/2)-(lightHeight/2),0);
			TransformNode positionLight = new TransformNode("Move light into position", m);

			m = Mat4.multiply(Mat4Transform.scale(lightWidth,lightHeight,lightDepth), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeLight = new TransformNode("Make the light for the lamp", m);
				LightNode lightShape = new LightNode("Light of lamp", headLight);




		// Add nodes hierarchy
		lampRoot.addChild(lampMoveTransform);
			lampMoveTransform.addChild(base);
				base.addChild(makeBase);
					makeBase.addChild(baseShape);
				base.addChild(positionArm1);
					positionArm1.addChild(arm1);
						arm1.addChild(makeArm1);
							makeArm1.addChild(arm1Shape);
						arm1.addChild(positionJoint);
							positionJoint.addChild(jointRotate);
							jointRotate.addChild(joint1);
								joint1.addChild(makeJoint);
									makeJoint.addChild(joint1Shape);
								joint1.addChild(positionArm2);
									positionArm2.addChild(arm2);
										arm2.addChild(makeArm2);
											makeArm2.addChild(arm2Shape);
										arm2.addChild(positionHead);
											positionHead.addChild(headRotate);
												headRotate.addChild(head);
													head.addChild(makeHead);
														makeHead.addChild(headShape);
													head.addChild(positionLight);
														positionLight.addChild(lampLight);
															lampLight.addChild(makeLight);
																makeLight.addChild(lightShape);

		lampRoot.update();  // IMPORTANT - don't forget this

	}

	public SGNode getRoot(){
		return lampRoot;
	}

	public void move(double elapsedTime){
		float rotateAngle = -(float)(Math.sin(elapsedTime)*25);
		jointRotate.setTransform(Mat4Transform.rotateAroundZ(rotateAngle));
		headRotate.setTransform(Mat4Transform.rotateAroundZ(-rotateAngle));
		lampRoot.update();
	}

	public void dispose(GL3 gl) {
		baseCube.dispose(gl);
		jointSphere.dispose(gl);
		armSphere.dispose(gl);
	}
}