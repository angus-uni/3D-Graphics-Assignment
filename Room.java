import gmaths.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

/**
 * Room class to represent the room
 * of the scene, i.e. the floor and the walls
 */

public class Room {

    private Model floor, wall, window;
    private Vec2 cloudPos;
    private Shader windowShader;
    public Float wallSize = 16f;
    private SGNode roomRoot;

    public Room(GL3 gl, Camera camera, Light light, int[] floorTexture, int[] wallTexture,int[] windowTexture, int[] backgroundTexture) {


        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader shader = new Shader(gl, "shaders/tt_vs.glsl", "shaders/tt_fs.glsl");
        windowShader = new Shader(gl, "shaders/window_vs.glsl", "shaders/window_fs.glsl");

        // The floor is going to be wood, this should be pretty matte
        Material floorMaterial = new Material(new Vec3(0.76f, 0.62f, 0.51f), new Vec3(0.84f,  0.71f,  0.59f), new Vec3(0.3f, 0.3f, 0.3f), 1.0f);

        // For now the wallMaterial can be the same as the floor
        Material wallMaterial = new Material(new Vec3(0.76f, 0.62f, 0.51f), new Vec3(0.84f,  0.71f,  0.59f), new Vec3(0.3f, 0.3f, 0.3f), 1.0f);

        // The window is glass so it should be shiny
        Material glass = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.84f,  0.71f,  0.59f), new Vec3(0.5f, 0.5f, 0.5f), 2.0f);


        // Create models for the floor & wall
        floor = new Model(gl, camera, light, shader, floorMaterial, new Mat4(), mesh, floorTexture);
        window = new Model(gl, camera, light, windowShader, glass, new Mat4(), mesh, windowTexture, backgroundTexture  );
        wall = new Model(gl, camera, light, shader, wallMaterial, new Mat4(), mesh, wallTexture);

        // ====================== Create the scene graph for our room =============================

        // Base matrix (both window's & floor need to be this size)
        Mat4 mStart = Mat4Transform.scale(wallSize,1f,wallSize);

        roomRoot = new NameNode("Room root");

        // Create transform to move the room if we want
        TransformNode roomMoveTransform = new TransformNode("move room transform", new Mat4(1));

        // Create the floor node
        NameNode floorNode = new NameNode("Floor");
            TransformNode floorTransform = new TransformNode("Floor transform", mStart);
            ModelNode floorShape = new ModelNode("floor shape", floor);

        // Create the back wall node
        NameNode windowNode = new NameNode("Window");
            Mat4 m = Mat4.multiply(Mat4Transform.rotateAroundX(90), mStart);
            m = Mat4.multiply(Mat4Transform.translate(0,wallSize*0.5f,-wallSize*0.5f), m);
                TransformNode windowTransform = new TransformNode("Window transform", m);
                ModelNode windowShape = new ModelNode("Window shape", window);

        // Create the left wall node
        NameNode leftWall = new NameNode("Left wall");
            m = Mat4.multiply(Mat4Transform.rotateAroundY(90), mStart);
            m = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), m);
            m = Mat4.multiply(Mat4Transform.translate(-wallSize*0.5f,wallSize*0.5f,0), m);
                TransformNode leftWallTransform = new TransformNode("Left wall transform", m);
                ModelNode leftWallShape = new ModelNode("left wall shape", wall);

        // Create the right wall node
        NameNode rightWall = new NameNode("Right wall");
            m = Mat4.multiply(Mat4Transform.rotateAroundY(90), mStart);
            m = Mat4.multiply(Mat4Transform.rotateAroundZ(90), m);
            m = Mat4.multiply(Mat4Transform.translate(wallSize*0.5f,wallSize*0.5f,0), m);
                TransformNode rightWallTransform = new TransformNode("Right wall transform", m);
                ModelNode rightWallShape = new ModelNode("Right wall shape", wall);


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

    public void render(GL3 gl) {

        windowShader.use(gl);
        windowShader.setFloat(gl, "offset", cloudPos.x, cloudPos.y);
        roomRoot.draw(gl);

    }

    public void setClouds(Vec2 pos){
        cloudPos = pos;
    }

    public void dispose(GL3 gl) {
        floor.dispose(gl);
        wall.dispose(gl);
    }

}