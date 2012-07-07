package smile.xml;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import smile.xml.util.UtilJ;

@JRubyClass(name = "LibXML::XML::Parser" )
public class ParserJ extends RubyObject {
	
	private static final long serialVersionUID = 4634367713101505188L;
	
	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new ParserJ(runtime, klass);
		}
	};
	
	private RubyString fileName;
	private RubyString string;

	public static RubyClass define(Ruby runtime) {
		return UtilJ.defineClass(runtime, ParserJ.class, ALLOCATOR );
	}

	private static RubyClass getRubyClass(ThreadContext context) {
		return UtilJ.getClass(context.getRuntime(), ParserJ.class);
	}

	private static ParserJ newInstance(ThreadContext context) {
		IRubyObject[] args = {};
		return (ParserJ) getRubyClass( context ).newInstance(context, args, null);
	}

	public ParserJ(Ruby runtime, RubyClass metaClass) {
		super(runtime, metaClass);
	}

	public static ParserJ fromFile(ThreadContext context, IRubyObject pName,
			IRubyObject pHash) {
		IRubyObject[] args = { pName, pHash };
		return fromFile(context, getRubyClass(context), args );
	}

	@JRubyMethod(name = "file", module = true, required=1, optional=1)
	public static ParserJ fromFile(ThreadContext context, IRubyObject klass, IRubyObject[] args ) {
		
		if( args.length == 0 || args[0].isNil() )
			throw context.getRuntime().newTypeError("can't convert nil into String");
		
		RubyString name = (RubyString) args[0];

		if( new File( name.asJavaString() ).exists() == false )
			throw ErrorJ.newRaiseException(context, "Warning: failed to load external entity \"" + name.asJavaString() + "\".");
		
		if( args.length > 1 && args[1].isNil()==false ) {
			RubyHash hash = (RubyHash) args[1];
			hash.get(context.getRuntime().newSymbol("encoding"));
			hash.get(context.getRuntime().newSymbol("options"));
		}

		ParserJ parser = ParserJ.newInstance(context);
		parser.fileName = name;
		return parser;
	}


	@JRubyMethod(name = { "initialize" }, optional = 1)
	public void initialize(ThreadContext context, IRubyObject[] args) {
	}

	@JRubyMethod(name = { "io=" })
	public void setIo(ThreadContext context, IRubyObject io) {
		if( io.isNil() )
			throw context.getRuntime().newTypeError("Must pass in an IO object");
		RubyString string = (RubyString) io.callMethod(context, "read");
		this.string = string;
	}

	@JRubyMethod(name = { "string=" })
	public void setString(ThreadContext context, IRubyObject pString) {
		if( pString.isNil() ) {
			throw context.getRuntime().newTypeError("");
		}
		this.string = ((RubyString) pString);
	}

	@JRubyMethod(name = "context" )
	public IRubyObject getContext(ThreadContext context) throws Exception {
		return ParserContextJ.newInstance( context );
	}

	@JRubyMethod(name = { "document=" })
	public void setDocument(ThreadContext context, IRubyObject pDocument) throws Exception {
		DocumentJ document = (DocumentJ) pDocument;
		string = document.toString(context, new IRubyObject[]{} );
	}

	@JRubyMethod(name ="file=" )
	public void setFile(ThreadContext context, IRubyObject pDocument) throws Exception {
		string = (RubyString) UtilJ.toRubyString( context, pDocument );
	}

	@JRubyMethod(name = "parse")
	public DocumentJ parse(ThreadContext context) throws Exception {

		DocumentBuilder builder = UtilJ.getBuilder();
		Document doc = null;
		
		if (this.fileName != null) {
			doc = builder.parse(this.fileName.asJavaString());

		} else if (this.string != null) {
			StringReader reader = new StringReader(string.asJavaString());
			try {				
				doc = builder.parse(new InputSource(reader));
			} catch( SAXParseException e ) {
				throw ErrorJ.newRaiseException(context, e.getMessage() );
			} finally {
				reader.close();
			}
		} else {
			throw context.getRuntime().newTypeError("");
		}
		DocumentJ document = DocumentJ.newInstance(context);
		document.setJavaObject(doc);
		
		if (doc.getXmlEncoding() != null)
			document.setEncoding(context, context.getRuntime().newString(doc.getXmlEncoding()));
		
		return document;
	}

	@JRubyMethod(name = "document", module = true)
	public static ParserJ fromDocument(ThreadContext context, IRubyObject self, IRubyObject pDocument) throws Exception {
		if( pDocument.isNil() )
			throw context.getRuntime().newTypeError("Must pass an XML::Document object");
		
		DocumentJ document = (DocumentJ) pDocument;
		
		ParserJ parser = ParserJ.newInstance(context);
		parser.setDocument(context, document );
		return parser;

	}

	@JRubyMethod(name = "io", module = true)
	public static ParserJ fromIo(ThreadContext context, IRubyObject klass, IRubyObject io) throws Exception {
		if( io.isNil() )
			throw context.getRuntime().newTypeError("Must pass in an IO object");
		RubyString string = (RubyString) io.callMethod(context, "read");
		ParserJ parser = ParserJ.newInstance(context);
		parser.string = string;
		return parser;
	}

	@JRubyMethod(name ="io")
	public IRubyObject getIo(ThreadContext context) throws Exception {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "string", module = true, required=1, optional = 1)
	public static ParserJ fromString(ThreadContext context, IRubyObject klass, IRubyObject[] args) throws Exception {
		
		if( args.length == 0 || args[0].isNil() ) 
			throw context.getRuntime().newTypeError("wrong argument type nil (expected String)");
		
		RubyString string = (RubyString) args[0];
		RubyHash hash;
		if (args.length > 1) {
			hash = (RubyHash) args[1];
		}

		ParserJ parser = ParserJ.newInstance(context);
		parser.string = string;
		return parser;
	}

}