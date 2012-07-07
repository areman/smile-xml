package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.compiler.ir.instructions.THROW_EXCEPTION_Instr;

import smile.xml.sax.SaxParserCallbacksJ;
import smile.xml.sax.SaxParserJ;
import smile.xml.xpath.XPathContextJ;
import smile.xml.xpath.XPathExpressionJ;
import smile.xml.xpath.XPathJ;
import smile.xml.xpath.XPathObjectJ;
import smile.xml.xpath.XPointerJ;

public class SmileXML {
	public static void define(Ruby runtime) {
		
		try {
		RubyModule libxml = runtime.fastGetModule("LibXML");
		if (libxml == null) {
			libxml = runtime.defineModule("LibXML");
		}

		RubyModule xml = XmlJ.define(runtime);

		ParserJ.define(runtime);
		ParserContextJ.define(runtime);
		ParserOptionsJ.define(runtime);
		DocumentJ.define(runtime);
		NodeJ.define(runtime);
		NodeSetJ.define(runtime);
		AttributesJ.define(runtime);
		AttrJ.define(runtime);

		SaxParserJ.define(runtime);
		SaxParserCallbacksJ.define(runtime);

		XPathJ.define(runtime);
		XPathObjectJ.define(runtime);
		XPathContextJ.define(runtime);		
		XPathExpressionJ.define(runtime);
		XPointerJ.define(runtime);
		
		EncodingJ.define(runtime);
		
		ErrorJ.define(runtime);

		RubyClass ns = NamespaceJ.define(runtime);
		NamespacesJ.define(runtime);

		SchemaJ.define(runtime);

		runtime.addModule(xml);

		xml.setConstant("NS", ns);
		
		runtime.defineGlobalConstant( "XML", xml );
		
		} catch( RuntimeException e ) {
			e.printStackTrace();
			throw e;
		}
	}
}