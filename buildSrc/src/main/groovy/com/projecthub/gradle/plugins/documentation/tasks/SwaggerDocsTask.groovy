// package com.projecthub.gradle.plugins.documentation.tasks

// import com.projecthub.gradle.plugins.documentation.utils.DocUtils
// import groovy.transform.CompileStatic
// import org.gradle.api.DefaultTask
// import org.gradle.api.tasks.Input
// import org.gradle.api.tasks.Optional
// import org.gradle.api.tasks.OutputDirectory
// import org.gradle.api.tasks.TaskAction

// /**
//  * Custom task for generating Swagger API documentation.
//  * Updated implementation for Gradle 8.13 compatibility.
//  */
// @CompileStatic(skipAll=true)
// class SwaggerDocsTask extends DefaultTask {
//     @Input
//     @Optional
//     String basePackage = "com.projecthub"

//     @Input
//     @Optional
//     String apiGroup = "ProjectHub API"

//     @Input
//     @Optional
//     String apiTitle = "ProjectHub API Documentation"

//     @Input
//     @Optional
//     String apiVersion = project.version.toString()

//     @OutputDirectory
//     File outputDir = new File(project.buildDir, "docs/swagger")

//     SwaggerDocsTask() {
//         description = "Generates Swagger API documentation"
//         group = "Documentation"
//     }

//     @TaskAction
//     void generateSwaggerDocs() {
//         // Create output directory
//         outputDir.mkdirs()

//         // Use Spring Boot's actuator endpoint to generate Swagger docs
//         // In a real implementation, this would invoke swagger or springdoc-openapi tools
//         // This is a simplified implementation that depends on the project having springdoc/swagger configured

//         // We'll create a simple Java application that uses springdoc-openapi to generate the docs
//         File swaggerGenFile = File.createTempFile("SwaggerGenerator", ".java")
//         swaggerGenFile.text = """
//             import org.springdoc.core.SpringDocConfigProperties;
//             import org.springdoc.core.SpringDocConfiguration;
//             import org.springframework.boot.autoconfigure.SpringBootApplication;
//             import org.springframework.boot.SpringApplication;
//             import org.springframework.context.ConfigurableApplicationContext;
//             import org.springframework.context.annotation.Bean;
//             import org.springframework.context.annotation.ComponentScan;
//             import io.swagger.v3.oas.models.OpenAPI;
//             import io.swagger.v3.oas.models.info.Info;

//             @SpringBootApplication
//             @ComponentScan(basePackages = {"${basePackage}"})
//             public class SwaggerGenerator {
//                 public static void main(String[] args) {
//                     ConfigurableApplicationContext ctx = SpringApplication.run(SwaggerGenerator.class, args);
//                     // SpringDoc will generate the OpenAPI spec
//                     SpringDocConfiguration config = ctx.getBean(SpringDocConfiguration.class);
//                     SpringDocConfigProperties properties = ctx.getBean(SpringDocConfigProperties.class);
//                     properties.setWriteToFile(true);
//                     properties.setApiDocs(new SpringDocConfigProperties.ApiDocs());
//                     properties.getApiDocs().setPath("${outputDir.absolutePath}/openapi.json");
//                     ctx.close();
//                 }

//                 @Bean
//                 public OpenAPI customOpenAPI() {
//                     return new OpenAPI()
//                         .info(new Info()
//                             .title("${apiTitle}")
//                             .version("${apiVersion}")
//                             .description("${apiGroup}")
//                         );
//                 }
//             }
//         """

//         // For now, we'll create a simple HTML file with instructions
//         File swaggerIndexFile = new File(outputDir, "index.html")
//         swaggerIndexFile.text = """
//             <!DOCTYPE html>
//             <html>
//             <head>
//                 <title>${apiTitle}</title>
//                 <meta charset="UTF-8">
//                 <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist@3/swagger-ui.css">
//             </head>
//             <body>
//                 <div id="swagger-ui"></div>
//                 <script src="https://unpkg.com/swagger-ui-dist@3/swagger-ui-bundle.js"></script>
//                 <script>
//                     window.onload = function() {
//                         SwaggerUIBundle({
//                             url: "openapi.json",
//                             dom_id: '#swagger-ui',
//                             deepLinking: true,
//                             presets: [
//                                 SwaggerUIBundle.presets.apis,
//                                 SwaggerUIBundle.SwaggerUIStandalonePreset
//                             ],
//                             layout: "BaseLayout"
//                         });
//                     }
//                 </script>
//             </body>
//             </html>
//         """

//         // Create a placeholder openapi.json file
//         File openapiFile = new File(outputDir, "openapi.json")
//         openapiFile.text = """
//         {
//             "openapi": "3.0.1",
//             "info": {
//                 "title": "${apiTitle}",
//                 "description": "${apiGroup}",
//                 "version": "${apiVersion}"
//             },
//             "paths": {},
//             "components": {
//                 "schemas": {}
//             }
//         }
//         """

//         logger.lifecycle("Generated Swagger/OpenAPI placeholder in ${outputDir}")
//         logger.lifecycle("NOTE: For full Swagger generation, integrate with springdoc-openapi in your project")

//         // Copy Swagger files to unified docs directory
//         DocUtils.copyDocs(project, outputDir, "swagger")
//     }
// }
