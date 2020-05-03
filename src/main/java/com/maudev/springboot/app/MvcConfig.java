package com.maudev.springboot.app;

import java.util.Locale;

import org.springframework.context.annotation.Bean;

//import java.nio.file.Paths;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	// Metodo para registrar un controlador de vista

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/error_403").setViewName("error_403");
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Para multiLenguaje
	/* Resuelve el locale donde se almacena el prametro de nuestro lenguaje */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("es", "ES"));

		return localeResolver;
	}
	/*
	 * Interceptor que se encarga de cambiar o modificar el lenguaje cada que le
	 * pasemos lang por url
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
		localeInterceptor.setParamName("lang");

		return localeInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		registry.addInterceptor(localeChangeInterceptor());
	}

}

// debuugear
//private final Logger log = LoggerFactory.getLogger(getClass());

/*
 * @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
 * // Praa depsito en Pc //
 * WebMvcConfigurer.super.addResourceHandlers(registry); //
 * registry.addResourceHandler("/uploads/**").addResourceLocations(
 * "file:/C:/Temp/uploads/");
 * 
 * WebMvcConfigurer.super.addResourceHandlers(registry); String resourcePath =
 * Paths.get("uploads").toAbsolutePath().toUri().toString();
 * log.info(resourcePath);
 * registry.addResourceHandler("/uploads/**").addResourceLocations(resourcePath)
 * ;
 * 
 * }
 */
