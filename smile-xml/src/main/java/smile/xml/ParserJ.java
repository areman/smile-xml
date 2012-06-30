package smile.xml;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyModule;
import org.jruby.RubyModule.KindOf;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.xml.sax.InputSource;
import smile.xml.util.UtilJ;

@JRubyClass(name={"Parser"})
public class ParserJ extends RubyObject
{
  private static final long serialVersionUID = 4634367713101505188L;
  public static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
  {
    public IRubyObject allocate(Ruby runtime, RubyClass klass)
    {
      return new ParserJ(runtime, klass); }  } ;
  private static RubyClass rubyClass;
  private RubyString fileName;
  private RubyString string;

  public static RubyClass define(Ruby runtime) { try { RubyModule module = (RubyModule)runtime.getModule("LibXML").getConstant("XML");
      RubyClass result = module.defineClassUnder("Parser", runtime.getObject(), ALLOCATOR);

      result.kindOf = new RubyModule.KindOf() {
        public boolean isKindOf(IRubyObject obj, RubyModule type) {
          return obj instanceof ParserJ;
        }
      };
      result.defineAnnotatedMethods(ParserJ.class);

      return result;
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }

  private static synchronized RubyClass findRubyClass(Ruby runtime)
  {
    if (rubyClass == null)
      rubyClass = ((RubyModule)runtime.fastGetModule("LibXML").fastGetConstant("XML")).fastGetClass("Parser");
    return rubyClass;
  }

  public static RubyClass getRubyClass(Ruby runtime) {
    return rubyClass == null ? findRubyClass(runtime) : rubyClass;
  }

  public ParserJ(Ruby runtime, RubyClass metaClass)
  {
    super(runtime, metaClass);
  }

  public ParserJ(ThreadContext context) {
    this(context.getRuntime(), getRubyClass(context.getRuntime()));
  }

  public static ParserJ fromFile(ThreadContext context, IRubyObject pName, IRubyObject pHash) {
    return fromFile(context, getRubyClass(context.getRuntime()), pName, pHash);
  }

  @JRubyMethod(name={"file"}, module=true)
  public static ParserJ fromFile(ThreadContext context, IRubyObject klass, IRubyObject pName, IRubyObject pHash) {
    RubyString name = (RubyString)pName;

    if (!pHash.isNil()) {
      RubyHash hash = (RubyHash)pHash;
      hash.get(context.getRuntime().newSymbol("encoding"));
      hash.get(context.getRuntime().newSymbol("options"));
    }

    ParserJ parser = new ParserJ(context);
    parser.fileName = name;
    return parser;
  }
  @JRubyMethod(name={"initialize"}, optional=1)
  public void initialize(ThreadContext context, IRubyObject[] args) {
  }

  @JRubyMethod(name={"string="})
  public void setString(ThreadContext context, IRubyObject pString) {
    this.string = ((RubyString)pString);
  }

  @JRubyMethod(name={"parse"})
  public DocumentJ parse(ThreadContext context) throws Exception {
    DocumentJ document = DocumentJ.newInstance(context);
    if (this.fileName != null)
      document.setJavaObject(UtilJ.getBuilder().parse(this.fileName.asJavaString()));
    if (this.string != null)
      document.setJavaObject(UtilJ.getBuilder().parse(new InputSource(new StringReader(this.string.asJavaString()))));
    return document;
  }
  @JRubyMethod(name={"document"}, module=true)
  public static ParserJ fromDocument(ThreadContext context, IRubyObject pDocument) throws Exception {
    DocumentJ document = (DocumentJ)pDocument;
    throw context.getRuntime().newArgumentError("unsuported");
  }
  @JRubyMethod(name={"io="}, module=true)
  public static ParserJ fromIo(ThreadContext context, IRubyObject klass, IRubyObject io) throws Exception {
    RubyString string = (RubyString)io.callMethod(context, "read");
    ParserJ parser = new ParserJ(context);
    parser.string = string;
    return parser;
  }
  @JRubyMethod(name={"string"}, module=true, optional=1)
  public static ParserJ fromString(ThreadContext context, IRubyObject klass, IRubyObject[] args) throws Exception {
    RubyString string = (RubyString)args[0];
    RubyHash hash;
    if (args.length > 1) {
      hash = (RubyHash)args[1];
    }

    ParserJ parser = new ParserJ(context);
    parser.string = string;
    return parser;
  }

  public static class Options
  {
  }

  public static class Context
  {
  }
}