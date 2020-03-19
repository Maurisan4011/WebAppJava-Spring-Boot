package com.maudev.springboot.app;

//import java.nio.file.Paths;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	// debuugear
//	private final Logger log = LoggerFactory.getLogger(getClass());

	
	/*@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Praa depsito en Pc
//		WebMvcConfigurer.super.addResourceHandlers(registry);
//		registry.addResourceHandler("/uploads/**").addResourceLocations("file:/C:/Temp/uploads/");

		WebMvcConfigurer.super.addResourceHandlers(registry);
		String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
		log.info(resourcePath);
		registry.addResourceHandler("/uploads/**").addResourceLocations(resourcePath);

	}*/

}
