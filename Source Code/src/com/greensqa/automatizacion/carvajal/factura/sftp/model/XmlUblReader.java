package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.amazonaws.services.codebuild.model.Build;

import io.netty.util.internal.MathUtil;
import software.amazon.ion.Timestamp.Precision;

public class XmlUblReader {

//   cambiar nombre de la clase
//	public static void main(String[] arg) throws Exception {
//		getValuePathXmlUbl(
//				"C:\\Users\\dvalencia\\Documents\\Diana Valencia\\Documentos\\Archivos Configuración\\Cufe.xlsx",
//				"C:\\Users\\dvalencia\\Documents\\Test FECO\\Resultados\\FV_CPV10481.xml");
//	}

	/*
	 * Clase que permite obtener el valor del path ingresado por el usuario en el
	 * archivo de configuración con los datos de generación del CUFE
	 */

	public static String getValuePathXmlUbl(String pathConfiFile, File xmlFile) throws Exception {

		ArrayList<String> valuesPathCufe = new ArrayList<String>();
		ArrayList<String> xPathFile = ExcelReader.getValueFieldPosition(pathConfiFile, 0);
		String valuesItemsCufe = "";

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document cufeConfiFile = db.parse(new FileInputStream(xmlFile));

		// Obtener el valor contenido a partir de la Expresión Xpath ingresada en el archivo de configuración de excel. 

		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPathXmlUbl = xPathFactory.newXPath();
		xPathXmlUbl.setNamespaceContext(new NamespaceResolver(cufeConfiFile));
		String xPathCufe = "";
		XPathExpression xPathUblExpression = null;
		Object result = null;
		NodeList nodes = null;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < xPathFile.size(); i++) {
			xPathCufe = xPathFile.get(i);
			xPathUblExpression = xPathXmlUbl.compile(xPathCufe);
			result = xPathUblExpression.evaluate(cufeConfiFile, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int j = 0; j < nodes.getLength(); j++) {
				valuesPathCufe.add(nodes.item(j).getTextContent());
			}
		}

		// Reglas para generar el CUFE 
		
		//La fecha debe estar en el formato YYYYMMDD
		if (valuesPathCufe.get(1).matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")) {
			String date = valuesPathCufe.get(1).replaceAll("[-]", "");
			valuesPathCufe.set(1, date);
		}
		//La hora debe estar en el formato HHMMSS
		if (valuesPathCufe.get(2).matches("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)")) {
			String hour = valuesPathCufe.get(2).replaceAll(":", "");
			valuesPathCufe.set(2, hour);
		}

		//Los valores total, impuestos, etc deben estar en formato decimal separato por punto con dos decimales
		for (int i = 0; i < valuesPathCufe.size(); i++) {
			if (valuesPathCufe.get(i).matches("([0-9]{1,}[.][0-9]{1,})")) {
				if (!valuesPathCufe.get(i).matches("([0-9]{1,}[.][0-9]{2})")) {
					Double totalValueFact = Double.parseDouble(valuesPathCufe.get(i));
					DecimalFormat df = new DecimalFormat("#.00");
					String totalValueFactFormat = df.format(totalValueFact).replaceAll("[,]", ".");
					valuesPathCufe.set(i, totalValueFactFormat);
				}
			}
		}

		//Si la factura no contiene el impuesto con código 01, debe ponerlo y establecer como valor 0.00
		if (!valuesPathCufe.get(4).equals("01")) {
			valuesPathCufe.set(4, "01");
			valuesPathCufe.set(5, "0.00");
		}

		if (!valuesPathCufe.get(6).equals("02")) {
			valuesPathCufe.set(6, "02");
			valuesPathCufe.set(7, "0.00");
		}

		if (!valuesPathCufe.get(8).equals("03")) {
			valuesPathCufe.set(8, "03");
			valuesPathCufe.set(9, "0.00");
		}	

		for (int i = 0; i < valuesPathCufe.size(); i++) {
			valuesItemsCufe += valuesPathCufe.get(i);
		}

		return valuesItemsCufe;
	}
}

class NamespaceResolver implements NamespaceContext {
	// Store the source document to search the namespaces
	private Document sourceDocument;

	public NamespaceResolver(Document document) {
		sourceDocument = document;
	}

	// The lookup for the namespace uris is delegated to the stored document.
	public String getNamespaceURI(String prefix) {
		if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
			return sourceDocument.lookupNamespaceURI(null);
		} else {
			return sourceDocument.lookupNamespaceURI(prefix);
		}
	}

	public String getPrefix(String namespaceURI) {
		return sourceDocument.lookupPrefix(namespaceURI);
	}

	@SuppressWarnings("rawtypes")
	public Iterator getPrefixes(String namespaceURI) {
		return null;
	}

}
