package smile.xml;

import javax.xml.xpath.XPathExpressionException;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyNumeric;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import smile.xml.util.UtilJ;

public class NodeSetJ extends RubyObject
{
  private static final long serialVersionUID = 8212708453693594157L;
  public static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
  {
    public IRubyObject allocate(Ruby runtime, RubyClass klass)
    {
      return new NodeSetJ(runtime, klass);
    } } ;
  private RubyArray array;

  public static RubyClass define(Ruby runtime) { RubyClass klass = UtilJ.getClass(runtime, new String[] { "LibXML", "XML", "Node" });
    RubyClass result = klass.defineClassUnder("Set", runtime.getObject(), ALLOCATOR);
    result.includeModule(runtime.getModule("Enumerable"));
    result.defineAnnotatedMethods(NodeSetJ.class);
    return result; }

  private static RubyClass getRubyClass(Ruby runtime)
  {
    return UtilJ.getClass(runtime, new String[] { "LibXML", "XML", "Node", "Set" });
  }

  public static NodeSetJ newInstance(ThreadContext context, RubyArray array) {
    return (NodeSetJ)getRubyClass(context.getRuntime()).newInstance(context, new IRubyObject[] { array }, null);
  }

  private NodeSetJ(Ruby runtime, RubyClass metaClass)
  {
    super(runtime, metaClass);
  }
  @JRubyMethod(name={"initialize"})
  public void initialize(ThreadContext context, IRubyObject pArray) {
    this.array = ((RubyArray)pArray);
  }
  @JRubyMethod(name={"[]"})
  public IRubyObject get(ThreadContext context, IRubyObject pIndex) throws XPathExpressionException {
    RubyNumeric index = (RubyNumeric)pIndex;
    return (IRubyObject)this.array.get((int)index.getLongValue());
  }

  @JRubyMethod(name={"each"})
  public void each(ThreadContext context, Block block) {
    for (int i = 0; i < this.array.getLength(); i++) {
      IRubyObject obj = (IRubyObject)this.array.get(i);
      block.yield(context, obj);
      if (block.isEscaped()) break;
    }
  }

  @JRubyMethod(name={"empty?"})
  public RubyBoolean isEmpty(ThreadContext context) throws XPathExpressionException {
    return UtilJ.toBool(context, this.array.isEmpty());
  }
  @JRubyMethod(name={"first"})
  public IRubyObject getFirst(ThreadContext context) throws XPathExpressionException {
    if (this.array.isEmpty())
      return context.getRuntime().getNil();
    return (NodeJ)this.array.get(0);
  }
  @JRubyMethod(name={"length"}, alias={"size"})
  public RubyFixnum getLength(ThreadContext context) throws XPathExpressionException {
    return context.getRuntime().newFixnum(this.array.getLength());
  }
}