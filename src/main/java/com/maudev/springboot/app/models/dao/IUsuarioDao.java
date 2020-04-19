package com.maudev.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.maudev.springboot.app.models.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long> {
	
	/*A taraves del nombre del metodo (Query method name) se ejecutara la consulta JPQL
	 * "select u from Usuario u where u.username=?1"
	 * */
	public Usuario findByUsername (String username); //reemplaza a la consulta nativa que teniamos en SpringSecurityConfigc
	
	

}
