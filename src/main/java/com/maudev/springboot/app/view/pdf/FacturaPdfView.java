package com.maudev.springboot.app.view.pdf;

import java.awt.Color;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.maudev.springboot.app.models.entity.Factura;
import com.maudev.springboot.app.models.entity.ItemFactura;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private LocaleResolver localeResolver;

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		//Obtenemos el model "factura" tiene que se igual al nombre declarado en el controller 
		Factura factura = (Factura) model.get("factura");
		
		//una forma de  traducir  usando 
		Locale locale = localeResolver.resolveLocale(request);

		//Otra forma mas secilla de traducir , heredando de la superclase  AbstractPdfView
		MessageSourceAccessor mensajes = getMessageSourceAccessor();		
		
		
		// Cramos las tablas
		PdfPTable tabla = new PdfPTable(1); // tabla de una sola columna
		
		tabla.setSpacingAfter(20);

		PdfPCell cell = null;
   

		//una forma de  traducir  usando  Locale locale = localeResolver.resolveLocale(request);
		cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.cliente", null, locale)));
		cell.setBackgroundColor(new Color(184, 218, 255));
		cell.setPadding(8f);
		tabla.addCell(cell);

		tabla.addCell(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		tabla.addCell(factura.getCliente().getEmail());

		PdfPTable tabla2 = new PdfPTable(1);
		tabla2.setSpacingAfter(20);
	
		
		//una forma de  traducir  usando  Locale locale = localeResolver.resolveLocale(request);
		cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.factura", null, locale)));
		cell.setBackgroundColor(new Color(195, 230, 203));
		cell.setPadding(8f);
		tabla2.addCell(cell);
		//Otra forma de  traducir  usando  MessageSourceAccessor mensajes = getMessageSourceAccessor();
		tabla2.addCell(mensajes.getMessage("text.cliente.factura.folio") +  " : " + factura.getId());
		tabla2.addCell(mensajes.getMessage("text.cliente.factura.descripcion") + " : " + factura.getDescripcion());
		tabla2.addCell(mensajes.getMessage("text.cliente.factura.fecha") + " : " + factura.getCreateAt());

		document.add(tabla);
		document.add(tabla2);

		PdfPTable tabla3 = new PdfPTable(4); // tabla con cuatro comlumnas
		// Para ser mas preciso un arreglo float con decimales
		tabla3.setWidths(new float[] { 3.5f, 1, 1, 1 });// valores de la medida de cada columna
		//Otra forma de  traducir  usando  MessageSourceAccessor mensajes = getMessageSourceAccessor();
		tabla3.addCell(mensajes.getMessage("text.factura.form.item.nombre"));
		tabla3.addCell(mensajes.getMessage("text.factura.form.item.precio"));
		tabla3.addCell(mensajes.getMessage("text.factura.form.item.cantidad"));
		tabla3.addCell(mensajes.getMessage("text.factura.form.item.total"));

		for (ItemFactura item : factura.getItems()) {
			tabla3.addCell(item.getProducto().getNombre());
			tabla3.addCell(item.getProducto().getPrecio().toString());

			cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			tabla3.addCell(cell);
			tabla3.addCell(item.calcularImporte().toString());
		}
		//Otra forma de  traducir  usando  MessageSourceAccessor mensajes = getMessageSourceAccessor();
		cell = new PdfPCell(new Phrase(mensajes.getMessage("text.factura.form.total") + " : "));

		cell.setColspan(3);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		tabla3.addCell(cell);
		tabla3.addCell(factura.getTotal().toString());

		document.add(tabla3);

	}
}
