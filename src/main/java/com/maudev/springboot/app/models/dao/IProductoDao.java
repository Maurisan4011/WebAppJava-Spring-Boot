package com.maudev.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.maudev.springboot.app.models.entity.Producto;

public interface IProductoDao extends CrudRepository<Producto, Long> {

	@Query("select p from Producto p where p.nombre like %?1%")
	public List<Producto> findByNombre(String term);
	
	
	//Otra forma de implementacion LikeIgnoreCase consulta basada en nombre de metodo y spring data
	//Realiza la consulta simplemente por mantener un standar en el nombrado del metodo
	public List<Producto> findByNombreLikeIgnoreCase(String term);
}
