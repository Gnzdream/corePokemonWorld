package com.zdream.test.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class TryXML {

	File file;

	// 定义工厂 API，使应用程序能够从 XML 文档获取生成 DOM 对象树的解析器。
	DocumentBuilderFactory dbf;

	// 定义 API， 使其从 XML 文档获取 DOM 文档实例。
	// 使用此类，应用程序员可以从 XML 获取一个 Document。
	DocumentBuilder dbuilder;

	// 它是文档树的根，并提供对文档数据的基本访问
	Document doc;

	Scanner scan;

	public TryXML() {
		scan = new Scanner(System.in);
		file = new File("assets\\xml\\write.xml");
	}

	public TryXML(String path) {
		scan = new Scanner(System.in);
		file = new File(path);
	}

	private void writeXMLFile() {
		file.listFiles();
		if (!file.exists()) {
			file.mkdirs();
		}

		dbf = DocumentBuilderFactory.newInstance();
		dbuilder = null;
		try {
			dbuilder = dbf.newDocumentBuilder();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		doc = dbuilder.newDocument();

		// main write
		Element root = doc.createElement("pokemon");
		doc.appendChild(root);

		Element stu = doc.createElement("student");
		stu.setAttribute("sex", "f");
		root.appendChild(stu);

		Element stu_name = doc.createElement("name");
		stu.appendChild(stu_name);
		Text name_text = doc.createTextNode("AAAAAA");
		stu_name.appendChild(name_text);

		Element stu_age = doc.createElement("age");
		stu.appendChild(stu_age);
		Text age_text = doc.createTextNode("25");
		stu_age.appendChild(age_text);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			callDomWriter(doc, osw, "UTF-8");
			osw.close();
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void callDomWriter(Document dom, Writer writer, String encoding) {
		try {
			Source source = new DOMSource(dom);
			Result res = new StreamResult(writer);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			xformer.transform(source, res);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TryXML writeins = new TryXML("assets\\xml\\write.xml");
		writeins.writeXMLFile();
		System.out.println("finished");
	}

}
