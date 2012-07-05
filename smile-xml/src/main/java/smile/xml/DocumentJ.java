package smile.xml;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyHash;
import org.jruby.RubyNil;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.javasupport.JavaObject;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import smile.xml.util.UtilJ;
import smile.xml.xpath.XPathContextJ;
import smile.xml.xpath.XPathObjectJ;

@JRubyClass( name="LibXML::XML::Document" )
public class DocumentJ extends BaseJ<Document> {
	private static final long serialVersionUID = 1585911078348739867L;
	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new DocumentJ(runtime, klass);
		}
	};
	
	private RubyString version;
	private EncodingJ encoding;

	public static RubyClass define(Ruby runtime) {		
		return UtilJ.defineClass(runtime, DocumentJ.class, ALLOCATOR);
	}

	private static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ.getClass(runtime, DocumentJ.class );
	}

	public static DocumentJ newInstance(ThreadContext context) {
		return (DocumentJ) getRubyClass(context.getRuntime()).newInstance(
				context, new IRubyObject[0], null);
	}

	@JRubyMethod(name = { "file" }, module = true)
	public static DocumentJ fromFile(ThreadContext context, IRubyObject klass,
			IRubyObject pFile) throws Exception {
		ParserJ parser = ParserJ.fromFile(context, pFile,
				RubyHash.newHash(context.getRuntime()));

		return parser.parse(context);
	}

	@JRubyMethod(name = { "io" }, module = true)
	public static DocumentJ fromIo(ThreadContext context, IRubyObject klass,
			IRubyObject pIo) throws Exception {
		ParserJ parser = ParserJ.fromIo(context, null, pIo);
		return parser.parse(context);
	}

	@JRubyMethod(name = { "string" }, module = true)
	public static DocumentJ fromString(ThreadContext context,
			IRubyObject klass, IRubyObject pString) throws Exception {
		IRubyObject[] args = { pString };
		ParserJ parser = ParserJ.fromString(context, null, args);
		return parser.parse(context);
	}

	public DocumentJ(Ruby runtime, RubyClass klass) {
		super(runtime, klass);
	}

	@JRubyMethod(name = { "initialize" }, optional = 1)
	public void initialize(ThreadContext context, IRubyObject[] args) {
		if (args.length > 0) {
			this.version = (RubyString) args[0];
		} else {
			this.version = context.getRuntime().newString("1.0");
		}
	}

	@JRubyMethod( name="node_type" )
	public IRubyObject getNodeType( ThreadContext context ) {
		return context.getRuntime().newFixnum( NodeJ.DOCUMENT_NODE );
	}
	
	@JRubyMethod(name = { "root=" })
	public void setRoot(ThreadContext context, IRubyObject pRoot) throws Exception {
		NodeJ node = (NodeJ) pRoot;
		
		if( node.isDocPresent() && getJavaObject().equals( node.getJavaObject().getOwnerDocument() ) == false )
			throw ErrorJ.newRaiseException(context, " Nodes belong to different documents.  You must first import the node by calling XML::Document.import.");
		
		if (getJavaObject() == null) {
			setJavaObject( UtilJ.getBuilder().newDocument() );		
		} else if( getJavaObject().getDocumentElement() != null) {
			getJavaObject().removeChild( getJavaObject().getDocumentElement() );
		}
		//System.out.println( UtilJ.toString( node.getJavaObject(), true ) );
		getJavaObject().appendChild( getJavaObject().adoptNode( node.getJavaObject() ) );
	}

	@JRubyMethod(name = { "root" })
	public NodeJ getRoot(ThreadContext context) {
		NodeJ node = NodeJ.newInstance(context);
		node.setJavaObject(((Document) getJavaObject()).getDocumentElement());
		node.setDocPresent(true);
		return node;
	}

	@JRubyMethod(name = "import")
	public NodeJ adoptNode(ThreadContext context, IRubyObject pNode) {
		
		NodeJ node = (NodeJ) pNode;
		Node newNode = getJavaObject().adoptNode( node.getJavaObject().cloneNode(false) );

		NodeJ result = NodeJ.newInstance(context);
		result.setJavaObject( newNode );
		result.setDocPresent(true);
		return result;
	}
	
	@JRubyMethod(name = { "node_type_name" })
	public RubyString getNodetypeName(ThreadContext context) throws Exception {
		
		return context.getRuntime().newString("document_xml");
	}

	@JRubyMethod(name ="canonicalize", rest=true )
	public RubyString canonicalize(ThreadContext context, IRubyObject[] args ) throws Exception {
		
		boolean keepComment = false;
		
		if( args.length > 0 && args[0] instanceof RubyBoolean ) {
			keepComment = ((RubyBoolean)args[0]).isTrue();
			
		}
		
		String string = toString();
		string = string.replace("\r", "" );

		if( keepComment == false )
			for( int i = string.indexOf("<!--"); i != -1; i = string.indexOf("<!--") ) {
				int j = string.indexOf("-->");
				string = string.substring( 0, i ) + string.substring(j+3);
			}

		if( string.trim().startsWith("<?xml") ) {
			int i = string.indexOf( "?>" );
			string = string.substring( i+2 );
		}
		
		return context.getRuntime().newString(string);
	}

	@JRubyMethod(name = { "child" })
	public NodeJ getChild(ThreadContext context) {
		NodeJ node = NodeJ.newInstance(context);
		node.setJavaObject(((Document) getJavaObject()).getFirstChild());
		node.setDocPresent( true );
		return node;
	}

	@JRubyMethod( name = "child?" )
	public RubyBoolean hasChild(ThreadContext context) {
		return UtilJ.toBool( context, getJavaObject().getFirstChild() != null );
	}

	@JRubyMethod(name = "compression" )
	public RubyFixnum getCompression(ThreadContext context) {
		// TODO
		return context.getRuntime().newFixnum(0);
	}

	@JRubyMethod(name = { "compression=" })
	public void setCompression(ThreadContext context, IRubyObject value) {
		// TODO
	}

	@JRubyMethod(name = { "compression?" })
	public RubyBoolean hasCompression(ThreadContext context) {
		return UtilJ.toBool(context, false);
	}

	@JRubyMethod(name ="context", optional=1)
	public IRubyObject getContext(ThreadContext context, IRubyObject[] args ) {
		if( args.length > 0 ) {
			NamespacesJ namespaces = (NamespacesJ) args[0];
		}

		return XPathContextJ.newInstance(context, this);
	}

	@JRubyMethod( name="encoding" )
	public IRubyObject getEncoding(ThreadContext context) {
		return this.encoding;
	}

	@JRubyMethod( name="encoding=" )
	public void setEncoding(ThreadContext context, IRubyObject value) {
		if( value instanceof RubyString ) {
			this.encoding = EncodingJ.newInstance(context, (RubyString) value );
		} else if (value instanceof EncodingJ) {
			this.encoding = (EncodingJ) value;
		} else if (value instanceof RubyNil ) {
			this.encoding = null;
		} else {
			throw context.getRuntime().newArgumentError("unsupported " + value.getMetaClass().getName() );
		}
	}

	@JRubyMethod(name = { "find" }, required = 1, optional = 1)
	public IRubyObject find(ThreadContext context, IRubyObject[] args) {
		RubyString xpath = (RubyString) args[0];

		IRubyObject[] namespaces = UtilJ.toStringArray(context, args, 1);

		return XPathObjectJ.newInstance(context, xpath, this, namespaces);
	}

	@JRubyMethod(name = { "version" })
	public IRubyObject getVersion(ThreadContext context) throws Exception {
		if( this.version == null )
			return context.getRuntime().getNil();
		return this.version;
	}

				
	@JRubyMethod(name ="xhtml?")
	public IRubyObject isXhtml(ThreadContext context) throws Exception {
		// TODO
		return UtilJ.toBool(context, false);
	}

	@JRubyMethod(name = { "find_first" }, optional = 1)
	public IRubyObject findFirst(ThreadContext context, IRubyObject[] args)
			throws Exception {
		RubyString xpath = (RubyString) args[0];
		if (args.length > 0)
			;
		return XPathObjectJ.newInstance(context, xpath, this,
				new IRubyObject[0]).getFirst(context);
	}

	@Override
	public String toString() {
		return UtilJ.toString( getJavaObject(), true, encoding );
	}

	@JRubyMethod(name="to_s", rest=true)
	public RubyString toString(ThreadContext context, IRubyObject[] args ) throws Exception {
		RubyBoolean indent;
		EncodingJ   encoding = null;
		if( args.length > 0 ) {
			if( args[0] instanceof RubyHash ) {
				RubyHash hash = (RubyHash) args[0];
				RubySymbol key = context.getRuntime().newSymbol( "indent" );
				indent = toRubyBool( context, hash.get( key ) );
				key = context.getRuntime().newSymbol( "encoding" );
				encoding = EncodingJ.get(context, hash.get( key ) );
			} else if( args[0] instanceof RubyBoolean ) {
				indent = (RubyBoolean) args[0];
			} else if( args[0] instanceof RubyNil ) {
				
			} else {
				throw context.getRuntime().newArgumentError("");
			}
		}

		String string = UtilJ.toString( getJavaObject(), true, encoding );
		return context.getRuntime().newString(string);
	}

	private RubyBoolean toRubyBool( ThreadContext context, Object obj ) {
		if( obj instanceof RubyBoolean ) {
			return (RubyBoolean) obj;
		}
		if( obj instanceof Boolean ) {
			return context.getRuntime().newBoolean( (Boolean) obj );
		}
		if( obj == null ) {
			return null;
		}
		if( obj == null || obj instanceof RubyNil ) {
			return null;
		}
		throw context.getRuntime().newArgumentError("");			
	}
	
	@JRubyMethod(name = { "eql?" }, alias = { "==", "equal?" })
	public RubyBoolean isEql(ThreadContext context, IRubyObject arg) {
		boolean r = (arg instanceof BaseJ) ? ((BaseJ) arg).getJavaObject()
				.equals(getJavaObject()) : false;

		if (!r) {
			r = toString().equals( arg.toString() );
		}
		return r ? context.getRuntime().getTrue() : context.getRuntime()
				.getFalse();
	}

	@JRubyMethod(name = { "validate_schema" }, optional = 1)
	public IRubyObject validateSchema(final ThreadContext context,
			IRubyObject[] args, final Block block) throws IOException {
		SchemaJ schema = (SchemaJ) args[0];

		Validator validator = ((Schema) schema.getJavaObject()).newValidator();
		final Block handler = ErrorJ.getErrorHandler(context, null);
		final AtomicBoolean b = new AtomicBoolean(true);
		if ((handler != null) || (block != null))
			validator.setErrorHandler(new ErrorHandler() {
				public void warning(SAXParseException exception)
						throws SAXException {
					RubyString message = context.getRuntime().newString(
							exception.getMessage());

					IRubyObject obj = JavaObject.wrap(context.getRuntime(),
							exception);

					if (handler != null)
						handler.yield(context, obj);
					if (block != null) {
						block.yield(context, RubyArray.newArray(
								context.getRuntime(), new IRubyObject[] {
										message, obj }));
					}

					b.set(false);
				}

				public void fatalError(SAXParseException exception)
						throws SAXException {
					RubyString message = context.getRuntime().newString(
							exception.getMessage());

					IRubyObject obj = JavaObject.wrap(context.getRuntime(),
							exception);

					if (handler != null)
						handler.yield(context, obj);
					if (block != null) {
						block.yield(context, RubyArray.newArray(
								context.getRuntime(), new IRubyObject[] {
										message, obj }));
					}

					b.set(false);
				}

				public void error(SAXParseException exception)
						throws SAXException {
					RubyString message = context.getRuntime().newString(
							exception.getMessage());

					IRubyObject obj = JavaObject.wrap(context.getRuntime(),
							exception);

					if (handler != null)
						handler.yield(context, obj);
					if (block != null) {
						block.yield(context, RubyArray.newArray(
								context.getRuntime(), new IRubyObject[] {
										message, obj }));
					}

					b.set(false);
				}
			});
		try {
			validator.validate(new DOMSource((Node) getJavaObject()));
		} catch (SAXException e) {
			throw context.getRuntime().newStandardError(e.getMessage());
		}

		return b.get() ? context.getRuntime().getTrue() : context.getRuntime()
				.getFalse();
	}
}