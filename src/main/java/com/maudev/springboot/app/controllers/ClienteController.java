package com.maudev.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import org.apache.tomcat.util.http.fileupload.UploadContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.maudev.springboot.app.models.entity.Cliente;
import com.maudev.springboot.app.models.service.IClienteService;
import com.maudev.springboot.app.models.service.IUploadFileService;
import com.maudev.springboot.app.util.paginator.PageRender;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
//@RestController
@SessionAttributes("cliente")
@Api(tags = "cliente")
public class ClienteController {

	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileService;

	@Secured({"ROLE_USER"})
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}
    //Otra manera de validar el rol
//	@Secured("ROLE_USER")
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = clienteService.fetchByIdWithFacturas(id); // findOne(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos. msj desde backend");
			return "redirect:/listar";
		}

		model.put("cliente", cliente);
		model.put("titulo", "Detalle cliente: " + cliente.getNombre());
		return "ver";
	}

	@ApiOperation(value = "listar", notes = "Listas de Clientes")
	@GetMapping(value = { "/listar", "/" })
	// @RequestMapping(value = {"/listar", "/"}, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication,
			HttpServletRequest request) {

		if (authentication != null) {
			logger.info("Hola Usuario autenticado , tu username es: ".concat(authentication.getName()));
		}

		// Ejemplo de forma estatica
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			logger.info(
					"Utilizando de forma estatica SecurityContextHolder.getContext().getAuthentication();Usuario autenticado , tu username es: "
					.concat(auth.getName()));
		}

		if (hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat("Tenes Acceso!!"));
		} else {
			logger.info("Hola  ".concat(auth.getName()).concat(" NO Tenes Acceso!!"));
		}
		
		//Otra manera 
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "");
		
		if (securityContext.isUserInRole("ROLE_ADMIN")) {
			logger.info("FORMA USANDO SecurityContextHolderAwareRequestWrapper: Hola  ".concat(auth.getName()).concat("Tenes Acceso!!"));
		}else {
			
			logger.info("FORMA USANDO SecurityContextHolderAwareRequestWrapper: Hola  ".concat(auth.getName()).concat(" NO Tenes Acceso!!"));
		}
		
		
		//Otra manera 
		if (request.isUserInRole("ROLE_ADMIN")) {
			logger.info("FORMA USANDO HttpServletRequest nativo: Hola  ".concat(auth.getName()).concat("Tenes Acceso!!"));
		}else {
			
			logger.info("FORMA USANDO HttpServletRequest nativo: Hola  ".concat(auth.getName()).concat(" NO Tenes Acceso!!"));
		}
		
		

		Pageable pageRequest = PageRequest.of(page, 10);

		Page<Cliente> clientes = clienteService.findAll(pageRequest);

		PageRender<Cliente> pageRender = new PageRender<Cliente>("/listar", clientes);
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "listar";
	}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model) {

		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		return "form";
	}

	/*	@Secured("ROLE_ADMIN")OTRA MANERA DE VALIDAR EL ROL */
	
	@ApiOperation(value = "formulario", notes = "Formulario de Cliente")
	@RequestMapping(value = "/form/{id}")

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String editar(
			@ApiParam(name = "id", type = "Long", value = "id", example = "1", required = true) @PathVariable(value = "id") Long id,
			Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = null;

		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD!msj desde backend");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser cero!. msj desde backend");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		return "form";
	}

	@ApiOperation(value = "guardar", notes = "Guardar nuevo cliente	")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "cliente guardado") })
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	@Secured("ROLE_ADMIN")
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}

		if (!foto.isEmpty()) {
			if (cliente.getId() != null && cliente.getId() > 1 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 1) {

				uploadFileService.delete(cliente.getFoto());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info",
					"Has subido correctamente '" + uniqueFilename + "'" + ". msj desde backend");

			cliente.setFoto(uniqueFilename);
		}

		String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con éxito!. msj desde backend"
				: "Cliente creado con éxito!. msj desde backend";

		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listar";
	}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);

			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito!.msj desde backend");

			if (uploadFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminada con exito!msj desde backend");
			}

		}
		return "redirect:/listar";
	}
	// Roles de Usurio desde el Controller

	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return false;
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return false;
		}

		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		return authorities.contains(new SimpleGrantedAuthority(role));

		/*
		 * Una manera
		 * 
		 * for (GrantedAuthority authority : authorities) { if
		 * (role.equals(authority.getAuthority())) {
		 * logger.info("Hola Usuario ".concat(auth.getName()).concat(" Tu Rol es:"
		 * .concat(authority.getAuthority()))); return true; } } return false;
		 **/

	}

}