import gmaths.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

class Garden {

    private Camera camera;
    private Light light;
    private float wallSize = 25f;
    private Vec2 cloudPos;

    private Texture floorTexture, skyTexture, cloudTexture;
    private Shader dynamicShader;
    private SGNode roomRoot;
    private Model floorModel, wallModel;


    private void loadTextures(GL3 gl) {
        floorTexture = TextureLibrary.loadTexture(gl, "textures/soil.jpg");
        skyTexture = TextureLibrary.loadTexture(gl, "textures/sky.jpg");
        cloudTexture = TextureLibrary.loadTexture(gl, "textures/cloud.jpg");
    }

    public Garden(GL3 gl, Camera c, Light l) {
        camera = c;
        light = l;

        // Load our textures
        loadTextures(gl);

        // Setup mesh & shaders
        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader shader = new Shader(gl, "shaders/tt_vs.glsl", "shaders/tt_fs.glsl");
        dynamicShader = new Shader(gl, "shaders/dynamic_background_vs.glsl", "shaders/dynamic_background_fs.glsl");

        // Build each model
        floorModel = buildFloor(gl, mesh, shader);
        wallModel = buildWall(gl, mesh, dynamicShader);

        // ====================== Create the scene graph for our room =============================

        // Base matrix (both window's & floor need to be this size)
        Mat4 mStart = Mat4Transform.scale(wallSize,1f,wallSize);

        roomRoot = new NameNode("Room root");

        // Create transform to move the room if we want
        TransformNode roomMoveTransform = new TransformNode("move room transform", Mat4Transform.translate(0,0,-(Room.wallSize / 2)));


        // Create the floor node
        NameNode floorNode = new NameNode("Floor");
            TransformNode floorTransform = new TransformNode("Floor transform", mStart);
                ModelNode floorShape = new ModelNode("floor shape", floorModel);

        // Create the back wall node
        NameNode windowNode = new NameNode("Window");
            Mat4 m = Mat4.multiply(Mat4Transform.rotateAroundX(90), mStart);
            m = Mat4.multiply(Mat4Transform.translate(0,wallSize*0.5f,-wallSize*0.5f), m);
                TransformNode windowTransform = new TransformNode("Window transform", m);
                    ModelNode windowShape = new ModelNode("Window shape", wallModel);

        // Create the left wall node
        NameNode leftWall = new NameNode("Left wall");
            m = Mat4.multiply(Mat4Transform.rotateAroundY(90), mStart);
            m = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), m);
            m = Mat4.multiply(Mat4Transform.translate(-wallSize*0.5f,wallSize*0.5f,0), m);
                TransformNode leftWallTransform = new TransformNode("Left wall transform", m);
                    ModelNode leftWallShape = new ModelNode("left wall shape", wallModel);

        // Create the right wall node
        NameNode rightWall = new NameNode("Right wall");
            m = Mat4.multiply(Mat4Transform.rotateAroundY(90), mStart);
            m = Mat4.multiply(Mat4Transform.rotateAroundZ(90), m);
            m = Mat4.multiply(Mat4Transform.translate(wallSize*0.5f,wallSize*0.5f,0), m);
                TransformNode rightWallTransform = new TransformNode("Right wall transform", m);
                    ModelNode rightWallShape = new ModelNode("Right wall shape", wallModel);


        // Create Hierarchy
        roomRoot.addChild(roomMoveTransform);
            roomMoveTransform.addChild(floorNode);
                floorNode.addChild(floorTransform);
                    floorTransform.addChild(floorShape);
            roomMoveTransform.addChild(leftWall);
                leftWall.addChild(leftWallTransform);
                    leftWallTransform.addChild(leftWallShape);
            roomMoveTransform.addChild(windowNode);
                windowNode.addChild(windowTransform);
                windowTransform.addChild(windowShape);
            roomMoveTransform.addChild(rightWall);
                rightWall.addChild(rightWallTransform);
                rightWallTransform.addChild(rightWallShape);

        roomRoot.update();

    }

    private Model buildFloor(GL3 gl, Mesh mesh ,Shader shader){
        Material floorMaterial = new Material(new Vec3(0.76f, 0.62f, 0.51f), new Vec3(0.84f,  0.71f,  0.59f), new Vec3(0.3f, 0.3f, 0.3f), 1.0f);
        return new Model(gl, camera, light, shader, floorMaterial, new Mat4(), mesh, floorTexture);
    }

    private Model buildWall(GL3 gl, Mesh mesh ,Shader shader){
        Material wallMaterial = new Material(new Vec3(0.76f, 0.62f, 0.51f), new Vec3(0.84f,  0.71f,  0.59f), new Vec3(0.3f, 0.3f, 0.3f), 1.0f);
        return new Model(gl, camera, light, shader, wallMaterial, new Mat4(), mesh, skyTexture, cloudTexture);
    }


    public void render(GL3 gl) {
        dynamicShader.use(gl);
        dynamicShader.setFloat(gl, "offset", cloudPos.x, cloudPos.y);
        roomRoot.draw(gl);
    }

    public void setClouds(Vec2 pos){
        cloudPos = pos;
    }


    public void dispose(GL3 gl) {
        floorModel.dispose(gl);
        wallModel.dispose(gl);
    }
}
