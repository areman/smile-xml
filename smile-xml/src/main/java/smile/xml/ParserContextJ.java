package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import smile.xml.util.UtilJ;

@JRubyClass( name="LibXML::XML::Parser::Context" )
public class ParserContextJ extends RubyObject {


	private static final long serialVersionUID = -1085726141041314973L;
	
	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new ParserContextJ(runtime, klass);
		}
	};

	public static RubyClass define(Ruby runtime) {
		return UtilJ.defineClass(runtime, ParserContextJ.class, ALLOCATOR);
	}

	private static RubyClass getRubyClass( ThreadContext context ) {
		return UtilJ.getClass( context.getRuntime(), ParserContextJ.class );
	}

	private ParserContextJ(Ruby runtime, RubyClass metaClass) {
		super(runtime, metaClass);
	}

	public static IRubyObject newInstance(ThreadContext context) {
		IRubyObject[] args = {};
		return getRubyClass(context).newInstance( context, args, null );
	}



}
