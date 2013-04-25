package smile.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import smile.xml.xpath.XPathExpressionJ;
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
		ParserJ parser = ParserJ.fromFile(context, pFile,RubyHash.newHash(context.getRuntime()));

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

	@JRubyMethod(name ="child")
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
		return this.encoding == null ? null : this.encoding.toConstant(context);
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
		
		if( args.length == 0 || ( ( args[0] instanceof RubyString ) == false && ( args[0] instanceof XPathExpressionJ ) == false ) ) {
			
			throw context.getRuntime().newTypeError("Argument should be an intance of a String or XPath::Expression");
		}
		
		IRubyObject xpath = args[0];

		IRubyObject[] namespaces;
		if( args.length > 1 && ( args[1] instanceof RubyString || args[1] instanceof RubyArray ) )
			namespaces = UtilJ.toStringArray(context, args, 1);
		else if( args.length > 1 && args[1] instanceof RubyHash ) {
			Map map = (Map) args[1];
			List<IRubyObject> list = new ArrayList<IRubyObject>(map.size());
			for( Object o : map.entrySet() ) {
				Map.Entry e = (Entry) o;
				String k = toJavaString(context, e.getKey() );
				String v = toJavaString(context, e.getValue() );
				list.add( toRubyString(context, k + ":" + v ) );
			}
			namespaces = list.toArray( new IRubyObject[ list.size() ] );
		} else {
			namespaces = new IRubyObject[]{};
		}

		return XPathObjectJ.newInstance( context, xpath, this, namespaces );
	}

	@JRubyMethod(name = "debug" )
	public IRubyObject debug(ThreadContext context) {
		return context.getRuntime().getFalse();
	}

	@JRubyMethod(name = "save", rest=true )
	public IRubyObject save(ThreadContext context, IRubyObject[] args ) throws IOException {
		
		RubyString file = (RubyString) args[0];
		Boolean indent  = null;
		String encoding = null;
		if( args.length > 1 ) {
			RubyHash hash = (RubyHash) args[1];
			Object tmp = hash.get( context.getRuntime().newSymbol("indent") );
			indent   = UtilJ.toJavaBoolean( context, tmp );
			tmp = hash.get( context.getRuntime().newSymbol("encoding") );
			encoding = UtilJ.toJavaString( context, tmp );
		}
		
		File f = new File( file.asJavaString() );
		FileWriter writer = new FileWriter( file.asJavaString() );
		try {
			UtilJ.write(writer, getJavaObject(), nvl( indent, true ), nvl( encoding, EncodingJ.UTF_8 ) );
		} finally {
			writer.close();
		}
		
		return context.getRuntime().newFixnum( f.length() );
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
		Boolean indent = null;
		EncodingJ encoding = null;
		if( args.length > 0 ) {
			if( args[0] instanceof RubyHash ) {
				RubyHash hash = (RubyHash) args[0];
				RubySymbol key = context.getRuntime().newSymbol( "indent" );
				indent = UtilJ.toJavaBoolean(context, hash.get(key));
				key = context.getRuntime().newSymbol( "encoding" );
				encoding = EncodingJ.get(context, hash.get( key ) );
			} else if( args[0] instanceof RubyBoolean ) {
				indent = UtilJ.toJavaBoolean(context, args[0]);
			} else if( args[0] instanceof RubyNil ) {
				// do nothing
			} else {
				throw context.getRuntime().newArgumentError("");
			}
		}

		String string = UtilJ.toString( getJavaObject(), nvl(indent, true), encoding );
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
		if( handler != null || ( block != null && block.isGiven() ) )
			validator.setErrorHandler(new ErrorHandler() {
				
				private void callHandler( IRubyObject message, IRubyObject exception ) {
					IRubyObject[] args = { message, exception };
					handler.call(context, args, null );
				}
				
				private void callBlock( IRubyObject exception ) {
					IRubyObject[] args = { exception };
					block.call(context, args, null );
				}
				
				private void invoke( Exception exception ) {
					RubyString message = context.getRuntime().newString( exception.getMessage());
					IRubyObject obj = ErrorJ.newInstance( context, exception.getMessage() );
					if( block != null && block.isGiven() )
						callBlock( obj );
					if( handler != null && handler.isGiven() )
						callHandler(message, obj );

				}

				public void warning(SAXParseException exception)
						throws SAXException {
					
					invoke( exception );
					b.set(false);
				}

				public void fatalError(SAXParseException exception)
						throws SAXException {

					invoke( exception );
					b.set(false);
				}

				public void error(SAXParseException exception)
						throws SAXException {

					invoke( exception );
					b.set(false);
				}
			});
		try {
			validator.validate(new DOMSource(getJavaObject() ));
		} catch (SAXException e) {
			throw ErrorJ.newRaiseException(context, e.getMessage());
		}

		return b.get() ? context.getRuntime().getTrue() : context.getRuntime()
				.getFalse();
	}
}