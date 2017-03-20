package com.zdream.test.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TryReadXML {
	File file;
	DocumentBuilder db;
	Document doc;
	NodeList pmList;
	Node node;
	Element ele;

	TryReadXML(String path) {
		file = new File(path);
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = db.parse(file);
			pmList = doc.getElementsByTagName("pm");

			System.out.println("共有" + pmList.getLength() + "个PM！");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		node = pmList.item(0);
		ele = (Element) node;

		// node.getNodeName() pm
		// ELEMENT_NODE 1
		// ENTITY_NODE 6
		// ENTITY_REFERENCE_NODE 5
		// COMMENT_NODE 8
		// DOCUMENT_TYPE_NODE 10
		// TEXT_NODE 3

		if (node.getNodeType() == Node.ENTITY_NODE) {
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		} else {
			System.out.println(node.getAttributes().getNamedItem("identifier").getNodeValue());
			System.out.println(node.getAttributes().getNamedItem("identifier"));

			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				System.out.println(i + ": " + node.getChildNodes().item(i));

				if (node.getChildNodes().item(i).getNodeName().equals("name")) {
					System.out.println(node.getChildNodes().item(i).getTextContent());
				}

				if (node.getChildNodes().item(i).getNodeName().equals("property1")) {
					System.out.println(node.getChildNodes().item(i).getTextContent());
				}
			}

		}

	}

	public static void main(String[] args) {
		new TryReadXML("assets\\PmData\\pm_data.xml");
		System.out.println();
		for (int i = 0; i < args.length; i++) {

		}
	}

}
