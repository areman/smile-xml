package smile.xml.xpath;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Node;

public class CustomNamespaceContext
  implements NamespaceContext
{
  private final Map<String, String> namespaceMapping = new HashMap<String, String>();
  private final Class<?> cl;
  private final Object resolver;

  public CustomNamespaceContext(Node node, String[] namespaces)
  {
    try
    {
      this.cl = Class.forName("com.sun.org.apache.xml.internal.utils.PrefixResolverDefault", true, getClass().getClassLoader());
      this.resolver = this.cl.getConstructor(new Class[] { Node.class }).newInstance(new Object[] { node });
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    for (String ns : namespaces) {
      int i = ns.indexOf(':');
      this.namespaceMapping.put(ns.substring(0, i), ns.substring(i + 1));
    }
  }

  public String getNamespaceURI(String prefix)
  {
    String uri = (String)this.namespaceMapping.get(prefix);
    if (uri != null)
      return uri;
    try {
      String r = (String)this.cl.getMethod("getNamespaceForPrefix", new Class[] { String.class }).invoke(this.resolver, new Object[] { prefix });
      return r;
    } catch (RuntimeException e) {
      throw e; } catch (Exception e) {
    	  throw new RuntimeException(e);
    }
    
  }

  public String getPrefix(String namespaceURI)
  {
    return null;
  }

  public Iterator<?> getPrefixes(String namespaceURI)
  {
    return null;
  }
}