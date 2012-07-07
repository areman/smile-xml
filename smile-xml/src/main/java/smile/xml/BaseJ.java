package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyNil;
import org.jruby.RubyObject;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import smile.xml.util.UtilJ;

public class BaseJ<T> extends RubyObject {
	
	private static final long serialVersionUID = 4634403693609027673L;

	private T javaObject;

	public BaseJ(Ruby ruby, RubyClass clazz) {
		super(ruby, clazz);
	}

	public T getJavaObject() {
		return this.javaObject;
	}

	public void setJavaObject(T javaObject) {
		this.javaObject = javaObject;
	}
	
	public static <T> T nvl( T obj, T def ) {
		return obj == null ? def : obj;
	}
	
	public static IRubyObject toRubyBoolean(ThreadContext context, Object obj) {
		
		if( obj == null || obj instanceof RubyNil )
			return context.getRuntime().getNil();
		
		if( obj instanceof RubyBoolean )
			return (IRubyObject) obj;
		
		if( obj instanceof Boolean )
			return ((Boolean)obj) ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
		
			throw context.getRuntime().newArgumentError("");
	}

	public static String toJavaString(ThreadContext context, Object obj) {

		return UtilJ.toJavaString(context, obj);
	}

	public static IRubyObject toRubyString(ThreadContext context, Object obj) {

		return UtilJ.toRubyString( context, obj);
	}


}