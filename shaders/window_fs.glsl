#version 330 core

in vec3 aPos;
in vec3 aNormal;
in vec2 aTexCoord;

out vec4 fragColor;

uniform sampler2D first_texture;
uniform vec3 viewPos;

struct Light {
  vec3 position;
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
};

uniform Light light;

struct Material {
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  float shininess;
};

uniform Material material;

void main() {


  vec4 texColor = texture(first_texture, aTexCoord);
  if(texColor.a < 0.1)
    discard;
  fragColor = texColor;

//  vec3 ambient = light.ambient * material.ambient * texture(first_texture, aTexCoord).rgba;
//
//  // diffuse
//  vec3 norm = normalize(aNormal);
//  vec3 lightDir = normalize(light.position - aPos);
//  float diff = max(dot(norm, lightDir), 0.0);
//  vec3 diffuse = light.diffuse * (diff * material.diffuse) * texture(first_texture, aTexCoord).rgb;
//
//  // specular
//  vec3 viewDir = normalize(viewPos - aPos);
//  vec3 reflectDir = reflect(-lightDir, norm);
//  float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
//  vec3 specular = light.specular * (spec * material.specular);
//
//  vec3 result = ambient + diffuse + specular;
//  // Opacity
//  fragColor = vec4(result.x, result.y, result.z, 0.0);
}