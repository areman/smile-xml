package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyString;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import smile.xml.util.UtilJ;

public class AttrJ extends NodeJ {

	private static final long serialVersionUID = -8068663140926376117L;

	public static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new AttrJ(runtime, klass);
		}
	};

	public static RubyClass define(Ruby runtime) {
		RubyModule module = UtilJ.getModule(runtime, new String[] { "LibXML",
				"XML" });
		RubyClass result = module.defineClassUnder("Attr", runtime.getObject(),
				ALLOCATOR);
		result.defineAnnotatedMethods(AttrJ.class);
		return result;
	}

	public static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ
				.getClass(runtime, new String[] { "LibXML", "XML", "Attr" });
	}

	public static AttrJ newInstance(ThreadContext context) {
		return (AttrJ) getRubyClass(context.getRuntime()).newInstance(context,
				new IRubyObject[0], null);
	}

	private AttrJ(Ruby ruby, RubyClass clazz) {
		super(ruby, clazz);
	}

	@JRubyMethod(name = "initialize", optional = 3)
	public void initialize(ThreadContext context, IRubyObject[] args) {
		NodeJ node = (NodeJ) (NodeJ) (args.length > 0 ? args[0] : null);
		RubyString name = (RubyString) (args.length > 1 ? args[1] : null);
		RubyString value = (RubyString) (args.length > 2 ? args[2] : null);

		if (node != null) {
			Document doc = ((Node) node.getJavaObject()).getOwnerDocument();
			Attr attr = doc.createAttribute(name.asJavaString());
			attr.setNodeValue(value.asJavaString());
			setJavaObject(attr);
		}
	}

	@JRubyMethod(name = "name")
	public RubyString getName(ThreadContext context) {
		return super.getName(context);
	}

	@JRubyMethod(name = "parent")
	public IRubyObject getParent(ThreadContext context) {
		return super.getParent(context);
	}

	@JRubyMethod(name = "value")
	public RubyString getValue(ThreadContext context) {
		return context.getRuntime().newString(
				((Node) getJavaObject()).getNodeValue());
	}

	@JRubyMethod(name = "value=")
	public void setValue(ThreadContext context, IRubyObject obj) {
		((Node) getJavaObject()).setNodeValue(obj.asJavaString());
	}

	@JRubyMethod(name = "node_type_name")
	public RubyString getTypeName(ThreadContext context) {
		return super.getTypeName(context);
	}
}