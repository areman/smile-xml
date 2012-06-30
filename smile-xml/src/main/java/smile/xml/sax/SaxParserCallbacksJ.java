package smile.xml.sax;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import smile.xml.util.UtilJ;

public class SaxParserCallbacksJ
{
  public static RubyModule define(Ruby runtime)
  {
    RubyClass parent = UtilJ.getClass(runtime, new String[] { "LibXML", "XML", "SaxParser" });
    RubyModule result = parent.defineModuleUnder("Callbacks");
    result.defineAnnotatedMethods(SaxParserCallbacksJ.class);
    return result;
  }

  @JRubyMethod(name={"on_start_document"})
  public static void onStartDocument(ThreadContext context, IRubyObject self)
  {
  }

  @JRubyMethod(name={"on_start_element"})
  public static void onStartElement(ThreadContext context, IRubyObject self, IRubyObject name, IRubyObject attr)
  {
  }

  @JRubyMethod(name={"on_characters"})
  public static void onCharacter(ThreadContext context, IRubyObject self, IRubyObject arg)
  {
  }

  @JRubyMethod(name={"on_comment"})
  public static void onComment(ThreadContext context, IRubyObject self, IRubyObject arg)
  {
  }

  @JRubyMethod(name={"on_processing_instruction"})
  public static void onProcessingInstruction(ThreadContext context, IRubyObject self, IRubyObject target, IRubyObject data)
  {
  }

  @JRubyMethod(name={"on_cdata_block"})
  public static void onCDataBlock(ThreadContext context, IRubyObject self, IRubyObject cdata)
  {
  }

  @JRubyMethod(name={"on_end_element"})
  public static void onEndElement(ThreadContext context, IRubyObject self, IRubyObject name)
  {
  }

  @JRubyMethod(name={"on_end_document"})
  public static void onEndDocument(ThreadContext context, IRubyObject self)
  {
  }
}