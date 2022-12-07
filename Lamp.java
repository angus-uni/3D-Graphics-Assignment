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
 * in the scene, the lamps
 * in my scene are supposed
 * to be snails with two moving eyes
 * and a big shell on it's back
 * @author Angus Goody
 */

public class Lamp {

	/**
	 * An enum to store
	 * the sizes of available
	 * lamps
	 */
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



	private Model baseCube, armSphere, jointSphere, headCube, eyeSphere, eyeStemSphere, shellSphere;
	private SGNode lampRoot;
	private Texture[] textures;
	private SpotLight headLight;
	private TransformNode jointRotate, headRotate, baseRotate;
	private TransformNode[] eyeRotateNodes, positionEyeStems;
	private Poses poses;


	/**
	 * Load the textures for the lamp, we reuse
	 * some textures from the table
	 * @param gl
	 */
	private void loadTextures(GL3 gl) {
		textures = new Texture[3];
		textures[0] = TextureLibrary.loadTexture(gl, "textures/scales.jpg");
		textures[1] = TextureLibrary.loadTexture(gl, "textures/table_legs.jpg");
		textures[2] = TextureLibrary.loadTexture(gl, "textures/shell.jpg");
	}

	public SpotLight getSpotLight() {
		return headLight;
	}

	/**
	 * The Lamp class, (A snail)
	 * @param gl
	 * @param camera - Camera
	 * @param worldLights - world lights for our scene
	 * @param multiShader - The multi light shader
	 * @param size - Enum representing the size this lamp should be
	 * @param initialPosition - A matrix that defines the initial position of the lamp
	 * @param beamEquation - The equation for the spotlight
	 */

	public Lamp(GL3 gl, Camera camera, Light[] worldLights, Shader multiShader, Size size, Mat4 initialPosition, Poses poses) {
		this(gl,camera,worldLights,multiShader,size,initialPosition,poses,new Vec3(0.22f, 0.25f, 1));
	}

