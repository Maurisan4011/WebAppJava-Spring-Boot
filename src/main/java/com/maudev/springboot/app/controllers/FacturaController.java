package com.maudev.springboot.app.controllers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Secured("ROLE_ADMIN")
@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
@Api(value = "Servicio Rest para informacion de Facturas")
public class FacturaController {

	@Autowired // inyectamos
	// traemos id del cliente
	private IClienteService clienteService;

	// Debug de la cantidad y el ID usamos el
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MessageSource messageSource;

	// Pticion detalle
	@ApiOperation("Retorna detalle De la factura por id")
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash, Locale locale) {

		// Obtenemos la factuura por id
		Factura factura = clienteService.fetchFacturaByIdWithClienteWithItemFacturaWithProducto(id);
		//clienteService.findFacturaById(id);

		// verificar si no esta vacia
		if (factura == null) {
			flash.addFlashAttribute("error", messageSource.getMessage("text.factura.flash.db.error", null, locale));
			return "redirect:/listar";
		}

		// Si todo esta bien pasamos factura a vista

		model.addAttribute("factura", factura);
		model.addAttribute("titulo", String.format(messageSource.getMessage("text.factura.ver.titulo", null, locale), factura.getDescripcion()));
		return "factura/ver";
	}
	
	@ApiOperation("Crear nueva factura por ID del cliente")
	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable(value = "clienteId") Long clienteId, Map<String, Object> model,
			RedirectAttributes flash, Locale locale) {

		Cliente cliente = clienteService.findOne(clienteId);

		if (cliente == null) {
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
			return "redirect:/listar";
		}

		Factura factura = new Factura();
		factura.setCliente(cliente);

		model.put("factura", factura);
		model.put("titulo", messageSource.getMessage("text.factura.form.titulo", null, locale));

		return "factura/form";
	}

	// @ResponseBody suprime el cargar una vista de thymeleaf en ves de eso toma el
	// resultado
	// que se retorna y json lo poble y los registra dentro del body de la respueta

	
	@ApiOperation("Retorna Lista de Productos")
	@GetMapping(value = "/cargar-productos/{term}", produces = { "application/json" })
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term) {
		return clienteService.finByNombre(term);

	}

	@PostMapping("/form")
	// @Valid habilita la validacion en el objeto factura de manera autometica
	// BindingResult comprueba errores en la validacion de la factura
	@ApiOperation("Guarda la nueva factura con todos los datos llenados en el fromulario")
	public String guardar(@Valid Factura factura, BindingResult result, Model model,
			@RequestParam(name = "item_id[]", required = false) Long[] itemId,
			@RequestParam(name = "cantidad[]", required = false) Integer[] cantidad, RedirectAttributes flash,
			SessionStatus status, Locale locale) {
		// va a recibir el objeto que esta mapeado al formulario
		// y a su ves tambien algunos parametros del request que tienen la vista
		// plantilla-items
		// item_id[] y tambien cantidad[]

		if (result.hasErrors()) {
			model.addAttribute("titulo", messageSource.getMessage("text.factura.form.titulo", null, locale));
			return "factura/form";

		}

		if (itemId == null || itemId.length == 0) {
			model.addAttribute("titulo", messageSource.getMessage("text.factura.form.titulo", null, locale));
			model.addAttribute("error", messageSource.getMessage("text.factura.flash.lineas.error", null, locale));
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

		flash.addFlashAttribute("success", messageSource.getMessage("text.factura.flash.crear.success", null, locale));

		return "redirect:/ver/" + factura.getCliente().getId();
	}
	@ApiOperation("Elimina 1 factura por id de la factura ")
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {

		Factura factura = clienteService.findFacturaById(id);

		if (factura != null) {
			clienteService.deleteFactura(id);
			flash.addFlashAttribute("success", messageSource.getMessage("text.factura.flash.eliminar.success", null, locale));
			return "redirect:/ver/" + factura.getCliente().getId();
		}
		flash.addFlashAttribute("error", messageSource.getMessage("text.factura.flash.db.error", null, locale));

		return "redirect:/listar";
	}

}
