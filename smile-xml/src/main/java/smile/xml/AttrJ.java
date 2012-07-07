package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import smile.xml.util.UtilJ;

@JRubyClass( name="LibXML::XML::Attr" )
public class AttrJ extends NodeJ {

	private static final long serialVersionUID = -8068663140926376117L;

	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new AttrJ(runtime, klass);
		}
	};

	public static RubyClass define(Ruby runtime) {
		return UtilJ.defineClass( runtime, AttrJ.class, ALLOCATOR );
	}

	private static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ.getClass(runtime, AttrJ.class );
	}

	public static AttrJ newInstance(ThreadContext context) {
		return (AttrJ) getRubyClass(context.getRuntime()).newInstance(context,
				new IRubyObject[0], null);
	}

	private NodeJ parent;
	
	private AttrJ(Ruby ruby, RubyClass clazz) {
		super(ruby, clazz);
	}

	public void setParent( NodeJ node ) {
		this.parent = node;
	}
	
	@JRubyMethod(name = "initialize", optional = 4)
	public void initialize(ThreadContext context, IRubyObject[] args) {
		NodeJ node = (NodeJ) (NodeJ) (args.length > 0 ? args[0] : null);
		RubyString name = (RubyString) (args.length > 1 ? args[1] : null);
		RubyString value = (RubyString) (args.length > 2 ? args[2] : null);
		NamespaceJ ns = (NamespaceJ) (args.length > 3 ? args[3] : null);
		
		this.parent = node;
		
		if (node != null) {
			Document doc = ((Node) node.getJavaObject()).getOwnerDocument();
			Attr attr = doc.createAttribute(name.asJavaString());
			attr.setNodeValue(value.asJavaString());
			setJavaObject(attr);
			node.getJavaObject().getAttributes().setNamedItem( attr );
		}
	}

	@JRubyMethod(name = "next")
	public IRubyObject getNext(ThreadContext context) {
		
		Node n = null;
		
		if( parent != null ) {
			NamedNodeMap aa = parent.getJavaObject().getAttributes();
		    for( int i=0; i<aa.getLength(); i++ ) {
		
		    	if( aa.item(i).equals( getJavaObject() ) ) {
		    		if( i+1 < aa.getLength() )
		    			n = aa.item(i+1);
		    		break;
		    	}
		    }
		}
		
		if( n == null )
			return context.getRuntime().getNil();
		
	    AttrJ node = newInstance(context);
	    node.setDocPresent( isDocPresent() );
	    node.setJavaObject( n );
	    node.setParent( parent );
	    return node;
 
	}

	@JRubyMethod(name = "name")
	public RubyString getName(ThreadContext context) {
		if( getJavaObject().getLocalName() != null )
			return context.getRuntime().newString( getJavaObject().getLocalName() );
		return context.getRuntime().newString( getJavaObject().getNodeName() );
	}

	@JRubyMethod(name = "ns")
	public IRubyObject getNs(ThreadContext context) {
		if( getJavaObject().getNamespaceURI() != null ) {
			RubyString prefix = null;
			if( getJavaObject().getPrefix() != null )
				prefix = context.getRuntime().newString( getJavaObject().getPrefix() );
			RubyString uri = null;
			if( getJavaObject().getNamespaceURI() != null)
				uri = context.getRuntime().newString( getJavaObject().getNamespaceURI() );
			return NamespaceJ.newInstance(context, this, prefix, uri );
		}
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "parent")
	public IRubyObject getParent(ThreadContext context) {
		return parent;
	}

	@JRubyMethod(name = "parent?")
	public IRubyObject hasParent(ThreadContext context) {
		return toRubyBoolean( context, parent != null );
	}

	@JRubyMethod(name = "remove!")
	public IRubyObject remove(ThreadContext context) {

		
		NamedNodeMap aa = parent.getJavaObject().getAttributes();
		
		if( aa != null ) {
			aa.removeNamedItem( getJavaObject().getNodeName() );
		}
		
		parent = null;
		
		return this;
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