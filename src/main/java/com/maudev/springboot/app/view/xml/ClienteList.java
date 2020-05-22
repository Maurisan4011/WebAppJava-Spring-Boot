package com.maudev.springboot.app.view.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.maudev.springboot.app.models.entity.Cliente;

import lombok.Getter;

@XmlRootElement(name="clientesList")
public class ClienteList {
	@Getter
	@XmlElement(name="cliente")
	public List<Cliente> clientes;

	public ClienteList() {

	}

	public ClienteList(List<Cliente> clientes) {

		this.clientes = clientes;
	}

}
