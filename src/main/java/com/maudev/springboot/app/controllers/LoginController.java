package com.maudev.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	
	@GetMapping("/login")
	public String login(@RequestParam (value = "error", required =false)String error,
			Model model, Principal principal, RedirectAttributes flash) {
		
		if (principal != null) {			
			flash.addFlashAttribute("info", "Usted incio sesion anteriormente.Msj desde backend");
			return "redirect:/";
		}
		
		if (error != null) {
			model.addAttribute("error", "Error en el Login: Nombre de usuario o contraseña INCORRECTA, por favor vuelva a intentarlo.Msj desde BackEnd");
			
		}
		return"login";
	}

}
