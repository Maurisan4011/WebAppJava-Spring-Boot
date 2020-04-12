package com.maudev.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {
			
		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar").permitAll()
		.antMatchers("/ver/**").hasAnyRole("USER") //ojo este esta como admin si pincha a futuro
		.antMatchers("/uploads/**").hasAnyRole("USER")
		.antMatchers("/form/**").hasAnyRole("ADMIN")
		.antMatchers("/eliminar/**").hasAnyRole("ADMIN")
		.antMatchers("/factura/**").hasAnyRole("ADMIN")
		.anyRequest().authenticated()
		.and()
				.formLogin().loginPage("/login")
				.permitAll()
		.and()
		.logout().permitAll();
	}



	@Autowired
	public void congurerGlobal(AuthenticationManagerBuilder builer) throws Exception {
//		puede aparecer en otras versiones
//		UserBuilder users = User.withDefaultPasswordEncoder();
		PasswordEncoder encoder = passwordEncoder();

		// UserBuilder users = User.builder().passwordEncoder(password ->
		// encoder.encode(password));

		// mas simple lambda java 8
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);
		
		//Creamos los usuarios 
		builer.inMemoryAuthentication()
		.withUser(users.username("admin").password("12345").roles("ADMIN", "USER"))
		.withUser(users.username("mauricio").password("12345").roles("USER"));
		

	}

}
