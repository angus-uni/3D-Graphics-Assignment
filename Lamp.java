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
	private TransformNode jointRotate;


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

//		// Create the joint of the lamp
//		NameNode joint1 = new NameNode("Joint 1");
//
//			/*
//			 - Translate our sphere to the surface
//			 - Scale it into an arm
//			 - move into position
//			 */
//
//			m = Mat4Transform.translate(0,armHeight-(jointRadius/2),0);
//			TransformNode positionJoint = new TransformNode("Move joint 1 into position", m);
//
//			m = Mat4.multiply(Mat4Transform.scale(jointRadius,jointRadius,jointRadius), Mat4Transform.translate(0,0.5f,0));
//				TransformNode makeJoint = new TransformNode("Create joint 1", m);
//					ModelNode joint1Shape = new ModelNode("Joint 1 of lamp", jointSphere);

		// Rotate transform
		jointRotate = new TransformNode("Rotate upper arm",Mat4Transform.rotateAroundZ(90));

		// Create the arm of the lamp
		NameNode arm2 = new NameNode("arm 2");

			/*
			 - Translate our sphere to the surface
			 - Scale it into an arm
			 - move into position
			 */

		m = Mat4Transform.translate(0,armHeight,0);
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

			m = Mat4.multiply(Mat4Transform.scale(headWidth,headHeight,headDepth), Mat4Transform.translate(0,0.5f,0));
				TransformNode makeHead = new TransformNode("Make the head", m);
				ModelNode headShape = new ModelNode("Head of lamp", headCube);


		// Add nodes hierarchy
		lampRoot.addChild(lampMoveTransform);
			lampMoveTransform.addChild(base);
				base.addChild(makeBase);
					makeBase.addChild(baseShape);
				base.addChild(positionArm1);
					positionArm1.addChild(arm1);
						arm1.addChild(makeArm1);
							makeArm1.addChild(arm1Shape);
						arm1.addChild(positionArm2);
							positionArm2.addChild(jointRotate);
								jointRotate.addChild(arm2);
									arm2.addChild(makeArm2);
										makeArm2.addChild(arm2Shape);
									arm2.addChild(positionHead);
										positionHead.addChild(head);
											head.addChild(makeHead);
												makeHead.addChild(headShape);



		/*
		twoBranchRoot.addChild(translateX);
      translateX.addChild(rotateAll);
        rotateAll.addChild(lowerBranch);
          lowerBranch.addChild(makeLowerBranch);
            makeLowerBranch.addChild(cube0Node);
          lowerBranch.addChild(translateToTop);
            translateToTop.addChild(rotateUpper);
              rotateUpper.addChild(upperBranch);
                upperBranch.addChild(makeUpperBranch);
                  makeUpperBranch.addChild(cube1Node);
		 */

		lampRoot.update();  // IMPORTANT - don't forget this

	}

	public SGNode getRoot(){
		return lampRoot;
	}

	public void move(double elapsedTime){
		float rotateAngle = -(float)(Math.sin(elapsedTime)*90);
		jointRotate.setTransform(Mat4Transform.rotateAroundZ(rotateAngle));
		System.out.println(rotateAngle);
		lampRoot.update();
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