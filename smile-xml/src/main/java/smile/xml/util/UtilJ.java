package smile.xml.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.jruby.Ruby;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyNil;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.Node;

public class UtilJ {
	private static final SchemaFactory schemaFactoryInstance = SchemaFactory
			.newInstance("http://www.w3.org/2001/XMLSchema");

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

	public static RubyModule getModule(Ruby runtime, String[] path) {
		RubyModule m = runtime.fastGetModule(path[0]);

		for (int i = 1; i < path.length; i++) {
			IRubyObject tmp = m.fastGetConstant(path[i]);
			if (tmp == null) {
				tmp = m.defineModuleUnder(path[i]);
			}
			m = (RubyModule) tmp;
		}

		return m;
	}

	public static RubyClass getClass(Ruby runtime, String[] path) {
		if (path.length == 1) {
			return runtime.fastGetClass(path[0]);
		}
		RubyModule m = runtime.fastGetModule(path[0]);

		for (int i = 1; i < path.length - 1; i++) {
			IRubyObject tmp = m.fastGetConstant(path[i]);
			if (tmp == null) {
				tmp = m.defineModuleUnder(path[i]);
			}
			m = (RubyModule) tmp;
		}

		return m.fastGetClass(path[(path.length - 1)]);
	}

	public static void iterateOver(ThreadContext context, Block block,
			Iterable<?> it) {
		for (Iterator i$ = it.iterator(); i$.hasNext();) {
			Object o = i$.next();
			block.yield(context, (IRubyObject) o);
			if (block.isEscaped())
				break;
		}
	}

	public static String toString(Node node) throws Exception {
		StringWriter writer = new StringWriter();
		try {
			StreamResult result = new StreamResult(writer);
			DOMSource source = new DOMSource(node);
			Transformer transformer = getTransformer();
			transformer.transform(source, result);
			String str = writer.toString().replace(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
			return str;
		} finally {
			writer.close();
		}
	}

	public static IRubyObject[] toStringArray(ThreadContext context,
			Object array, int offset) {
		List list;
		if (array == null) {
			list = new ArrayList(0);
		} else {
			if ((array instanceof List)) {
				list = (List) array;
			} else {
				if (array.getClass().isArray())
					list = Arrays.asList((Object[]) (Object[]) array);
				else
					throw context.getRuntime().newArgumentError(
							array.getClass().getName() + " unsuported");
			}
		}
		List result = new ArrayList();
		for (int i = offset; i < list.size(); i++) {
			if ((list.get(i) instanceof List))
				result.addAll((List) list.get(i));
			else if ((list.get(i) instanceof String))
				result.add(context.getRuntime().newString((String) list.get(i)));
			else {
				result.add((IRubyObject) list.get(i));
			}
		}
		ListIterator it = result.listIterator();
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
		transformerLocal = new ThreadLocal() {
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
}