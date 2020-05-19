package com.maudev.springboot.app.view.csv;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.maudev.springboot.app.models.entity.Cliente;

@Component("listar")
public class ClienteCsvView extends AbstractView {

	// Base del archivo plano csv
	public ClienteCsvView() {
		setContentType("text/csv");
	}

	// Base del archivo plano csv
	@Override
	protected boolean generatesDownloadContent() {

		return true;
	}

	// Base del archivo plano csv
	@Override
	@SuppressWarnings("unchecked")
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

		// Asignar un nombre a nuestro archivo plano
		response.setHeader("Content-Disposition", "attachment; filename=\"clientes.csv\"");

		// pasamos el ContentType a la respuesta
		response.setContentType(getContentType());
		// obtenemos clientes atra ves del model y castearlo
	
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");

		ICsvBeanWriter beanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		// crear un arreglo de string de la clase que se va a convertir

		String[] header = { "id", "nombre", "apellido", "email", "createAt" };
		beanWriter.writeHeader(header);

		for (Cliente cliente : clientes) {
			beanWriter.write(cliente, header);

		}
		beanWriter.close();
	}
}
