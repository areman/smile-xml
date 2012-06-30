package smile.xml.sax;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;
import smile.xml.util.UtilJ;

public class SaxParserJ extends RubyObject
{
  private static final long serialVersionUID = -8712513236401964568L;
  public static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
  {
    public IRubyObject allocate(Ruby runtime, RubyClass klass)
    {
      return new SaxParserJ(runtime, klass); }  } ;
  private RubyString string;
  private RubyString fileName;
  private IRubyObject callbacks;

  public static RubyClass define(Ruby runtime) { RubyModule module = UtilJ.getModule(runtime, new String[] { "LibXML", "XML" });
    RubyClass result = module.defineClassUnder("SaxParser", runtime.getObject(), ALLOCATOR);
    result.defineAnnotatedMethods(SaxParserJ.class);
    return result; }

  public static RubyClass getRubyClass(Ruby runtime)
  {
    return UtilJ.getClass(runtime, new String[] { "LibXML", "XML", "SaxParser" });
  }

  @JRubyMethod(name={"string"}, module=true)
  public static IRubyObject fromString(ThreadContext context, IRubyObject pString) {
    SaxParserJ parser = new SaxParserJ(context);
    parser.string = ((RubyString)pString);
    return parser;
  }

  @JRubyMethod(name={"file"}, module=true)
  public static IRubyObject fromFile(ThreadContext context, IRubyObject pFile) {
    SaxParserJ parser = new SaxParserJ(context);
    parser.fileName = ((RubyString)pFile);
    return parser;
  }

  @JRubyMethod(name={"io"}, module=true)
  public static IRubyObject fromIo(ThreadContext context, IRubyObject pIo) {
    SaxParserJ parser = new SaxParserJ(context);
    parser.string = ((RubyString)pIo.callMethod(context, "read"));
    return parser;
  }

  public SaxParserJ(Ruby runtime, RubyClass metaClass)
  {
    super(runtime, metaClass);
  }

  public SaxParserJ(ThreadContext context) {
    this(context.getRuntime(), getRubyClass(context.getRuntime()));
  }

  @JRubyMethod(name={"initialize"}, optional=1)
  public IRubyObject initialize(ThreadContext context, IRubyObject[] args) {
    return this;
  }
  @JRubyMethod(name={"filename="})
  public void setFilename(ThreadContext context, IRubyObject pFileName) {
    this.fileName = ((RubyString)pFileName);
  }
  @JRubyMethod(name={"string="})
  public void setString(ThreadContext context, IRubyObject pFileName) {
    this.string = ((RubyString)pFileName);
  }
  @JRubyMethod(name={"string"})
  public IRubyObject getString(ThreadContext context) {
    return this.string;
  }
  @JRubyMethod(name={"callbacks="})
  public void setCallbacks(ThreadContext context, IRubyObject pCallbacks) {
    this.callbacks = pCallbacks;
  }
  @JRubyMethod(name={"callbacks"})
  public IRubyObject getCallbacks(ThreadContext context) {
    return this.callbacks;
  }

  @JRubyMethod(name={"parse"})
  public IRubyObject parse(ThreadContext context) throws SAXException, IOException {
    XMLReader xmlReader = XMLReaderFactory.createXMLReader();
    InputSource inputSource;
    if (this.string != null) {
      inputSource = new InputSource(new StringReader(this.string.asJavaString()));
    } else {
      FileReader reader = new FileReader(this.fileName.asJavaString());
      inputSource = new InputSource(reader);
    }

    xmlReader.setEntityResolver(new EntityResolver()
    {
      public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException, IOException
      {
        return new InputSource(new StringReader(""));
      }
    });
    xmlReader.setContentHandler(new Handler(context));
    xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", xmlReader.getContentHandler());

    xmlReader.parse(inputSource);

    return context.getRuntime().getTrue();
  }

  private class Handler extends DefaultHandler2
  {
    private final ThreadContext context;
    private boolean cdata = false;

    public Handler(ThreadContext context) {
      this.context = context;
    }

    public void setDocumentLocator(Locator locator)
    {
    }

    public void startDocument() throws SAXException
    {
      IRubyObject[] args = new IRubyObject[0];
      SaxParserJ.this.callbacks.callMethod(this.context, "on_start_document", args);
    }

    public void endDocument() throws SAXException
    {
      IRubyObject[] args = new IRubyObject[0];
      SaxParserJ.this.callbacks.callMethod(this.context, "on_end_document", args);
    }

    public void startElement(String uri, String localName, String qName, Attributes atts)
      throws SAXException
    {
      Map map = new HashMap();
      for (int i = 0; i < atts.getLength(); i++)
        map.put(this.context.getRuntime().newString(atts.getLocalName(i)), this.context.getRuntime().newString(atts.getValue(i)));
      IRubyObject[] args = { this.context.getRuntime().newString(localName), RubyHash.newHash(this.context.getRuntime(), map, this.context.getRuntime().getNil()) };
      SaxParserJ.this.callbacks.callMethod(this.context, "on_start_element", args);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
      IRubyObject[] args = { this.context.getRuntime().newString(localName) };
      SaxParserJ.this.callbacks.callMethod(this.context, "on_end_element", args);
    }

    public void characters(char[] ch, int start, int length) throws SAXException
    {
      IRubyObject[] args = { this.context.getRuntime().newString(String.valueOf(ch, start, length)) };
      if (this.cdata)
        SaxParserJ.this.callbacks.callMethod(this.context, "on_cdata_block", args);
      else
        SaxParserJ.this.callbacks.callMethod(this.context, "on_characters", args);
    }

    public void processingInstruction(String target, String data)
      throws SAXException
    {
      IRubyObject[] args = { this.context.getRuntime().newString(target), this.context.getRuntime().newString(data) };
      SaxParserJ.this.callbacks.callMethod(this.context, "on_processing_instruction", args);
    }

    public void startCDATA()
    {
      this.cdata = true;
    }

    public void endCDATA()
    {
      this.cdata = false;
    }

    public void comment(char[] ch, int start, int length) throws SAXException
    {
      IRubyObject[] args = { this.context.getRuntime().newString(String.valueOf(ch, start, length)) };
      SaxParserJ.this.callbacks.callMethod(this.context, "on_comment", args);
    }

    public void skippedEntity(String name)
      throws SAXException
    {
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
      throws SAXException
    {
    }

    public void startPrefixMapping(String prefix, String uri)
      throws SAXException
    {
    }

    public void endPrefixMapping(String prefix)
      throws SAXException
    {
    }
  }
}