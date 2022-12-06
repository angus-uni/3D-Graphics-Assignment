#version 330 core

in vec3 aPos;
in vec3 aNormal;
in vec2 aTexCoord;

out vec4 fragColor;

uniform sampler2D first_texture;
uniform sampler2D second_texture;

uniform vec3 viewPos;

struct Light {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};


struct SpotLight {
    vec3 position;
    vec3 direction;
    float cutOff;
    float outerCutOff;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};


struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

int NR_WORLD_LIGHTS = 2;
uniform Light worldLights[2];
int NR_POINT_LIGHTS = 2;
uniform SpotLight SpotLights[2];

uniform Material material;

vec3 CalcWorldLight(Light worldLight, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    /*
     * Calculate the affect of a world light on this particular fragment
     */

    // ambient
    vec3 ambient = worldLight.ambient * material.ambient * texture(first_texture, aTexCoord).rgb;

    // diffuse
    vec3 lightDir = normalize(worldLight.position - fragPos);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = worldLight.diffuse * (diff * material.diffuse) * texture(first_texture, aTexCoord).rgb;

    // specular
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = worldLight.specular * (spec * vec3(texture(second_texture, aTexCoord)));

    return (ambient + diffuse + specular);
}

vec3 CalcSpotLight(SpotLight light, vec3 norm, vec3 fragPos, vec3 viewDir)
{
    /*
     * Calculate the affect of a spot light on this particular fragment
     */

    vec3 lightDir = normalize(light.position - fragPos);

    // Ambient
    vec3 ambient = light.ambient * material.diffuse;

    // diffuse
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * diff * material.diffuse;

    // specular
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = light.specular * spec * texture(second_texture, aTexCoord).rgb;

    // spotlight (soft edges)
    float theta = dot(lightDir, normalize(-light.direction));
    float epsilon = (light.cutOff - light.outerCutOff);
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0, 1.0);
    diffuse  *= intensity;
    specular *= intensity;

    // attenuation
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    ambient  *= attenuation;
    diffuse   *= attenuation;
    specular *= attenuation;
    return (ambient + diffuse + specular);
}


void main() {

    vec3 viewDir = normalize(viewPos - aPos);
    vec3 norm = normalize(aNormal);
    vec3 result = vec3(0,0.0,0);

    //Go through each world light
    for(int i = 0; i < NR_WORLD_LIGHTS; i++){
        result += CalcWorldLight(worldLights[i], norm, aPos, viewDir);
    }

    // Go though our spot lights
    for(int i = 0; i < NR_POINT_LIGHTS; i++){
        result += CalcSpotLight(SpotLights[i], norm, aPos, viewDir);
    }

    fragColor = vec4(result, 1.0);
}