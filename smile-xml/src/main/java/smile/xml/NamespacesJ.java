package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import smile.xml.util.UtilJ;

@JRubyClass( name = "LibXML::XML::Namespaces", include = "Enumerable" )
public class NamespacesJ extends RubyObject {
	
	private static final long serialVersionUID = 4673137342270845475L;
	
	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new NamespacesJ(runtime, klass);
		}
	};

	public static RubyClass define(Ruby runtime) {
		return UtilJ.defineClass(runtime, NamespacesJ.class, ALLOCATOR);
	}

	private static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ.getClass(runtime, NamespacesJ.class);
	}

	public static NamespacesJ newInstance(ThreadContext context, NodeJ node) {
		IRubyObject[] args = { node };
		return (NamespacesJ) getRubyClass(context.getRuntime()).newInstance(context, args, null);
	}
	
	private NodeJ node;

	public NamespacesJ(Ruby runtime, RubyClass metaClass) {
		super(runtime, metaClass);
	}

	@JRubyMethod(name = { "initialize" })
	public void initialize(ThreadContext context, IRubyObject pNode) {
		this.node = ((NodeJ) pNode);
	}
	
	@JRubyMethod(name = { "default" })
	public IRubyObject getDefault(ThreadContext context) {
		// TODO
		throw context.getRuntime().newRuntimeError("not yet implemented");
	}
	
	@JRubyMethod(name = { "default_prefix=" })
	public void setDefaultPrefix(ThreadContext context, IRubyObject pPrefix) {
		// TODO
		throw context.getRuntime().newRuntimeError("not yet implemented");
	}
	
	@JRubyMethod(name = { "definitions" })
	public IRubyObject getDefinitions(ThreadContext context) {
		// TODO
		throw context.getRuntime().newRuntimeError("not yet implemented");
	}
	
	@JRubyMethod(name = { "each" })
	public IRubyObject each(ThreadContext context, Block block) {
		// TODO
		throw context.getRuntime().newRuntimeError("not yet implemented");
	}
	
	@JRubyMethod(name = { "find_by_href" })
	public IRubyObject findByHref(ThreadContext context, IRubyObject pHref) {
		// TODO
		throw context.getRuntime().newRuntimeError("not yet implemented");
	}
	
	@JRubyMethod(name = { "find_by_prefix" }, optional = 1)
	public IRubyObject findByPrefix(ThreadContext context, IRubyObject pPrefix) {
		// TODO
		throw context.getRuntime().newRuntimeError("not yet implemented");
	}

	@JRubyMethod(name = { "namespace" })
	public IRubyObject getNamespace(ThreadContext context) {
		Ruby run = context.getRuntime();

		String prefixStr = ((Node) node.getJavaObject()).getPrefix();
		IRubyObject prefix = prefixStr == null ? run.getNil() : run.newString(prefixStr);

		String uriStr = ((Node) node.getJavaObject()).getNamespaceURI();
		IRubyObject uri = uriStr == null ? run.getNil() : run.newString(uriStr);
		
		if (uri.isNil()) {
			return run.getNil();
		} else {
			return NamespaceJ.newInstance(context, node, prefix, uri);
		}
	}
	
	@JRubyMethod(name = { "namespace=" })
	public void setNamespace(ThreadContext context, IRubyObject pNamespace) {
		if ( ! (pNamespace instanceof NamespaceJ)) {
			throw context.getRuntime().newTypeError("wrong argument type " + UtilJ.getRubyClassName(pNamespace) + " (expected Data)");
		}
		
		NamespaceJ namespace = (NamespaceJ) pNamespace;
		Document doc = node.getJavaObject().getOwnerDocument();
		
		String namespaceURI = "";
		String qualifiedName = node.getJavaObject().getNodeName();
		if (! namespace.getHref(context).isNil()) {
			namespaceURI = namespace.getHref(context).asJavaString();
		}
		if (! namespace.getPrefix(context).isNil()) {
			qualifiedName = namespace.getPrefix(context).asJavaString() + ":" + node.getJavaObject().getNodeName();
		}
		node.setJavaObject(doc.renameNode(node.getJavaObject(), namespaceURI, qualifiedName));
	}
	
	@JRubyMethod(name = { "node" })
	public IRubyObject getNode(ThreadContext context) {
		return this.node;
	}
}