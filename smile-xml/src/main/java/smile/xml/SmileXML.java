package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import smile.xml.sax.SaxParserCallbacksJ;
import smile.xml.sax.SaxParserJ;
import smile.xml.xpath.XPathContextJ;
import smile.xml.xpath.XPathJ;
import smile.xml.xpath.XPathObjectJ;

public class SmileXML {
	public static void define(Ruby runtime) {
		
		try {
		RubyModule libxml = runtime.fastGetModule("LibXML");
		if (libxml == null) {
			libxml = runtime.defineModule("LibXML");
		}

		RubyModule xml = XmlJ.define(runtime);
//		RubyModule xml = (RubyModule) libxml.getConstantNoConstMissing("XML");
//		if (xml == null) {
//			xml = runtime.defineModuleUnder("XML", libxml);
//		}

		ParserJ.define(runtime);
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