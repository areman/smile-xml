package smile.xml.util;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import jline.Terminal;

import org.jruby.Ruby;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyNil;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyModule;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import smile.xml.AttrJ;
import smile.xml.EncodingJ;

public class UtilJ {
	private static final SchemaFactory schemaFactoryInstance = SchemaFactory
			.newInstance( "http://www.w3.org/2001/XMLSchema");

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory
			.newInstance();
	private static final TransformerFactory transformerFactory;
	private static final XPathFactory xpathFactory;
	private static final ThreadLocal<DocumentBuilder> builderLocal;
	private static final ThreadLocal<Transformer> transformerLocal;

	public static TransformerFactory getTransformerFactory() {
		return transformerFactory;
	}

	public static DocumentBuilderFactory getDocumentBuilderFactory() {
		return factory;
	}

	public static DocumentBuilder getBuilder() {
		return (DocumentBuilder) builderLocal.get();
	}

	public static Transformer getTransformer() {
		return (Transformer) transformerLocal.get();
	}

	public static XPathFactory getXPathFactory() {
		return xpathFactory;
	}

	public static XPath newXPath() {
		return getXPathFactory().newXPath();
	}

	public static SchemaFactory getSchemaFactoryInstance() {
		return schemaFactoryInstance;
	}

	private static List<String> split( String name ) {
		List<String> list = new ArrayList<String>();
		int i=0;
		for( int j=name.indexOf("::"); j!=-1; j=name.indexOf("::", i )  ) {
			list.add( name.substring(i,j) );
			i = j+2;
		}
		list.add( name.substring(i) );
		return list;
	}
	
	public static RubyClass defineClass( Ruby runtime, Class<? extends RubyObject> klass, ObjectAllocator allocator ) {

		JRubyClass anno     = klass.getAnnotation( JRubyClass.class );		
		List<String> path   = split( anno.name()[0] );
		String name         = path.remove( path.size()-1 );
		List<String> parent = split( anno.parent() );
		
		RubyModule module = getModule( runtime, path );
		RubyClass result = module.defineClassUnder( name, getClass(runtime, parent), allocator );
		
		for( String i : anno.include() ) {
			result.includeModule( getModule(runtime, split(i) ) );
		}
		
		result.defineAnnotatedMethods( klass );
		
		result.defineAnnotatedConstants( klass );
		 
		return result;
	}

	public static RubyModule defineModule( Ruby runtime, Class<?> klass ) {

		JRubyModule anno    = klass.getAnnotation( JRubyModule.class );		
		List<String> path   = split( anno.name()[0] );
		String name         = path.remove( path.size()-1 );
		
		RubyModule module = getModule( runtime, path );
		RubyModule result = module.defineModuleUnder( name );
		
		for( String i : anno.include() ) {
			result.includeModule( getModule(runtime, split(i) ) );
		}
		
		result.defineAnnotatedMethods( klass );
		result.defineAnnotatedConstants( klass );
		
		return result;
	}

	public static IRubyObject toRubyString( ThreadContext context, Object obj ) {
		
		if( obj == null || obj instanceof RubyNil )
			return context.getRuntime().getNil();
		
		if( obj instanceof RubyString )
			return (RubyString) obj;
		
		if( obj instanceof String ) 
			return context.getRuntime().newString( (String) obj );
		
		throw context.getRuntime().newTypeError("");
	}

	public static String toJavaString( ThreadContext context, Object obj ) {
		return toJavaString(context, obj, null );
	}
	
	public static <T> T nvl( T obj, T defaultValue ) {
		return obj == null ? defaultValue : obj;
	}
	
	public static String toJavaString( ThreadContext context, Object obj, String defaultValue ) {
		
		if( obj == null || obj instanceof RubyNil )
			return defaultValue;
		
		if( obj instanceof RubyString )
			return ((RubyString) obj).asJavaString();

		if( obj instanceof RubySymbol )
			return ((RubySymbol) obj).asJavaString();

		if( obj instanceof String ) 
			return (String) obj;
		
		throw context.getRuntime().newTypeError("");
	}

	public static RubyModule getModule(Ruby runtime, String...path) {
		return getModule(runtime, Arrays.asList( path ) );
	}
	
	public static RubyModule getModule(Ruby runtime, List<String> path) {
		
		if( path.isEmpty() )
			return runtime.getObject();
		
		RubyModule m = runtime.getModule( path.get(0) ) ;

		for (int i = 1; i < path.size(); i++) {
			IRubyObject tmp = m.getConstant( path.get(i) );
			if (tmp == null) {
				tmp = m.defineModuleUnder( path.get(i) );
			}
			m = (RubyModule) tmp;
		}

		return m;
	}

	public static RubyClass getClass(Ruby runtime, Class<? extends RubyObject> klass ) {
		
		JRubyClass anno     = klass.getAnnotation( JRubyClass.class );		
		List<String> path   = split( anno.name()[0] );
		return getClass( runtime, path );
	}
	
	public static RubyClass getClass(Ruby runtime, String...path) {
		return getClass(runtime, Arrays.asList(path) );
	}
	
	public static RubyClass getClass(Ruby runtime, List<String> path ) {
		if (path.size() == 1) {
			return runtime.getClass( path.get(0) );
		}
		RubyModule m = runtime.getModule( path.get(0) );

		for (int i = 1; i < path.size() - 1; i++) {
			IRubyObject tmp = m.getConstant( path.get(i) );
			if (tmp == null) {
				tmp = m.defineModuleUnder( path.get(i) );
			}
			m = (RubyModule) tmp;
		}

		return m.getClass( path.get( path.size() - 1) );
	}

