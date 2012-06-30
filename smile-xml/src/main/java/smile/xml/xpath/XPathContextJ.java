package smile.xml.xpath;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import smile.xml.DocumentJ;
import smile.xml.NamespaceJ;
import smile.xml.NamespacesJ;
import smile.xml.NodeJ;
import smile.xml.util.UtilJ;

public class XPathContextJ extends RubyObject
{
  private static final long serialVersionUID = -5768331253256181175L;
  public static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
  {
    public IRubyObject allocate(Ruby runtime, RubyClass klass)
    {
      return new XPathContextJ(runtime, klass); }  } ;

  private final Map<String, String> namespaceMapping = new HashMap<String, String>();
  private DocumentJ document;
  private NodeJ node;

  public static RubyClass define(Ruby runtime) { RubyModule module = UtilJ.getModule(runtime, new String[] { "LibXML", "XML", "XPath" });
    RubyClass result = module.defineClassUnder("Context", runtime.getObject(), ALLOCATOR);
    result.defineAnnotatedMethods(XPathContextJ.class);
    return result; }

  private static RubyClass getRubyClass(Ruby runtime)
  {
    return UtilJ.getClass(runtime, new String[] { "LibXML", "XML", "XPath", "Context" });
  }

  public static XPathContextJ newInstance(ThreadContext context)
  {
    IRubyObject[] args = new IRubyObject[0];
    return (XPathContextJ)getRubyClass(context.getRuntime()).newInstance(context, args, null);
  }

  private XPathContextJ(Ruby runtime, RubyClass metaClass)
  {
    super(runtime, metaClass);
  }
  @JRubyMethod(name={"initialize"}, optional=1)
  public void initialize(ThreadContext context, IRubyObject[] args) {
    this.document = ((DocumentJ)args[0]);
  }
  @JRubyMethod(name={"doc"})
  public IRubyObject getDocument(ThreadContext context) {
    return this.document;
  }
  @JRubyMethod(name={"register_namespace"})
  public IRubyObject registerNamespace(ThreadContext context, IRubyObject pPrefix, IRubyObject pUri) {
    String prefix = pPrefix.asJavaString();
    String uri = pUri.asJavaString();
    this.namespaceMapping.put(prefix, uri);

    return context.getRuntime().getTrue();
  }

  @JRubyMethod(name={"register_namespaces"}, rest=true)
  public IRubyObject registerNamespaces(ThreadContext context, IRubyObject[] args) {
    for (int i = 0; i < args.length; i++) {
      if ((args[i] instanceof RubyString)) {
        String str = args[i].asJavaString();
        int j = str.indexOf(':');
        this.namespaceMapping.put(str.substring(0, j), str.substring(j + 1));
      }
      else
      {
        Iterator i$;
        if ((args[i] instanceof RubyHash)) {
          RubyHash hash = (RubyHash)args[i];
          for (i$ = hash.entrySet().iterator(); i$.hasNext(); ) { Object e = i$.next();
            Map.Entry entry = (Map.Entry)e;
            String prefix = UtilJ.toJavaString(entry.getKey());
            String uri = UtilJ.toJavaString(entry.getValue());
            this.namespaceMapping.put(prefix, uri); }
        }
        else if ((args[i] instanceof RubyArray)) {
          RubyArray array = (RubyArray)args[i];

          for (IRubyObject obj : UtilJ.toStringArray(context, array, 0)) {
            String str = obj.asJavaString();
            int x = str.indexOf(':');
            this.namespaceMapping.put(str.substring(0, x), str.substring(x + 1));
          }
        }
        else
        {
          throw context.getRuntime().newArgumentError("unsupported argument type " + args[i].getMetaClass().getName());
        }
      }
    }
    return this;
  }
  @JRubyMethod(name={"register_namespaces_from_node"})
  public IRubyObject registerNamespacesFromNode(ThreadContext context, IRubyObject pNode) {
    NodeJ node = (NodeJ)pNode;
    NamespacesJ namespaces = node.getNamespaces(context);
    IRubyObject ns = namespaces.getNamespace(context);
    if ((ns instanceof NamespaceJ)) {
      NamespaceJ nss = (NamespaceJ)ns;
      IRubyObject prefix = nss.getPrefix(context);
      IRubyObject uri = nss.getHref(context);
      if ((!prefix.isNil()) && (!uri.isNil()))
      {
        this.namespaceMapping.put(prefix.asJavaString(), uri.asJavaString());
      }
    }
    return this;
  }

  @JRubyMethod(name={"find"})
  public IRubyObject find(ThreadContext context, IRubyObject pXpath) {
    IRubyObject[] array = new IRubyObject[this.namespaceMapping.size()];
    int i = 0;
    for (Map.Entry e : this.namespaceMapping.entrySet()) {
      array[i] = context.getRuntime().newString((String)e.getKey() + ":" + (String)e.getValue());
      i++;
    }
    if ((this.node instanceof NodeJ)) {
      return XPathObjectJ.newInstance(context, pXpath, this.node, array);
    }
    return XPathObjectJ.newInstance(context, pXpath, this.document, array);
  }

  @JRubyMethod(name={"node="})
  public void setNode(ThreadContext context, IRubyObject pNode) {
    this.node = ((NodeJ)pNode);
  }
}