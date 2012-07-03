package smile.xml.sax;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

import smile.xml.ErrorJ;
import smile.xml.util.UtilJ;

public class SaxParserJ extends RubyObject {
	
	private static final long serialVersionUID = -8712513236401964568L;
	
	public static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new SaxParserJ(runtime, klass);
		}
	};
	
	public static RubyClass define(Ruby runtime) {
		RubyModule module = UtilJ.getModule(runtime, "LibXML", "XML" );
		RubyClass result = module.defineClassUnder( "SaxParser", runtime.getObject(), ALLOCATOR );
		result.defineAnnotatedMethods(SaxParserJ.class);
		return result;
	}

	public static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ.getClass(runtime, "LibXML", "XML", "SaxParser" );
	}

	@JRubyMethod(name = { "string" }, module = true)
	public static IRubyObject fromString(ThreadContext context, IRubyObject self, IRubyObject pString) {
		
		if (pString.isNil())
			throw context.getRuntime().newTypeError( "wrong argument type nil (expected String)" );

		SaxParserJ parser = new SaxParserJ(context);
		parser.string = ((RubyString) pString);
		return parser;
	}

	@JRubyMethod(name = "file", module = true)
	public static IRubyObject fromFile(ThreadContext context, IRubyObject self,
			IRubyObject pFile) {

		if (pFile.isNil())
			throw context.getRuntime().newTypeError( "can't convert nil into String" );

		if (new File(pFile.asJavaString()).exists() == false)
			throw ErrorJ.newRaiseException( context, "Warning: failed to load external entity \"" + pFile.asJavaString() + "\".");

		SaxParserJ parser = new SaxParserJ(context);
		parser.fileName = ((RubyString) pFile);
		return parser;
	}

	private RubyString string;
	private RubyString fileName;
	private IRubyObject callbacks;

	@JRubyMethod(name = { "io" }, module = true)
	public static IRubyObject fromIo(ThreadContext context, IRubyObject self, IRubyObject pIo) {
		SaxParserJ parser = new SaxParserJ(context);
		parser.string = ((RubyString) pIo.callMethod(context, "read"));
		return parser;
	}

	public SaxParserJ(Ruby runtime, RubyClass metaClass) {
		super(runtime, metaClass);
	}

	public SaxParserJ(ThreadContext context) {
		this(context.getRuntime(), getRubyClass(context.getRuntime()));
	}

	@JRubyMethod(name = { "initialize" }, optional = 1)
	public IRubyObject initialize(ThreadContext context, IRubyObject[] args) {
		return this;
	}

	@JRubyMethod(name = { "filename=" })
	public void setFilename(ThreadContext context, IRubyObject pFileName) {
		this.fileName = ((RubyString) pFileName);
	}

	@JRubyMethod(name = { "string=" })
	public void setString(ThreadContext context, IRubyObject pFileName) {
		this.string = ((RubyString) pFileName);
	}

	@JRubyMethod(name = { "string" })
	public IRubyObject getString(ThreadContext context) {
		return this.string;
	}

	@JRubyMethod(name = { "callbacks=" })
	public void setCallbacks(ThreadContext context, IRubyObject pCallbacks) {
		this.callbacks = pCallbacks;
	}

	@JRubyMethod(name = { "callbacks" })
	public IRubyObject getCallbacks(ThreadContext context) {
		return this.callbacks;
	}

	@JRubyMethod(name = { "parse" })
	public IRubyObject parse(ThreadContext context) throws SAXException,
			IOException {
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		InputSource inputSource;
		if (this.string != null) {
			inputSource = new InputSource(new StringReader(
					this.string.asJavaString()));
		} else {
			FileReader reader = new FileReader(this.fileName.asJavaString());
			inputSource = new InputSource(reader);
		}

		xmlReader.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				return new InputSource(new StringReader(""));
			}
		});
		if (callbacks != null && callbacks.isNil() == false)
			xmlReader.setContentHandler(new CallbackHandler(context, callbacks));
		
		xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler",xmlReader.getContentHandler());
		try {
			xmlReader.parse(inputSource);
		} catch (SAXException e) {
			throw ErrorJ.newRaiseException(context, e.getMessage());
		}

		return context.getRuntime().getTrue();
	}

}