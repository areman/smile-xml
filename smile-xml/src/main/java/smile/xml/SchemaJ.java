package smile.xml;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import smile.xml.util.UtilJ;

public class SchemaJ extends BaseJ<Schema>
{
  private static final long serialVersionUID = -6424527398086774781L;
  private static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
  {
    public IRubyObject allocate(Ruby runtime, RubyClass klass)
    {
      return new SchemaJ(runtime, klass);
    }
  };

  public static RubyClass define(Ruby runtime)
  {
    RubyModule module = UtilJ.getModule(runtime, new String[] { "LibXML", "XML" });
    RubyClass result = module.defineClassUnder("Schema", runtime.getObject(), ALLOCATOR);
    result.defineAnnotatedMethods(SchemaJ.class);
    return result;
  }

  private static RubyClass getRubyClass(Ruby runtime)
  {
    return UtilJ.getClass(runtime, new String[] { "LibXML", "XML", "Schema" });
  }

  @JRubyMethod(name={"document"}, meta=true)
  public static SchemaJ fromDocument(ThreadContext context, IRubyObject klass, IRubyObject pDocument) throws SAXException {
    DocumentJ document = (DocumentJ)pDocument;
    SchemaFactory schemaFactory = UtilJ.getSchemaFactoryInstance();
    Source schemaFile = new DOMSource((Node)document.getJavaObject());
    Schema schema = schemaFactory.newSchema(schemaFile);

    SchemaJ result = newInstance(context);
    result.setJavaObject(schema);
    return result;
  }

  public static SchemaJ newInstance(ThreadContext context) {
    return (SchemaJ)getRubyClass(context.getRuntime()).newInstance(context, new IRubyObject[0], null);
  }

  private SchemaJ(Ruby ruby, RubyClass clazz) {
    super(ruby, clazz);
  }

  @JRubyMethod(name={"initialize"}, rest=true)
  public void initialize(ThreadContext context, IRubyObject[] args)
  {
  }
}