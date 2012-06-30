package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.Node;
import smile.xml.util.UtilJ;

public class NamespacesJ extends RubyObject
{
  private static final long serialVersionUID = 4673137342270845475L;
  private static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
  {
    public IRubyObject allocate(Ruby runtime, RubyClass klass)
    {
      return new NamespacesJ(runtime, klass);
    } } ;
  private NodeJ node;

  public static RubyClass define(Ruby runtime) { RubyModule module = UtilJ.getModule(runtime, new String[] { "LibXML", "XML" });
    RubyClass result = module.defineClassUnder("Namespaces", runtime.getObject(), ALLOCATOR);
    result.defineAnnotatedMethods(NamespacesJ.class);
    return result; }

  private static RubyClass getRubyClass(Ruby runtime)
  {
    return UtilJ.getClass(runtime, new String[] { "LibXML", "XML", "Namespaces" });
  }

  public static NamespacesJ newInstance(ThreadContext context, NodeJ node)
  {
    IRubyObject[] args = { node };
    return (NamespacesJ)getRubyClass(context.getRuntime()).newInstance(context, args, null);
  }

  public NamespacesJ(Ruby runtime, RubyClass metaClass)
  {
    super(runtime, metaClass);
  }
  @JRubyMethod(name={"initialize"})
  public void initialize(ThreadContext context, IRubyObject pNode) {
    this.node = ((NodeJ)pNode);
  }

  @JRubyMethod(name={"namespace"})
  public IRubyObject getNamespace(ThreadContext context) {
    Ruby run = context.getRuntime();

    String tmp = ((Node)this.node.getJavaObject()).getPrefix();
    IRubyObject prefix = tmp == null ? run.getNil() : run.newString(tmp);

    tmp = ((Node)this.node.getJavaObject()).getNamespaceURI();
    IRubyObject uri = tmp == null ? run.getNil() : run.newString(tmp);

    return NamespaceJ.newInstance(context, this.node, prefix, uri);
  }
}