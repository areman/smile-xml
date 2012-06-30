package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;
import smile.xml.util.UtilJ;

public class EncodingJ extends RubyObject
{
  private static final long serialVersionUID = -3033814105839661460L;
  private static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
  {
    public IRubyObject allocate(Ruby runtime, RubyClass klass)
    {
      return new EncodingJ(runtime, klass);
    }
  };

  public static RubyClass define(Ruby runtime)
  {
    RubyModule module = (RubyModule)runtime.getModule("LibXML").getConstant("XML");
    RubyClass result = module.defineClassUnder("Encoding", runtime.getObject(), ALLOCATOR);
    result.defineAnnotatedMethods(EncodingJ.class);
    return result;
  }

  public static RubyClass getRubyClass(Ruby runtime) {
    return UtilJ.getClass(runtime, new String[] { "LibXML", "XML", "Encoding" });
  }

  private EncodingJ(Ruby runtime, RubyClass metaClass) {
    super(runtime, metaClass);
  }
}