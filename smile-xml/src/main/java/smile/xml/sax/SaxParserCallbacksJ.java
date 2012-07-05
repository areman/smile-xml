package smile.xml.sax;

import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.anno.JRubyMethod;
import org.jruby.anno.JRubyModule;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import smile.xml.util.UtilJ;

@JRubyModule( name="LibXML::XML::SaxParser::Callbacks" )
public class SaxParserCallbacksJ
{
  public static RubyModule define(Ruby runtime)
  {
	  return UtilJ.defineModule( runtime, SaxParserCallbacksJ.class );
  }

  @JRubyMethod(name="on_start_document")
  public static void onStartDocument(ThreadContext context, IRubyObject self)
  {
  }

  @JRubyMethod(name="on_start_element")
  public static void onStartElement(ThreadContext context, IRubyObject self, IRubyObject name, IRubyObject attr)
  {
  }

  @JRubyMethod(name="on_characters")
  public static void onCharacter(ThreadContext context, IRubyObject self, IRubyObject arg)
  {
  }

  @JRubyMethod(name="on_comment")
  public static void onComment(ThreadContext context, IRubyObject self, IRubyObject arg)
  {
  }

  @JRubyMethod(name="on_processing_instruction")
  public static void onProcessingInstruction(ThreadContext context, IRubyObject self, IRubyObject target, IRubyObject data)
  {
  }

  @JRubyMethod(name="on_cdata_block")
  public static void onCDataBlock(ThreadContext context, IRubyObject self, IRubyObject cdata)
  {
  }

  @JRubyMethod(name="on_end_element")
  public static void onEndElement(ThreadContext context, IRubyObject self, IRubyObject name)
  {
  }

  @JRubyMethod(name="on_end_document")
  public static void onEndDocument(ThreadContext context, IRubyObject self)
  {
  }

  @JRubyMethod(name="on_start_element_ns", rest=true) 
  public static void onStartElementNS(ThreadContext context, IRubyObject self, IRubyObject[] args )  
  {
	  //IRubyObject attributes, IRubyObject prefix, IRubyObject uri, IRubyObject namespaces
  }

  @JRubyMethod(name="on_end_element_ns") 
  public static void onEndElementNS(ThreadContext context, IRubyObject self, IRubyObject name, IRubyObject prefix, IRubyObject uri )
  {
  }
  
  @JRubyMethod(name="on_error")
  public static void onError(ThreadContext context, IRubyObject self, IRubyObject error )
  {
  }

  
}