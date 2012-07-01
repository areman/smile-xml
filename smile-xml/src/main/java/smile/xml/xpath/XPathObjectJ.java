package smile.xml.xpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyModule;
import org.jruby.RubyNumeric;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import smile.xml.BaseJ;
import smile.xml.NodeJ;
import smile.xml.NodeSetJ;
import smile.xml.util.UtilJ;

public class XPathObjectJ extends RubyObject {
	private static final long serialVersionUID = 4673137342270845475L;
	
	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new XPathObjectJ(runtime, klass);
		}
	};
	

	public static RubyClass define(Ruby runtime) {
		RubyModule module = UtilJ.getModule(runtime,"LibXML", "XML", "XPath"  );
		RubyClass result = module.defineClassUnder("Object",
				runtime.getObject(), ALLOCATOR);
		result.includeModule(runtime.getModule("Enumerable"));
		result.defineAnnotatedMethods(XPathObjectJ.class);
		return result;
	}

	private static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ.getClass(runtime, "LibXML", "XML", "XPath", "Object");
	}

	public static XPathObjectJ newInstance(ThreadContext context,
			IRubyObject expression, IRubyObject document,
			IRubyObject[] namespaces) {
		List args = new ArrayList();
		args.add(expression);
		args.add(document);
		args.addAll(Arrays.asList(namespaces));
		return (XPathObjectJ) getRubyClass(context.getRuntime()).newInstance(
				context,
				(IRubyObject[]) args.toArray(new IRubyObject[args.size()]),
				null);
	}

	private String[] namespaces;
	private RubyString expression;
	private Node node;
	private RubyArray result;

	private XPathObjectJ(Ruby runtime, RubyClass metaClass) {
		super(runtime, metaClass);
	}

	@JRubyMethod(name = { "initialize" }, required = 1, rest = true)
	public void initialize(ThreadContext context, IRubyObject[] args) {
		this.expression = ((RubyString) args[0]);
		this.node = ((Node) ((BaseJ) args[1]).getJavaObject());

		this.namespaces = new String[args.length - 2];
		for (int i = 2; i < args.length; i++)
			this.namespaces[(i - 2)] = args[i].asJavaString();
	}

	@JRubyMethod(name = { "each" })
	public void iterateOver(ThreadContext context, Block block)
			throws Exception {
		RubyArray array = getResult(context);
		for (Iterator i$ = array.iterator(); i$.hasNext();) {
			Object obj = i$.next();
			IRubyObject o = (IRubyObject) obj;
			block.yield(context, o);
			if (block.isEscaped())
				break;
		}
	}

	@JRubyMethod(name = { "length" }, alias = { "size" })
	public RubyFixnum getLength(ThreadContext context) throws Exception {
		return context.getRuntime().newFixnum(getResult(context).getLength());
	}

	@JRubyMethod(name ="string")
	public RubyString getString(ThreadContext context) throws Exception {
		return this.expression;
	}

	@JRubyMethod(name = { "first" })
	public IRubyObject getFirst(ThreadContext context) throws Exception {
		if (getResult(context).isEmpty())
			return context.getRuntime().getNil();
		return (NodeJ) getResult(context).get(0);
	}

	@JRubyMethod(name =  "last" )
	public IRubyObject getLast(ThreadContext context) throws Exception {
		if (getResult(context).isEmpty())
			return context.getRuntime().getNil();
		return (NodeJ) getResult(context).last();
	}

	@JRubyMethod(name = { "xpath_type" })
	public IRubyObject getXpathType(ThreadContext context) throws Exception {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = { "empty?" })
	public RubyBoolean isEmpty(ThreadContext context) throws Exception {
		return UtilJ.toBool(context, getResult(context).isEmpty());
	}

	@JRubyMethod(name = { "set" })
	public IRubyObject getSet(ThreadContext context) throws Exception {
		return NodeSetJ.newInstance(context, getResult(context));
	}

	@JRubyMethod(name = { "to_a" })
	public IRubyObject toArray(ThreadContext context) throws Exception {
		return getResult(context);
	}

	private RubyArray getResult(ThreadContext context) throws Exception {
		if (this.result == null)
			this.result = evaluateExpression(context);
		return this.result;
	}

	@JRubyMethod(name = { "[]" })
	public IRubyObject get(ThreadContext context, IRubyObject pIndex)
			throws Exception {
		RubyNumeric index = (RubyNumeric) pIndex;
		RubyArray result = getResult(context);
		return (IRubyObject) result.at( index );
	}

	@JRubyMethod(name = { "context" })
	public IRubyObject getContext(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	private RubyArray evaluateExpression(ThreadContext context) throws Exception {
		XPath xpath = UtilJ.newXPath();

		Node nn = (this.node instanceof Document) ? ((Document) this.node)
				.getDocumentElement() : this.node.getOwnerDocument();

		xpath.setNamespaceContext( new CustomNamespaceContext( nn, this.namespaces) );
		NodeList list;
		try {
			list = (NodeList) xpath.evaluate(this.expression.asJavaString(), this.node, XPathJ.NODESET);
		} catch (XPathExpressionException e) {
			if ((e.getCause() instanceof Exception))
				throw ((Exception) e.getCause());
			throw e;
		}

		List array = new ArrayList();
		for (int i = 0; i < list.getLength(); i++) {
			NodeJ node = NodeJ.newInstance(context);
			node.setJavaObject(list.item(i));
			array.add(node);
		}

		return context.getRuntime().newArray(array);
	}
}