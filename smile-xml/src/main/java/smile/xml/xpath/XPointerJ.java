package smile.xml.xpath;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;

import smile.xml.util.UtilJ;

@JRubyClass(name = "LibXML::XML::XPath::XPointer")
public class XPointerJ extends RubyObject {

	private static final long serialVersionUID = -7924934418493439093L;

	private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
		public IRubyObject allocate(Ruby runtime, RubyClass klass) {
			return new XPointerJ(runtime, klass);
		}
	};
	
	public static RubyClass define(Ruby runtime) {
		return UtilJ.defineClass( runtime, XPointerJ.class, ALLOCATOR );
	}

	private XPointerJ(Ruby runtime, RubyClass metaClass) {
		super(runtime, metaClass);
	}
	
	
}