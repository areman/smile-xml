package smile.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyHash;
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

@JRubyClass(name = "LibXML::XML::Attributes", include = "Enumerable")
public class AttributesJ extends BaseJ<Node> {

	private static final long serialVersionUID = -2519312417704775509L;

	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new AttributesJ(runtime, klass);
		}
	};

	public static RubyClass define(Ruby runtime) {
		return UtilJ.defineClass(runtime, AttributesJ.class, ALLOCATOR);
	}

	private NodeJ parent;

	public void setParent(NodeJ node) {
		this.parent = node;
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

	@JRubyMethod(name = "[]")
	public IRubyObject getValue(ThreadContext context, IRubyObject pName) {
		
		String name = toJavaString(context, pName );		

		NamedNodeMap aa = getJavaObject().getAttributes();
		if ( aa == null ) {

			return context.getRuntime().getNil();
		}

		Node node = aa.getNamedItem( name );



		if (node == null) {

			for( int i=0;i<aa.getLength(); i++ ) {
				Node item = aa.item(i);
//				System.out.println( item.get);
				
				if( item.getLocalName().equals( name ) ) {
					node = item;
					break;
				}
			}
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
		AttrJ attr = null;
		NamedNodeMap attributes = getJavaObject().getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node item = attributes.item(i);
			if (UtilJ.isAttr(item)) {
				attr = AttrJ.newInstance(context);
				attr.setJavaObject(item);
				attr.setParent(parent);
				break;
			}
		}
		return UtilJ.nvl(attr, context.getRuntime().getNil());
	}

	@JRubyMethod(name = { "get_attribute" })
	public IRubyObject getAttribute(ThreadContext context, RubyString name) {
		NamedNodeMap aa = getJavaObject().getAttributes();
		String string = name.asJavaString();
		Node node = aa.getNamedItem(string);
		if (node == null)
			for (int i = 0; i < aa.getLength(); i++) {
				Node a = aa.item(i);
				if (a.getLocalName().equals(string)) {
					node = a;
					break;
				}
			}
		if (node != null) {
			AttrJ attr = AttrJ.newInstance(context);
			attr.setParent(parent);
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
			attr.setParent(parent);
			attr.setJavaObject(node);
			return attr;
		}

		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = { "length" })
	public RubyFixnum getLength(ThreadContext context) {
		int length = attributesAsList(context).size();
		return context.getRuntime().newFixnum(length);
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
		NamedNodeMap attributes = ((Node) getJavaObject()).getAttributes();
		List<AttrJ> list = new ArrayList<AttrJ>();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node item = attributes.item(i);
			if (! UtilJ.isAttr(item)) {
				continue;
			}
			AttrJ node = AttrJ.newInstance(context);
			node.setJavaObject(item);
			node.setParent(parent);
			list.add(node);
		}
		return list;
	}
}