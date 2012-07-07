package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.anno.JRubyConstant;
import org.jruby.anno.JRubyMethod;
import org.jruby.anno.JRubyModule;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import smile.xml.util.UtilJ;

@JRubyModule(name="LibXML::XML")
public class XmlJ {

  public static RubyModule define(Ruby runtime) {
	  return UtilJ.defineModule(runtime, XmlJ.class);
  }

  @JRubyConstant
  public static final String VERSION = "1";
  
  @JRubyConstant
  public static final String VERNUM = "1";
  
  @JRubyMethod( name="default_substitute_entities=", module=true )
  public static void setDefaultSubstituteEntities( ThreadContext context, IRubyObject self, IRubyObject pValue ) {
	  
  }

  @JRubyMethod( name="indent_tree_output=", module=true )
  public static void setIndentTreeOutput( ThreadContext context, IRubyObject self, IRubyObject pValue ) {
	  
  }

  @JRubyMethod( name="indent_tree_output", module=true )
  public static IRubyObject getIndentTreeOutput( ThreadContext context, IRubyObject self ) {
	  return context.getRuntime().getFalse();
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
