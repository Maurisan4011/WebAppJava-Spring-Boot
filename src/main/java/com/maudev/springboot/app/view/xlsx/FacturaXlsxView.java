package com.maudev.springboot.app.view.xlsx;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.maudev.springboot.app.models.entity.Factura;
import com.maudev.springboot.app.models.entity.ItemFactura;

@Component("factura/ver.xlsx")
public class FacturaXlsxView extends AbstractXlsxView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// Cambiamos el nombre archivo a traves del objeto response(el nombre que le
		// querramos dar al documento )
		response.setHeader("Content-Disposition", "attachment; filename=\"factura_view.xlsx\"");

		// Obtenemos el model "factura" tiene que se igual al nombre declarado en el
		// controller
		Factura factura = (Factura) model.get("factura");

		// Otra forma mas secilla de traducir , heredando de la superclase
		// AbstractPdfView
		MessageSourceAccessor mensajes = getMessageSourceAccessor();

		// apartir de aqui armamos las hojas y/o planilla todo se importa de .poi.ss
		Sheet sheet = workbook.createSheet("Factura SpringBoot");

		Row row = sheet.createRow(0); // fila 1
		Cell cell = row.createCell(0); // Columna 1 fila 1
		// cell.setCellValue("Datos del Cliente");sin traducir
		cell.setCellValue(mensajes.getMessage("text.factura.ver.datos.cliente"));

		row = sheet.createRow(1); // fila 2
		cell = row.createCell(0); // Columna 1 Fila2
		cell.setCellValue(factura.getCliente().getNombre() + "  " + factura.getCliente().getApellido());

		row = sheet.createRow(2);// fila 3
		cell = row.createCell(0); // Columna 1 Fila3
		cell.setCellValue(factura.getCliente().getEmail());

		// mas sencillo encadenando metodos para en ves de crear el objeto row y cell

//		sheet.createRow(4).createCell(0).setCellValue("Datos de la Factura"); //sin usar traducciones 

		sheet.createRow(4).createCell(0).setCellValue(mensajes.getMessage("text.factura.ver.datos.factura"));

		// sin usar traducciones
//		sheet.createRow(5).createCell(0).setCellValue("Folio:  " + factura.getId());
//		sheet.createRow(6).createCell(0).setCellValue("Descripcion: " + factura.getDescripcion());
//		sheet.createRow(7).createCell(0).setCellValue("Fecha: " + factura.getCreateAt());

		// Otra forma de traducir usando MessageSourceAccessor mensajes =
		// getMessageSourceAccessor();
		sheet.createRow(5).createCell(0)
				.setCellValue(mensajes.getMessage("text.cliente.factura.folio") + " : " + factura.getId());
		sheet.createRow(6).createCell(0).setCellValue(
				mensajes.getMessage("text.cliente.factura.descripcion") + " : " + factura.getDescripcion());
		sheet.createRow(7).createCell(0)
				.setCellValue(mensajes.getMessage("text.cliente.factura.fecha") + " : " + factura.getCreateAt());

		CellStyle theaderStyle = workbook.createCellStyle(); // Tuneamos nuestra tabla con estilos lindos

		theaderStyle.setBorderBottom(BorderStyle.MEDIUM);
		theaderStyle.setBorderTop(BorderStyle.MEDIUM);
		theaderStyle.setBorderRight(BorderStyle.MEDIUM);
		theaderStyle.setBorderLeft(BorderStyle.MEDIUM);
		// Color de fondo e importamos indexed
		theaderStyle.setFillForegroundColor(IndexedColors.GOLD.index);
		// Aplicar el tipo de patron que vamos a ultilizar
		theaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		CellStyle tbodyStyle = workbook.createCellStyle();
		tbodyStyle.setBorderBottom(BorderStyle.THIN);
		tbodyStyle.setBorderTop(BorderStyle.THIN);
		tbodyStyle.setBorderRight(BorderStyle.THIN);
		tbodyStyle.setBorderLeft(BorderStyle.THIN);

		Row header = sheet.createRow(9);
		// sin usar traducciones
//		header.createCell(0).setCellValue("Producto");
//		header.createCell(1).setCellValue("Precio");
//		header.createCell(2).setCellValue("Cantidad");
//		header.createCell(3).setCellValue("Total");

		// forma de traducir usando MessageSourceAccessor mensajes =
		// getMessageSourceAccessor();
		header.createCell(0).setCellValue(mensajes.getMessage("text.factura.form.item.nombre"));
		header.createCell(1).setCellValue(mensajes.getMessage("text.factura.form.item.precio"));
		header.createCell(2).setCellValue(mensajes.getMessage("text.factura.form.item.cantidad"));
		header.createCell(3).setCellValue(mensajes.getMessage("text.factura.form.item.total"));

		// Aplicamos los estilos creados a cada celda
		header.getCell(0).setCellStyle(theaderStyle);
		header.getCell(1).setCellStyle(theaderStyle);
		header.getCell(2).setCellStyle(theaderStyle);
		header.getCell(3).setCellStyle(theaderStyle);

		int rownum = 10;

		for (ItemFactura item : factura.getItems()) {
			Row fila = sheet.createRow(rownum++); // incrementa pr cada item que exista
			cell = fila.createCell(0);
			cell.setCellValue(item.getProducto().getNombre());
			cell.setCellStyle(tbodyStyle);

			cell = fila.createCell(1);
			cell.setCellValue(item.getProducto().getPrecio());
			cell.setCellStyle(tbodyStyle);

			cell = fila.createCell(2);
			cell.setCellValue(item.getCantidad());
			cell.setCellStyle(tbodyStyle);

			cell = fila.createCell(3);
			cell.setCellValue(item.calcularImporte());
			cell.setCellStyle(tbodyStyle);

			header.getCell(0).setCellStyle(theaderStyle);
		}

		Row filatotal = sheet.createRow(rownum);
		cell = filatotal.createCell(2);

//		cell.setCellValue("Gran Total: ");//Sin traduccion 

		cell.setCellValue(mensajes.getMessage("text.factura.form.total") + " : ");
		cell.setCellStyle(tbodyStyle);

		cell = filatotal.createCell(3);
		cell.setCellValue(factura.getTotal());
		cell.setCellStyle(tbodyStyle);

	}

}
