import gmaths.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

/**
 * Room class to represent the room
 * of the scene, the room contains
 * the table and egg etc
 */

public class Room {

    private Model floor, wall, window;
    public static Float wallSize = 16f;
    private SGNode roomRoot;
    private Table table;
    private Lamp[] lamps;

    private Texture[] textures;


    private void loadTextures(GL3 gl) {
        textures = new Texture[3];
        textures[0] = TextureLibrary.loadTexture(gl, "textures/floor.jpg");
        textures[1] = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
        textures[2] = TextureLibrary.loadTexture(gl, "textures/window.png");

    }

    public Room(GL3 gl, Camera camera, Light[] worldLights ,Shader multiShader) {

        loadTextures(gl);
        SpotLight[] lampLights = new SpotLight[2];

        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader windowShader = new Shader(gl, "shaders/window_vs.glsl", "shaders/window_fs.glsl");

        // The floor is going to be wood, this should be pretty matte
        Material floorMaterial = new Material();

        // For now the wallMaterial can be the same as the floor
        Material wallMaterial = new Material();

        // The window is glass so it should be shiny
        Material glass = new Material();

        // Create an array to store our lamps
        lamps = new Lamp[2];

        // Lamp 1 (left hand side)
        Mat4 initialPosition = Mat4Transform.translate(-3,0,0);
        Lamp lamp1 = new Lamp(gl, camera, worldLights, multiShader, Lamp.Size.SMALL, initialPosition);
        lampLights[0] = lamp1.getSpotLight();
        lamps[0] = lamp1;

        // Lamp 2 (right hand side)
        initialPosition = Mat4.multiply(Mat4Transform.translate(4,0,0), Mat4Transform.rotateAroundY(180));
        Lamp lamp2 = new Lamp(gl, camera, worldLights, multiShader, Lamp.Size.MEDIUM, initialPosition);
        lampLights[1] = lamp2.getSpotLight();
        lamps[1] = lamp2;

        //Create a table object
        table  = new Table(gl, camera, worldLights, lampLights, multiShader);


        // Create models for the floor & wall
        floor = new Model(gl, camera, worldLights, lampLights, multiShader, floorMaterial, new Mat4(), mesh, textures[0]);
        wall = new Model(gl, camera, worldLights, lampLights, multiShader, wallMaterial, new Mat4(), mesh, textures[1]);

        // Only render the window with the room light
        window = new Model(gl, camera, worldLights, lampLights, windowShader, glass, new Mat4(), mesh, textures[2]);



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

        float lightSize = 0.9f;
            LightNode roomLightNode = new LightNode("Light", worldLights[0]);
                m = Mat4Transform.translate(0,Room.wallSize,0);
                TransformNode positionLight = new TransformNode("Position light",m);

                m = Mat4Transform.scale(new Vec3(lightSize));
                TransformNode scaleLight = new TransformNode("Scale light",m);


        // Create Hierarchy
        roomRoot.addChild(roomMoveTransform);
            roomMoveTransform.addChild(table.getRoot());
            roomMoveTransform.addChild(lamp1.getRoot());
            roomMoveTransform.addChild(lamp2.getRoot());
            roomMoveTransform.addChild(positionLight);
                positionLight.addChild(scaleLight);
                    scaleLight.addChild(roomLightNode);

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

    public void render(GL3 gl, double elapsedTime) {

        // Activate egg animation
        table.makeEggJump(elapsedTime);

        // Move the features on the lamps
        for (Lamp lamp:lamps) {
            lamp.move(elapsedTime);
        }

        // Draw the root
        roomRoot.draw(gl);

    }

    public void dispose(GL3 gl) {
        floor.dispose(gl);
        wall.dispose(gl);
        table.dispose(gl);

        // Remove all the lamps
        for (Lamp lamp:lamps) {
            lamp.dispose(gl);
        }
    }

    public void toggleLamp(int i) {
        lamps[i].getSpotLight().toggle();
    }

    public void animateLamp(int lampNumber, int pose) {
        lamps[lampNumber].animate(pose);
    }
}