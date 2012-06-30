package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;
import smile.xml.util.UtilJ;

public class ReaderJ extends RubyObject
{
  private static final long serialVersionUID = -120431027715516425L;
  private static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
  {
    public IRubyObject allocate(Ruby runtime, RubyClass klass)
    {
      return new ReaderJ(runtime, klass);
    }
  };

  private ReaderJ(Ruby runtime, RubyClass metaClass)
  {
    super(runtime, metaClass);
  }

  public static RubyClass define(Ruby runtime)
  {
    RubyModule module = UtilJ.getModule(runtime, new String[] { "LibXML", "XML" });
    RubyClass result = module.defineClassUnder("Reader", runtime.getObject(), ALLOCATOR);
    result.defineAnnotatedMethods(ReaderJ.class);
    return result;
  }
}