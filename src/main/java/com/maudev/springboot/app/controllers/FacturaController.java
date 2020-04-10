package com.maudev.springboot.app.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.maudev.springboot.app.models.entity.Cliente;
import com.maudev.springboot.app.models.entity.Factura;
import com.maudev.springboot.app.models.entity.ItemFactura;
import com.maudev.springboot.app.models.entity.Producto;
import com.maudev.springboot.app.models.service.IClienteService;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

	@Autowired // inyectamos
	// traemos id del cliente
	private IClienteService clienteService;

	// Debug de la cantidad y el ID usamos el
	private final Logger log = LoggerFactory.getLogger(getClass());

	// Pticion detalle
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		// Obtenemos la factuura por id
		Factura factura = clienteService.fetchFacturaByIdWithClienteWithItemFacturaWithProducto(id);
		//clienteService.findFacturaById(id);

		// verificar si no esta vacia
		if (factura == null) {
			flash.addFlashAttribute("error", "La factura no existe en base de datos!. msj desde backend");
			return "redirect:/listar";
		}

		// Si todo esta bien pasamos factura a vista

		model.addAttribute("factura", factura);
		model.addAttribute("titulo", "Factura :  ".concat(factura.getDescripcion()));

		return "factura/ver";

	}

	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable(value = "clienteId") Long clienteId, Map<String, Object> model,
			RedirectAttributes flash) {

		Cliente cliente = clienteService.findOne(clienteId);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de Datos. msj desde backend");
			return "redirect:/listar";
		}
		Factura factura = new Factura();
		factura.setCliente(cliente);

		model.put("factura", factura);
		model.put("titulo", "Crear Factura");

		return "factura/form";
	}

	// @ResponseBody suprime el cargar una vista de thymeleaf en ves de eso toma el
	// resultado
	// que se retorna y json lo poble y los registra dentro del body de la respueta

	@GetMapping(value = "/cargar-productos/{term}", produces = { "application/json" })
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term) {
		return clienteService.finByNombre(term);

	}

	@PostMapping("/form")
	// @Valid habilita la validacion en el objeto factura de manera autometica
	// BindingResult comprueba errores en la validacion de la factura
	public String guardar(@Valid Factura factura, BindingResult result, Model model,
			@RequestParam(name = "item_id[]", required = false) Long[] itemId,
			@RequestParam(name = "cantidad[]", required = false) Integer[] cantidad, RedirectAttributes flash,
			SessionStatus status) {
		// va a recibir el objeto que esta mapeado al formulario
		// y a su ves tambien algunos parametros del request que tienen la vista
		// plantilla-items
		// item_id[] y tambien cantidad[]

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Crear Factura");
			return "factura/form";

		}

		if (itemId == null || itemId.length == 0) {
			model.addAttribute("titulo", "Crear Factura");
			model.addAttribute("error", "Error: La Factura NO puede no tener lineas!!");
			return "factura/form";
		}
		for (int i = 0; i < itemId.length; i++) {
			Producto producto = clienteService.finProductoById(itemId[i]);

			ItemFactura linea = new ItemFactura();
			linea.setCantidad(cantidad[i]);
			linea.setProducto(producto);
			factura.addItemFactura(linea);

			log.info("ID: " + itemId[i].toString() + ", Cantidad:  " + cantidad[i].toString());
		}
		// Guardar la factura en la base de datos

		clienteService.saveFactura(factura);
		status.setComplete();
		flash.addFlashAttribute("success", "Factura Creada con éxito!! msj desde backend");
		return "redirect:/ver/" + factura.getCliente().getId();
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		Factura factura = clienteService.findFacturaById(id);

		if (factura != null) {
			clienteService.deleteFactura(id);
			flash.addFlashAttribute("success", "Factura eliminada con éxito!. Msj desde Backend");
			return "redirect:/ver/" + factura.getCliente().getId();
		}

		flash.addFlashAttribute("error", "La Factura no existe en la base de datos, no se pudo eliminar!. Msj desde Backend");
		return "redirect:/listar";
	}

}
