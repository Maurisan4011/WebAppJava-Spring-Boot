package com.maudev.springboot.app.models.dao;



//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.maudev.springboot.app.models.entity.Cliente;

public interface IClienteDao  extends PagingAndSortingRepository <Cliente, Long>{


}
