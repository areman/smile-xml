package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import smile.xml.util.UtilJ;

public class XmlJ {

  public static RubyModule define(Ruby runtime) {
    RubyModule parent = UtilJ.getModule( runtime, "LibXML" );
    RubyModule result = parent.defineModuleUnder("XML");
    result.defineAnnotatedMethods(XmlJ.class);
    result.defineAnnotatedConstants(XmlJ.class);
    return result;
  }

  @JRubyMethod( name="default_keep_blanks=", module=true )
  public static void setDefaultKeepBlanks( ThreadContext context, IRubyObject self, IRubyObject pValue ) {
	  
  }

  @JRubyMethod( name="enabled_zlib?", module=true )
  public static IRubyObject isEnabledZlib( ThreadContext context, IRubyObject self ) {
	// TODO
	  return context.getRuntime().getFalse();
  }

  @JRubyMethod( name="enabled_zlib=", module=true )
  public static void setEnabledZlib( ThreadContext context, IRubyObject self, IRubyObject pValue ) {
	  // TODO
  }

  
}