	public Lamp(GL3 gl, Camera camera, Light[] worldLights, Shader multiShader, Size size, Mat4 initialPosition, Poses poses,
	            Vec3 beamEquation) {

		loadTextures(gl);
		this.poses = poses;

		// Define our base & head info
		Mesh cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
		Material baseMaterial = new Material();

		// Define the arms & joints
		Mesh sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
		Material armMaterial = new Material();
		Material eyeMaterial = new MagicMaterial(new Vec3(0.2f), new Vec3(1), new Vec3(1), 1);

		// Create our models
		baseCube = new Model(gl, camera, worldLights, multiShader, baseMaterial, new Mat4(1), cubeMesh, textures[1]);
		headCube = new Model(gl, camera, worldLights, multiShader, baseMaterial, new Mat4(1), cubeMesh, textures[1]);
		armSphere = new Model(gl, camera, worldLights, multiShader, armMaterial, new Mat4(1), sphereMesh, textures[0]);
		jointSphere = new Model(gl, camera, worldLights, multiShader, armMaterial, new Mat4(1), sphereMesh, textures[0]);

		eyeSphere = new Model(gl, camera, worldLights, multiShader, eyeMaterial, new Mat4(1), sphereMesh, textures[0]);
		eyeStemSphere = new Model(gl, camera, worldLights, multiShader, armMaterial, new Mat4(1), sphereMesh, textures[0]);
		shellSphere = new Model(gl, camera, worldLights, multiShader, armMaterial, new Mat4(1), sphereMesh, textures[2]);


		// Create the light
		headLight = new SpotLight(gl, 12.5f, 16f, beamEquation);
		headLight.setCamera(camera);

		// ================== Transformations ====================

		// All measurements are based on the enum size
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

		float eyeRadius = headDepth;
		float eyeStemWidth = armWidth*0.2f;
		float eyeStemHeight = armHeight*0.1f;
		float eyeStemDepth = armDepth*0.2f;

		float shellRadius = 3*jointRadius;

		// Create root
		lampRoot = new NameNode("table");

		// Move the lamp into position
		TransformNode lampMoveTransform = new TransformNode("Move the lamp", initialPosition);


		// Create the base of the lamp
		NameNode base = new NameNode("base");

			baseRotate = new TransformNode("Rotate the arm at the base",Mat4Transform.rotateAroundY(0));


			Mat4 m = Mat4.multiply(Mat4Transform.scale(baseWidth,baseHeight,baseDepth), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeBase = new TransformNode("make the base for the lamp", m);
					ModelNode baseShape = new ModelNode("Base of lamp", baseCube);

		// Create the arm of the lamp
		NameNode arm1 = new NameNode("arm 1");

			// Move into position
			m = Mat4Transform.translate(0,baseHeight/2,0);
			TransformNode positionArm1 = new TransformNode("Move arm 1 into position", m);

			// Construct the arm
			m = Mat4.multiply(Mat4Transform.scale(armWidth,armHeight,armDepth), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeArm1 = new TransformNode("Make arm 1", m);
					ModelNode arm1Shape = new ModelNode("Arm 1 of lamp", armSphere);

		// Create the joint of the lamp
		NameNode joint1 = new NameNode("Joint 1");

			// Move into position
			m = Mat4Transform.translate(0,armHeight-(jointRadius/2),0);
			TransformNode positionJoint = new TransformNode("Move joint 1 into position", m);

			// Create a rotation node so we can rotate the joint
			jointRotate = new TransformNode("Rotate the joint",Mat4Transform.rotateAroundZ(0));

			// Build the actual joint sphere
			m = Mat4.multiply(Mat4Transform.scale(jointRadius,jointRadius,jointRadius), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeJoint = new TransformNode("Create joint 1", m);
					ModelNode joint1Shape = new ModelNode("Joint 1 of lamp", jointSphere);

		// Create the shell of the snail
		NameNode shell = new NameNode("Shell");

			// Move the shell into position (It should merge around the second arm)
			m = Mat4Transform.translate(-shellRadius/2,0,0);
			TransformNode positionShell = new TransformNode("Move shell into position", m);

			// Build the shell
			m = Mat4.multiply(Mat4Transform.scale(shellRadius, shellRadius, shellRadius), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeShell = new TransformNode("Make the shell", m);
				ModelNode shellShape = new ModelNode("Shell of the lamp", shellSphere);


		// Create the arm of the lamp
		NameNode arm2 = new NameNode("arm 2");

			// Move the second arm of the lamp into position
			m = Mat4Transform.translate(0,0,0);
			TransformNode positionArm2 = new TransformNode("Move arm into position", m);

			// Build the arm
			m = Mat4.multiply(Mat4Transform.scale(armWidth,armHeight,armDepth), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeArm2 = new TransformNode("Make arm 2", m);
					ModelNode arm2Shape = new ModelNode("Arm 2 of lamp", armSphere);



		// Create the head of the lamp
		NameNode head = new NameNode("Head");


			// Position the head of the lamp
			m = Mat4Transform.translate(0,armHeight,0);
			TransformNode positionHead = new TransformNode("Move head into position", m);

			// Create a node to allow us to tilt the head of the lamp
			headRotate = new TransformNode("Tilt the head of the lamp",Mat4Transform.rotateAroundZ(0));

			// Build the head
			m = Mat4.multiply(Mat4Transform.scale(headWidth,headHeight,headDepth), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeHead = new TransformNode("Make the head", m);
				ModelNode headShape = new ModelNode("Head of lamp", headCube);

			// Create two snail eyes attached to the head
			eyeRotateNodes = new TransformNode[2];
			positionEyeStems = new TransformNode[2];
			for (int i = 0; i < 2; i++) {

				// Store a new rotation node
				 TransformNode eyeRotateNode = new TransformNode("Rotate eye of the lamp",Mat4Transform.rotateAroundZ(15));

				// Different z direction depending on which eye
				int zFactor = (i == 0)
						? 1
						: -1;

				// Create a stem for the eye
				NameNode eyeStem = new NameNode("Eye stem");
					m = Mat4Transform.translate(-headWidth/2+(eyeRadius/2),headHeight,zFactor*(headDepth/2-(eyeStemDepth/2)));
					TransformNode positionEyeStem = new TransformNode("Move stem for eye into position", m);

					m = Mat4.multiply(Mat4Transform.scale(eyeStemWidth,eyeStemHeight,eyeStemDepth), Mat4Transform.translate(0,0.5f,0));
						TransformNode makeEyeStem = new TransformNode("Make the stem for eye", m);
						ModelNode eyeStemShape = new ModelNode("Stem for eye 1",eyeStemSphere);

				// Create the eye itself
				NameNode eye = new NameNode("Eyeball");
					m = Mat4Transform.translate(0, eyeStemHeight,0);
						TransformNode positionEye = new TransformNode("Move eyeball into position", m);

					m = Mat4.multiply(Mat4Transform.scale(eyeRadius,eyeRadius,eyeRadius), Mat4Transform.translate(0,0.5f,0));
						TransformNode makeEye = new TransformNode("Make the eyeball", m);
						ModelNode eyeShape = new ModelNode("Eye of lamp", eyeSphere);


				// Create a mini hierarchy
				positionEyeStem.addChild(eyeRotateNode);
					eyeRotateNode.addChild(eyeStem);
						eyeStem.addChild(makeEyeStem);
							makeEyeStem.addChild(eyeStemShape);
						eyeStem.addChild(positionEye);
							positionEye.addChild(eye);
								eye.addChild(makeEye);
									makeEye.addChild(eyeShape);

				// Store the nodes in arrays
				eyeRotateNodes[i] = eyeRotateNode;
				positionEyeStems[i] = positionEyeStem;


			}

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
					positionArm1.addChild(baseRotate);
						baseRotate.addChild(arm1);
							arm1.addChild(makeArm1);
								makeArm1.addChild(arm1Shape);
							arm1.addChild(positionJoint);
								positionJoint.addChild(jointRotate);
								jointRotate.addChild(joint1);
									joint1.addChild(makeJoint);
										makeJoint.addChild(joint1Shape);
									joint1.addChild(positionShell);
										positionShell.addChild(shell);
											shell.addChild(makeShell);
												makeShell.addChild(shellShape);
									joint1.addChild(positionArm2);
										positionArm2.addChild(arm2);
											arm2.addChild(makeArm2);
												makeArm2.addChild(arm2Shape);
											arm2.addChild(positionHead);
												positionHead.addChild(headRotate);
													headRotate.addChild(head);
														head.addChild(makeHead);
															makeHead.addChild(headShape);
														// Add the eyes
														for (TransformNode positionEyeStem : positionEyeStems) {
															head.addChild(positionEyeStem);
														}
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
		float eyeRotate = -(float)(Math.cos(elapsedTime)*15);

//		jointRotate.setTransform(Mat4Transform.rotateAroundZ(rotateAngle));
//		headRotate.setTransform(Mat4Transform.rotateAroundX(-rotateAngle));
//		baseRotate.setTransform(Mat4Transform.rotateAroundY(rotateAngle));

		// Move the eyes on the lamp
		for (int i = 0; i < eyeRotateNodes.length; i++){
			int zFactor = (i == 0) ? 1 : -1;
			eyeRotateNodes[i].setTransform(Mat4Transform.rotateAroundZ(zFactor*eyeRotate));
		}

		lampRoot.update();
	}

	public void animate(int pose, double elapsedTime){

		LampPose currentPose = poses.getPose(pose);

		float factor = (float) Math.sin(elapsedTime);

		// Update the transforms
		baseRotate.setTransform(currentPose.getBaseTransform());
		jointRotate.setTransform(currentPose.getArmTransform());
		headRotate.setTransform(currentPose.getHeadTransform());
		lampRoot.update();

	}

	public void dispose(GL3 gl) {
		baseCube.dispose(gl);
		jointSphere.dispose(gl);
		armSphere.dispose(gl);
		eyeSphere.dispose(gl);
		eyeStemSphere.dispose(gl);
		shellSphere.dispose(gl);
		headLight.dispose(gl);
	}
}