	public static void iterateOver(ThreadContext context, Block block, Iterable<?> it) {
		for (Iterator i$ = it.iterator(); i$.hasNext();) {
			Object o = i$.next();
			block.yield(context, (IRubyObject) o);
			if (block.isEscaped())
				break;
		}
	}

	public static void write( Writer writer, Node node, boolean indent, String encoding) {
		try {
				StreamResult result = new StreamResult(writer);
				DOMSource source = new DOMSource(node);
				Transformer transformer = getTransformer();
				
				transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
				
				if( encoding != null ) {
					transformer.setOutputProperty( OutputKeys.ENCODING, encoding );
				}
				
				transformer.transform( source, result);				
				
		} catch( RuntimeException e) {
			throw e;
		} catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}


	public static String toString(Node node, boolean indent) {
		return toString(node, indent, (String) null );
	}
	
	public static String toString(Node node, boolean indent, EncodingJ encoding) {
		return toString(node, indent, encoding == null ? null : encoding.asJavaString() );
	}
	
	public static String toString(Node node, boolean indent, String encoding) {
		StringWriter writer = new StringWriter();
		try {
			try { 
				StreamResult result = new StreamResult(writer);
				DOMSource source = new DOMSource(node);
				Transformer transformer = getTransformer();
				
				transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
				
				//OMIT_XML_DECLARATION
				if( encoding != null )
					transformer.setOutputProperty( OutputKeys.ENCODING, encoding );
				
				transformer.transform(source, result);
				
				String str = writer.toString().replace(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
				return str;
			} finally {
				writer.close();
			}
		} catch( RuntimeException e) {
			throw e;
		} catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public static IRubyObject[] toStringArray(ThreadContext context,
			Object array, int offset) {
		List list;
		if (array == null) {
			list = new ArrayList(0);
		} else {
			if (array instanceof List) {
				list = (List) array;
			} else {
				if (array.getClass().isArray())
					list = Arrays.asList((Object[]) (Object[]) array);
				else
					throw context.getRuntime().newArgumentError(
							array.getClass().getName() + " unsuported");
			}
		}
		List<IRubyObject> result = new ArrayList<IRubyObject>();
		for (int i = offset; i < list.size(); i++) {
			if ((list.get(i) instanceof List<?>))
				result.addAll( (List<IRubyObject>)list.get(i));
			else if ((list.get(i) instanceof String))
				result.add(context.getRuntime().newString((String) list.get(i)));
			else {
				result.add((IRubyObject) list.get(i));
			}
		}
		ListIterator<IRubyObject> it = result.listIterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if ((obj instanceof RubyString))
				continue;
			it.set(context.getRuntime().newString(obj.toString()));
		}
		return (IRubyObject[]) result.toArray(new IRubyObject[result.size()]);
	}

	public static String toJavaString(Object object) {
		if ((object instanceof RubyNil)) {
			return null;
		}
		if ((object instanceof String)) {
			return (String) object;
		}
		if ((object instanceof RubyString)) {
			return ((RubyString) object).asJavaString();
		}
		if ((object instanceof RubySymbol)) {
			return ((RubySymbol) object).asJavaString();
		}
		throw new IllegalArgumentException(object.getClass().getName()
				+ " can not be a String");
	}
	
	public static Boolean toJavaBoolean( ThreadContext context, Object object ) {
		return toJavaBoolean(context, object, null );
	}
	
	public static Boolean toJavaBoolean( ThreadContext context, Object object, Boolean defaultValue ) {

		if ( object == null || object instanceof RubyNil) 
			return defaultValue;
		
		if (object instanceof Boolean) 
			return (Boolean) object;
		
		if (object instanceof RubyBoolean ) {
			return ((RubyBoolean) object).isTrue();
		}
		
		throw context.getRuntime().newArgumentError("");
	}


	public static RubyBoolean toBool(ThreadContext context, boolean value) {
		return value ? context.getRuntime().getTrue() : context.getRuntime()
				.getFalse();
	}

	static {
		factory.setNamespaceAware(true);

		transformerFactory = TransformerFactory.newInstance();

		xpathFactory = XPathFactory.newInstance();

		builderLocal = new ThreadLocal<DocumentBuilder>() {
			protected DocumentBuilder initialValue() {
				try {
					return UtilJ.getDocumentBuilderFactory().newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					throw new RuntimeException(e);
				}
			}

			public DocumentBuilder get() {
				DocumentBuilder builder = (DocumentBuilder) super.get();
				builder.reset();
				return builder;
			}
		};
		transformerLocal = new ThreadLocal<Transformer>() {
			protected Transformer initialValue() {
				try {
					return UtilJ.getTransformerFactory().newTransformer();
				} catch (TransformerConfigurationException e) {
					throw new RuntimeException(e);
				}
			}

			public Transformer get() {
				Transformer builder = (Transformer) super.get();
				builder.reset();
				return builder;
			}
		};
	}

	public static Node find( NamedNodeMap map, String name ) {
		
		Node node = map.getNamedItem( name );

		if( node != null )
			return node;
		
		for( int i=0;i<map.getLength(); i++ ) {
			Node item = map.item(i);
			
			if( item.getLocalName().equals( name ) ) {
				return item;
			}
		}
		
		return null;
	}
	
	public static String getRubyClassName(IRubyObject rObject) {
		if (rObject.isNil()) {
			return "nil";
		}
		return rObject.getMetaClass().asString().asJavaString();
	}
	
	public static boolean isNamespace(Node node) {
		String name = node.getNodeName();
		if (name.startsWith("xmlns:") || name.equals("xmlns")) {
			return true;
		}
		return false;
	}
	
	public static boolean isAttr(Node node) {
		return ! isNamespace(node);
	}
}