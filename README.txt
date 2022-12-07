
# Hatch
Angus Goody
2022

My scene consists of a large egg object on a table surrounded by two lamps that are meant to resemble snails. The egg is placed in a room that is modeled as an office. The window in the office looks out onto the garden scene, the garden features a simple grass hill with clouds moving around.

I have made use of some of the code from the tutorials and also created new code, below I will briefly outline the classes I have created

## Light classes

### SpotLight
A class to represent a spotlight, this stores the relevant information such as viewing angle, direction and equation

### LightNode
A class I created to allow lights and spotlights to be added to a scene graph

## Lamp related classes

### Lamp
The lamp is an articulated structure that can be moved around, the head of the lamp contains a spotlight which is used to illuminate the scene, these lamps are made to look like snails, they have a shell and moving eyeballs that change colour.

### LampPose
This class represents a single pose that a lamp can take, this is made up of three transformations to...
- The base
- The arm joint
- The head

### Poses
This class stores all the different poses (LampPose) that a lamp can take and makes the code cleaner for retrieving said poses

## Other classes

### Garden
Builds a garden scene graph with 4 walls and a floor, animated clouds and a yellow sun world light

### Table
The class that renders a wooden table with a jumping egg on it.

### Room
Builds the main office scene graph, this is made of 3 walls and a floor. The room class then renders the `Table` class

### Scene
The main class that is responsible for rendering the whole scene, it is also used to store global values such as world lights

### MagicMaterial
A cool little class that extends `Material` that will change the colour of itself throughout the program runtime

## Tweaks to existing code

### Model
I updated the model class to be able to handle shaders that contain array uniforms, for example the world lights and spot lights

### Gmaths
I added some little helper methods to be able to extract position and direction vectors from the world matrix

