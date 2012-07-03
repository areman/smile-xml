package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyConstant;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import smile.xml.util.UtilJ;

public class EncodingJ extends RubyObject {
	private static final long serialVersionUID = -3033814105839661460L;
	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new EncodingJ(runtime, klass);
		}
	};

	public static RubyClass define(Ruby runtime) {
		RubyModule module = UtilJ.getModule(runtime, "LibXML", "XML");
		RubyClass result = module.defineClassUnder("Encoding",
				runtime.getObject(), ALLOCATOR);
		result.defineAnnotatedMethods(EncodingJ.class);
		result.defineAnnotatedConstants(EncodingJ.class);
		return result;
	}

	@JRubyConstant
	public static final String NONE = null;

	@JRubyConstant
	public static final String UTF_8 = "UTF-8";
	
	@JRubyConstant
	public static final String ISO_8859_1 = "ISO-8859-1";

	public static EncodingJ newInstance(ThreadContext context, String string) {

		IRubyObject[] args = { context.getRuntime().newString(string) };
		EncodingJ encoding = (EncodingJ) getRubyClass( context.getRuntime() ).newInstance(context, args, null );
		return encoding;
	}

	private RubyString encoding;

	@JRubyMethod(name = { "initialize" }, rest = true)
	public void initialize(ThreadContext context, IRubyObject[] args) {
		if (args.length > 0 && args[0] instanceof RubyString) {
			this.encoding = (RubyString) args[0];
		}
	}

	public static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ.getClass(runtime, "LibXML", "XML", "Encoding");
	}

	private EncodingJ(Ruby runtime, RubyClass metaClass) {
		super(runtime, metaClass);
	}

}