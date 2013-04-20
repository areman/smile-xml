package smile.xml;

import java.util.concurrent.atomic.AtomicReference;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyException;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyConstant;
import org.jruby.anno.JRubyMethod;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import smile.xml.util.UtilJ;

@JRubyClass( name="LibXML::XML::Error", parent="StandardError" )
public class ErrorJ extends RubyException {
	
	private static final long serialVersionUID = -8185698427872540911L;

	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new ErrorJ(runtime, klass);
		}
	};

	private static final AtomicReference<Block> handler = new AtomicReference<Block>();

	public static RubyClass define(Ruby runtime) {
		return UtilJ.defineClass(runtime, ErrorJ.class, ALLOCATOR);
	}

	private static RubyClass getRubyClass(Ruby runtime) {
		return UtilJ.getClass(runtime, ErrorJ.class );
	}

	public static RaiseException newRaiseException(ThreadContext context,
			String message) {
		Ruby run = context.getRuntime();
		return new RaiseException(run, getRubyClass(context.getRuntime()), message, true);
	}

	public static RubyException newInstance(ThreadContext context,
			String message) {
		return RubyException.newException(context.getRuntime(),getRubyClass(context.getRuntime()), message);
	}
	
	@JRubyConstant
	public static final IRubyObject VERBOSE_HANDLER = null;
	
	@JRubyConstant
	public static final IRubyObject QUIET_HANDLER = null;
	
	@JRubyConstant
	public static final IRubyObject PARSER = null;
	
	@JRubyConstant
	public static final IRubyObject DOCUMENT_END = null;
	
	@JRubyConstant
	public static final IRubyObject FATAL = null;
	
	@JRubyConstant
	public static final IRubyObject TAG_NAME_MISMATCH = null;

	@JRubyConstant
	public static final IRubyObject SCHEMASV = null;

	@JRubyConstant
	public static final IRubyObject SCHEMAV_ELEMENT_CONTENT = null;

	@JRubyConstant
	public static final IRubyObject ERROR = null;
	
	private ErrorJ(Ruby runtime, RubyClass metaClass) {
		super(runtime, metaClass);
	}

	@JRubyMethod(name = "initialize")
	public void initialize(ThreadContext context, IRubyObject pString) {
		message = pString;
	}

	@JRubyMethod(name = "domain")
	public IRubyObject getDomain(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "level")
	public IRubyObject getLevel(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "code")
	public IRubyObject getCode(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "file")
	public IRubyObject getFile(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "line")
	public IRubyObject getLine(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "str1")
	public IRubyObject getStringOne(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "str2")
	public IRubyObject getStringTwo(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "str3")
	public IRubyObject getStringThree(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "int1")
	public IRubyObject getIntOne(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "int2")
	public IRubyObject getIntTwo(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "node")
	public IRubyObject getNode(ThreadContext context) {
		return context.getRuntime().getNil();
	}

	@JRubyMethod(name = "set_handler", module=true)
	public static void setHandler(ThreadContext context, IRubyObject self, Block pHandler) {
		handler.set(pHandler);
	}

	@JRubyMethod(name = "reset_handler", module=true)
	public static void resetHandler(ThreadContext context, IRubyObject self) {

		handler.set(null);
	}

	@JRubyMethod(name = "handler")
	public static Block getErrorHandler(ThreadContext context, IRubyObject self) {
		return (Block) handler.get();
	}
}