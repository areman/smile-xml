package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import smile.xml.util.UtilJ;

@JRubyClass( name="LibXML::XML::Namespace" )
public class NamespaceJ extends RubyObject {
	
	private static final long serialVersionUID = 4128551928821799987L;
	
	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new NamespaceJ(runtime, klass);
		}
	};

	public static RubyClass define(Ruby runtime) {
		return UtilJ.defineClass(runtime, NamespaceJ.class, ALLOCATOR);
	}

	private static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ.getClass(runtime, NamespaceJ.class);
	}
	
	public static NamespaceJ newInstance(ThreadContext context) {
		return new NamespaceJ(context.getRuntime(), getRubyClass(context.getRuntime()));
	}

	public static NamespaceJ newInstance(ThreadContext context,	IRubyObject node, IRubyObject prefix, IRubyObject href) {
		IRubyObject[] args = { node, prefix, href };
		return (NamespaceJ) getRubyClass(context.getRuntime()).newInstance(context, args, null);
	}
	
	private NodeJ node;
	private RubyString prefix;
	private RubyString href;

	public NamespaceJ(Ruby runtime, RubyClass metaClass) {
		super(runtime, metaClass);
	}

	@JRubyMethod(name = { "initialize" })
	public void initialize(ThreadContext context, IRubyObject pNode, IRubyObject pPrefix, IRubyObject pHref) {
		if ( ! (pNode instanceof NodeJ)) {
			throw context.getRuntime().newTypeError("wrong argument type " + UtilJ.getRubyClassName(pNode) + " (expected Data)");
		}
		
		node = ((NodeJ) pNode);
		if (! pPrefix.isNil()) {
			prefix = (RubyString) pPrefix;	
		}
		href = (RubyString) pHref;
		
		// define namespace as "xmlns" attribute
		String name = prefix == null ? "xmlns" : "xmlns:" + prefix.asJavaString();
		String value = href.asJavaString();
		AttrJ.newInstance(context, node, UtilJ.toRubyString(context, name).asString(), 
				UtilJ.toRubyString(context, value).asString(), this);
	}

	@JRubyMethod(name = { "prefix" })
	public IRubyObject getPrefix(ThreadContext context) {
		return UtilJ.nvl(this.prefix, context.getRuntime().getNil());
	}

	@JRubyMethod(name = { "href" })
	public IRubyObject getHref(ThreadContext context) {
		return UtilJ.nvl(this.href, context.getRuntime().getNil());
	}

	@JRubyMethod(name = { "node" })
	public IRubyObject getNode(ThreadContext context) {
		return this.node;
	}
	
	@JRubyMethod(name = { "node_type" })
	public IRubyObject getNodeType(ThreadContext context) {
		return new RubyFixnum(context.getRuntime(), NodeJ.NAMESPACE_DECL);
	}
	
	@JRubyMethod(name = { "next" })
	public IRubyObject getNext(ThreadContext context) {
		// TODO
		return context.getRuntime().getNil();
	}
	
	@JRubyMethod(name = { "==" })
	public IRubyObject isEqual(ThreadContext context, IRubyObject pOther) {
		if (pOther instanceof NamespaceJ) {
			NamespaceJ otherNS = (NamespaceJ) pOther;
			if (href.equals(otherNS.href) && node.equals(otherNS.node)
					&& (prefix == null && otherNS.prefix == null 
					|| prefix.equals(otherNS.prefix))) {
				return context.getRuntime().getTrue();
			}
		}
		return context.getRuntime().getFalse();
	}
	
	@JRubyMethod(name = { "to_s" })
	public IRubyObject toS(ThreadContext context) {
		String str;
		if (prefix != null) {
			str = prefix.asJavaString() + ":" + href.asJavaString();
		} else {
			str = href.asJavaString();
		}		
		return context.getRuntime().newString(str);
	}
	
	public void setNode(NodeJ node) {
		this.node = node;
	}
	
	public void setPrefix(RubyString prefix) {
		this.prefix = prefix;
	}
	
	public void setHref(RubyString href) {
		this.href = href;
	}
}