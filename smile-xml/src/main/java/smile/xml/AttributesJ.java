package smile.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyHash;
import org.jruby.RubyModule;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import smile.xml.util.UtilJ;

@JRubyClass(name = { "Attributes" })
public class AttributesJ extends BaseJ<Node> {
	
	private static final long serialVersionUID = -2519312417704775509L;
	
	public static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new AttributesJ(runtime, klass);
		}
	};

	public static RubyClass define(Ruby runtime) {
		RubyModule module = UtilJ.getModule(runtime, new String[] { "LibXML",
				"XML" });
		RubyClass result = module.defineClassUnder("Attributes",
				runtime.getObject(), ALLOCATOR);
		result.includeModule(runtime.fastGetModule("Enumerable"));
		result.defineAnnotatedMethods(AttributesJ.class);
		return result;
	}

	@JRubyMethod(name = { "initialize" })
	public IRubyObject initialize(ThreadContext context) {
		return this;
	}

	private static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ.getClass(runtime, new String[] { "LibXML", "XML",
				"Attributes" });
	}

	public static AttributesJ newInstance(ThreadContext context) {
		return (AttributesJ) getRubyClass(context.getRuntime()).newInstance(
				context, new IRubyObject[0], null);
	}

	public AttributesJ(Ruby ruby, RubyClass clazz) {
		super(ruby, clazz);
	}

	@JRubyMethod(name = { "[]" })
	public IRubyObject getValue(ThreadContext context, IRubyObject pName) {
		if ((pName instanceof RubySymbol)) {
			pName = pName.asString();
		}

		RubyString name = (RubyString) pName;

		if (((Node) getJavaObject()).getAttributes() == null) {
			return context.getRuntime().getNil();
		}
		Node node = ((Node) getJavaObject()).getAttributes().getNamedItem(
				name.asJavaString());

		if (node == null) {
			return context.getRuntime().getNil();
		}
		return context.getRuntime().newString(node.getTextContent());
	}

	@JRubyMethod(name = { "[]=" })
	public void setValue(ThreadContext context, IRubyObject pName,
			IRubyObject pValue) {
		if ((pName instanceof RubySymbol)) {
			pName = pName.asString();
		}

		RubyString name = (RubyString) pName;

		RubyString value = (RubyString) pValue;

		Node node = ((Node) getJavaObject()).getAttributes().getNamedItem(
				name.asJavaString());
		if (node == null) {
			node = ((Node) getJavaObject()).getOwnerDocument().createAttribute(
					name.asJavaString());
			((Node) getJavaObject()).getAttributes().setNamedItem(node);
		}

		node.setNodeValue(value.asJavaString());
	}

	@JRubyMethod(name = { "each" })
	public void iterateOver(ThreadContext context, Block block) {
		UtilJ.iterateOver(context, block, attributesAsList(context));
	}

	@JRubyMethod(name = { "first" })
	public IRubyObject first(ThreadContext context) {
		if (((Node) getJavaObject()).getAttributes().getLength() == 0) {
			return context.getRuntime().getNil();
		}
		AttrJ attr = AttrJ.newInstance(context);
		attr.setJavaObject(((Node) getJavaObject()).getAttributes().item(0));
		return attr;
	}

	@JRubyMethod(name = { "get_attribute" })
	public IRubyObject getAttribute(ThreadContext context, RubyString name) {
		Node node = ((Node) getJavaObject()).getAttributes().getNamedItem(
				name.asJavaString());
		if (node != null) {
			AttrJ attr = AttrJ.newInstance(context);

			attr.setJavaObject(node);
			return attr;
		}

		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = { "get_attribute_ns" })
	public IRubyObject getAttributeNs(ThreadContext context,
			RubyString namespace, RubyString name) {
		Node node = ((Node) getJavaObject()).getAttributes().getNamedItemNS(
				namespace.asJavaString(), name.asJavaString());
		if (node != null) {
			AttrJ attr = AttrJ.newInstance(context);
			attr.setJavaObject(node);
			return attr;
		}

		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = { "length" })
	public RubyFixnum getLength(ThreadContext context) {
		int r = ((Node) getJavaObject()).getAttributes() == null ? 0
				: ((Node) getJavaObject()).getAttributes().getLength();
		return context.getRuntime().newFixnum(r);
	}

	@JRubyMethod(name = { "node" })
	public NodeJ getNode(ThreadContext context) {
		NodeJ node = NodeJ.newInstance(context);
		node.setJavaObject(getJavaObject());
		return node;
	}

	@JRubyMethod(name = { "to_h" })
	public RubyHash toHash(ThreadContext context) {
		Map map = new HashMap();

		for (int i = 0; i < ((Node) getJavaObject()).getAttributes()
				.getLength(); i++) {
			Node a = ((Node) getJavaObject()).getAttributes().item(i);
			map.put(context.getRuntime().newString(a.getNodeName()), context
					.getRuntime().newString(a.getNodeValue()));
		}
		return RubyHash.newHash(context.getRuntime(), map, context.getRuntime()
				.getNil());
	}

	private List<AttrJ> attributesAsList(ThreadContext context) {
		NamedNodeMap list = ((Node) getJavaObject()).getAttributes();
		List array = new ArrayList(list.getLength());
		for (int i = 0; i < list.getLength(); i++) {
			AttrJ node = AttrJ.newInstance(context);
			node.setJavaObject(list.item(i));
			array.add(node);
		}
		return array;
	}
}