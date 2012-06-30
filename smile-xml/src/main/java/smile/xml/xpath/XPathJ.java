package smile.xml.xpath;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.anno.JRubyConstant;
import smile.xml.util.UtilJ;

public class XPathJ
{

  @JRubyConstant
  public static final QName UNDEFINED = null;

  @JRubyConstant
  public static final QName NODESET = XPathConstants.NODESET;

  @JRubyConstant
  public static final QName BOOLEAN = XPathConstants.BOOLEAN;

  @JRubyConstant
  public static final QName NUMBER = XPathConstants.NUMBER;

  @JRubyConstant
  public static final QName STRING = XPathConstants.STRING;

  public static RubyModule define(Ruby runtime)
  {
    RubyModule parent = UtilJ.getModule(runtime, new String[] { "LibXML", "XML" });
    RubyModule result = parent.defineModuleUnder("XPath");
    result.defineAnnotatedMethods(XPathJ.class);
    result.defineAnnotatedConstants(XPathJ.class);
    return result;
  }
}