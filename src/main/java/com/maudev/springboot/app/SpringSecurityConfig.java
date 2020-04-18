package com.maudev.springboot.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import com.maudev.springboot.app.auth.handler.LoginSuccessHandler;


@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	
	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
			
		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar").permitAll()
		/*.antMatchers("/ver/**").hasAnyRole("USER") //ojo este esta como admin si pincha a futuro*/
		/*.antMatchers("/uploads/**").hasAnyRole("USER")*/
		/*.antMatchers("/form/**").hasAnyRole("ADMIN")*/
		/*.antMatchers("/eliminar/**").hasAnyRole("ADMIN")*/
		/*.antMatchers("/factura/**").hasAnyRole("ADMIN")*/
		.anyRequest().authenticated()
		.and()
			.formLogin()
				.successHandler(successHandler)
				.loginPage("/login")
			.permitAll()
		.and()
		.logout().permitAll()
		.and()
		.exceptionHandling().accessDeniedPage("/error_403");
	}


	 @Autowired
	public void congurerGlobal(AuthenticationManagerBuilder build) throws Exception {
		build.jdbcAuthentication()
		.dataSource(dataSource)
		.passwordEncoder(passwordEncoder)
		.usersByUsernameQuery("select username, password, enabled from users where username=?")
		.authoritiesByUsernameQuery("select u.username, a.authority from authorities a inner join users u on (a.user_id=u.id) where u.username=?");

	}


	/*esTE SE USA IN MEMORY
	 * 
	 * @Autowired
	public void congurerGlobal(AuthenticationManagerBuilder builer) throws Exception {
		puede aparecer en otras versiones
		UserBuilder users = User.withDefaultPasswordEncoder();
		PasswordEncoder encoder = this.passwordEncoder;
		// UserBuilder users = User.builder().passwordEncoder(password ->
		// encoder.encode(password));
		// mas simple lambda java 8
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);
		//Creamos los usuarios 
		builer.inMemoryAuthentication()
		.withUser(users.username("admin").password("12345").roles("ADMIN", "USER"))
		.withUser(users.username("mauricio").password("12345").roles("USER"));
	

	}*/	

}
