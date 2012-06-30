package smile.xml;

import java.util.concurrent.atomic.AtomicReference;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import smile.xml.util.UtilJ;

public class ErrorJ extends RubyObject
{
  private static final long serialVersionUID = -8185698427872540911L;
  private static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
  {
    public IRubyObject allocate(Ruby runtime, RubyClass klass)
    {
      return new ErrorJ(runtime, klass);
    }
  };

  private static final AtomicReference<Block> handler = new AtomicReference();

  public static RubyClass define(Ruby runtime)
  {
    RubyModule module = UtilJ.getModule(runtime, new String[] { "LibXML", "XML" });
    RubyClass result = module.defineClassUnder("Error", runtime.getObject(), ALLOCATOR);
    result.defineAnnotatedMethods(ErrorJ.class);
    return result;
  }

  private static RubyClass getRubyClass(Ruby runtime) {
    return UtilJ.getClass(runtime, new String[] { "LibXML", "XML", "Error" });
  }

  public static ErrorJ newInstance(ThreadContext context, IRubyObject node, IRubyObject prefix, IRubyObject uri)
  {
    IRubyObject[] args = { node, uri, prefix };
    return (ErrorJ)getRubyClass(context.getRuntime()).newInstance(context, args, null);
  }

  private ErrorJ(Ruby runtime, RubyClass metaClass) {
    super(runtime, metaClass);
  }

  @JRubyMethod(name={"set_handler"})
  public static void setHandler(ThreadContext context, Block pHandler)
  {
    handler.set(pHandler);
  }
  @JRubyMethod(name={"reset_handler"})
  public static void resetHandler(ThreadContext context) {
    handler.set(null);
  }
  @JRubyMethod(name={"handler"})
  public static Block getErrorHandler(ThreadContext context) {
    return (Block)handler.get();
  }
}