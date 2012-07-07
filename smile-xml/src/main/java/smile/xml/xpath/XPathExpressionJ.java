package smile.xml.xpath;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import smile.xml.util.UtilJ;

@JRubyClass( name="LibXML::XML::XPath::Expression" )
public class XPathExpressionJ extends RubyObject {
	
	private static final long serialVersionUID = 9176572911090989553L;
	
	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new XPathExpressionJ(runtime, klass);
		}
	};

	public static RubyClass define(Ruby runtime) {
		return UtilJ.defineClass(runtime, XPathExpressionJ.class, ALLOCATOR);
	}

	private static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ.getClass(runtime, XPathExpressionJ.class);
	}

	@JRubyMethod( name="compile", module=true )
	public static XPathExpressionJ compile(ThreadContext context, IRubyObject self, IRubyObject pString ) {
		RubyString string = (RubyString) pString;
		return newInstance(context, string );		
	}
	
	public static XPathExpressionJ newInstance(ThreadContext context, RubyString string ) {
		IRubyObject[] args = { string };
		return (XPathExpressionJ) getRubyClass(context.getRuntime()).newInstance(
				context, args, null);
	}

	private XPathExpressionJ(Ruby runtime, RubyClass metaClass) {
		super(runtime, metaClass);
	}

	private RubyString string;
	
	@JRubyMethod(name = "initialize" )
	public void initialize(ThreadContext context, RubyString pString ) {
		string = pString;
	}
	
	public RubyString getExpression() {
		return string;
	}

}
