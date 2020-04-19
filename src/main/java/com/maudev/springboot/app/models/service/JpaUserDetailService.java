package com.maudev.springboot.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maudev.springboot.app.models.dao.IUsuarioDao;
import com.maudev.springboot.app.models.entity.Role;
import com.maudev.springboot.app.models.entity.Usuario;


@Service("jpaUserDetailService")
public class JpaUserDetailService implements UserDetailsService {

	@Autowired
	private IUsuarioDao usuarioDao;

	private Logger logger = org.slf4j.LoggerFactory.getLogger(JpaUserDetailService.class);

	/*
	 * Transaacional bajo la misma transaccion vamos a realizar la misma consulta
	 * del usuario como tambiem vamos a obtenre los roles (readOnly = true) solo es
	 * de lectura nada mas
	 */
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// obtener usuario atraves de la clase entity
		Usuario usuario = usuarioDao.findByUsername(username);

		if (usuario == null) {
			logger.error("Error login: no existe el usuario '" + usuario + "'");
			throw new UsernameNotFoundException("Username  " + usuario + " no existe en el sistema!!");
		}

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (Role role : usuario.getRoles()) {
			logger.info("Role: ".concat(role.getAuthority()));
			authorities.add(new SimpleGrantedAuthority(role.getAuthority()));

		}

		if (authorities.isEmpty()) {
			logger.error("Error login: usuario '" + usuario + "' no tiene roles asignados");
			throw new UsernameNotFoundException("Error login: usuario '" + usuario + "' no tiene roles asignados");
		}

		return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true,
				authorities);
	}

}
