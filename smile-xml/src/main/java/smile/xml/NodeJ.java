package smile.xml;

import java.util.ArrayList;
import java.util.List;

import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyHash;
import org.jruby.RubyModule;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.anno.JRubyConstant;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import smile.xml.util.UtilJ;
import smile.xml.xpath.XPathContextJ;
import smile.xml.xpath.XPathObjectJ;
import smile.xml.xpath.XPointerJ;

public class NodeJ extends BaseJ<Node>
{
  private static final long serialVersionUID = -7355585454179020932L;
  public static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
  {
    public IRubyObject allocate(Ruby runtime, RubyClass klass)
    {
      return new NodeJ(runtime, klass);
    }
  };

  @JRubyConstant
  public static final int ATTRIBUTE_NODE = 2;

  @JRubyConstant
  public static final int CDATA_SECTION_NODE = 4;

  @JRubyConstant
  public static final int COMMENT_NODE = 8;

  @JRubyConstant
  public static final int DOCUMENT_FRAG_NODE = 11;

  @JRubyConstant
  public static final int DOCUMENT_NODE = 9;

  @JRubyConstant
  public static final int DOCUMENT_TYPE_NODE = 10;

  @JRubyConstant
  public static final int ELEMENT_NODE = 1;

  @JRubyConstant
  public static final int ENTITY_NODE = 6;

  @JRubyConstant
  public static final int ENTITY_REFERENCE_NODE = 5;

  @JRubyConstant
  public static final int NOTATION_NODE = 12;

  @JRubyConstant
  public static final int PROCESSING_INSTRUCTION_NODE = 7;

  @JRubyConstant
  public static final int TEXT_NODE = 3;

  @JRubyConstant
  public static final int FRAGMENT_NODE = 11;
    
  // TODO
  @JRubyConstant
  public static final int SPACE_DEFAULT = -1;

  // TODO
  @JRubyConstant
  public static final int SPACE_PRESERVE = -1;

  public static RubyClass define(Ruby runtime) { RubyModule module = UtilJ.getModule(runtime, new String[] { "LibXML", "XML" });
    RubyClass result = module.defineClassUnder("Node", runtime.getObject(), ALLOCATOR);
    result.defineAnnotatedMethods(NodeJ.class);
    result.defineAnnotatedConstants(NodeJ.class);
    return result; }

  public static RubyClass getRubyClass(Ruby runtime)
  {
    return UtilJ.getClass(runtime, new String[] { "LibXML", "XML", "Node" });
  }

  public static NodeJ newInstance(ThreadContext context) {
    return (NodeJ)getRubyClass(context.getRuntime()).newInstance(context, new IRubyObject[0], null);
  }

  public static NodeJ newInstance(ThreadContext context, IRubyObject name, IRubyObject content, IRubyObject namespace)
  {
    return (NodeJ)getRubyClass(context.getRuntime()).newInstance(context, new IRubyObject[] { name, content, namespace }, null);
  }

  private boolean docPresent = false;
  
  private boolean outputEscaping = true;

  protected NodeJ(Ruby ruby, RubyClass clazz)
  {
    super(ruby, clazz);
  }

  public boolean isDocPresent() {
	  return docPresent;
  }
  
  @JRubyMethod(name={"initialize"}, optional=3)
  public void initialize(ThreadContext context, IRubyObject[] args) {
    RubyString name = (RubyString)(RubyString)(args.length > 0 ? args[0] : null);
    RubyString content = (RubyString)(RubyString)(args.length > 1 ? args[1] : null);
    NamespaceJ namespace = (NamespaceJ)(NamespaceJ)(args.length > 2 ? args[2] : null);

    if (name != null) {
      Document doc = UtilJ.getBuilder().newDocument();
      Element elemet = doc.createElement(name.asJavaString());
      if ((content != null) && (!content.isNil()))
        elemet.setTextContent(content.asJavaString());
      setJavaObject(elemet);
    }
  }

  @JRubyMethod(name={"new_cdata"}, module=true)
  public static NodeJ newCdata(ThreadContext context, IRubyObject klass, IRubyObject pContent) {
    RubyString content = (RubyString)pContent;
    Document doc = UtilJ.getBuilder().newDocument();
    NodeJ node = newInstance(context);
    node.setJavaObject(doc.createCDATASection(content.asJavaString()));
    return node;
  }

  @JRubyMethod(name={"new_comment"}, module=true)
  public static NodeJ newComment(ThreadContext context, IRubyObject klass, IRubyObject pContent) {
    RubyString content = (RubyString)pContent;
    Document doc = UtilJ.getBuilder().newDocument();
    NodeJ node = newInstance(context);
    node.setJavaObject(doc.createComment(content.asJavaString()));
    return node;
  }

  @JRubyMethod(name={"new_text"}, module=true)
  public static NodeJ newText(ThreadContext context, IRubyObject klass, IRubyObject pContent) {
    RubyString content = (RubyString)pContent;
    Document doc = UtilJ.getBuilder().newDocument();
    NodeJ node = newInstance(context);
    node.setJavaObject(doc.createTextNode(content.asJavaString()));
    return node;
  }

  @JRubyMethod(name={"<<"}, alias={"child_add"})
  public NodeJ add(ThreadContext context, IRubyObject arg)
  {
    if ((arg instanceof NodeJ)) {
    	
      Node node = (Node)((NodeJ)arg).getJavaObject();
      
		if( isDocPresent() && 
				getJavaObject().getOwnerDocument().equals( node.getOwnerDocument() ) == false )
			throw ErrorJ.newRaiseException(context, " Nodes belong to different documents.  You must first import the node by calling XML::Document.import.");

      node = ((Node)getJavaObject()).getOwnerDocument().adoptNode(node);
      ((Node)getJavaObject()).appendChild(node);
    } else if ((arg instanceof RubyString)) {
      String s = ((Node)getJavaObject()).getNodeValue() == null ? "" : ((Node)getJavaObject()).getNodeValue();

      s = s + arg.asJavaString();
      ((Node)getJavaObject()).setTextContent(s);
    }
    else
    {
      throw context.getRuntime().newArgumentError("unsupported argument");
    }
    return this;
  }

  @JRubyMethod( name="equal?" )
  public RubyBoolean isEqlual(ThreadContext context, IRubyObject arg) throws Exception {
    
	  if (arg.isNil()) {
      return context.getRuntime().getFalse();
    }
    
    NodeJ other = (NodeJ)arg;

    if (((Node)other.getJavaObject()).equals(getJavaObject())) {
      return context.getRuntime().getTrue();
    }

    return context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"eql?"}, alias={"=="})
  public RubyBoolean isEql(ThreadContext context, IRubyObject arg) throws Exception {
    
	  if (arg.isNil()) {
      return context.getRuntime().getFalse();
    }
    
    if (!(arg instanceof NodeJ)) {
      throw context.getRuntime().newTypeError("");
    }
    
    NodeJ other = (NodeJ)arg;

    if (((Node)other.getJavaObject()).equals(getJavaObject())) {
      return context.getRuntime().getTrue();
    }

//    if ( other.getJavaObject().getOwnerDocument().equals( getJavaObject().getOwnerDocument() ) == false ) {
//        return context.getRuntime().getFalse();
//      }

    boolean r = toString(context, new IRubyObject[0]).equals(other.toString(context, new IRubyObject[0]));

    return r ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"base="})
  public void setBase(ThreadContext context, IRubyObject pBase)
  {
    if ((getJavaObject() instanceof Element))
    {
      ((Element)getJavaObject()).setAttributeNS("xml", "base", pBase.asJavaString());
    }
    else if (((Node)getJavaObject()).getAttributes() != null) {
      Attr attr = ((Node)getJavaObject()).getOwnerDocument().createAttributeNS("xml", "base");
      ((Node)getJavaObject()).getAttributes().setNamedItemNS(attr);
    } else {
      Attr attr = ((Node)getJavaObject()).getOwnerDocument().createAttributeNS("xml", "base");
      ((Node)getJavaObject()).appendChild(attr);
    }
  }

  @JRubyMethod(name={"[]"}, alias={"property"})
  public RubyString getProperty(ThreadContext context, IRubyObject pName) {
    if ((pName instanceof RubySymbol)) {
      pName = ((RubySymbol)pName).asString();
    }
    RubyString name = (RubyString)pName;
    return context.getRuntime().newString(((Node)getJavaObject()).getAttributes().getNamedItem(name.asJavaString()).getNodeValue());
  }

  @JRubyMethod(name={"[]="})
  public void setProperty(ThreadContext context, IRubyObject pName, IRubyObject pValue)
  {
    if ((pName instanceof RubySymbol)) {
      pName = ((RubySymbol)pName).asString();
    }
    RubyString name = (RubyString)pName;
    RubyString value = (RubyString)pValue;

    if ((getJavaObject() instanceof Element)) {
      ((Element)getJavaObject()).setAttribute(name.asJavaString(), value.asJavaString());
    }
    else
    {
      throw context.getRuntime().newArgumentError("");
    }
  }

  @JRubyMethod(name={"base"})
  public IRubyObject getBase(ThreadContext context)
  {
    Node item = ((Node)getJavaObject()).getAttributes().getNamedItemNS("xml", "base");

    if (item == null)
      return context.getRuntime().getNil();
    AttrJ attr = AttrJ.newInstance(context);
    attr.setJavaObject(item);
    return attr;
  }

  @JRubyMethod(name={"attribute?"})
  public RubyBoolean isAttribute(ThreadContext context) {
    boolean r = ((Node)getJavaObject()).getNodeType() == 2;
    return r ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"attribute_decl?"})
  public RubyBoolean isAttributeDecl(ThreadContext context) {
    throw context.getRuntime().newArgumentError("unsupported");
  }
  @JRubyMethod(name={"attributes"}, alias={"properties"})
  public AttributesJ getAttributes(ThreadContext context) {
    AttributesJ attributes = AttributesJ.newInstance(context);
    attributes.setJavaObject(getJavaObject());
    return attributes;
  }

  @JRubyMethod(name={"attributes?"}, alias={"properties?"})
  public RubyBoolean hasAttributes(ThreadContext context) {
    boolean r = ((Node)getJavaObject()).getAttributes().getLength() != 0;
    return r ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"base_uri"})
  public IRubyObject getBaseUri(ThreadContext context) {
	String uri = getJavaObject().getBaseURI();
	if( uri == null )
		return context.getRuntime().getNil();
    return context.getRuntime().newString( uri );
  }
  
  @JRubyMethod(name={"base_uri="})
  public void setBaseUri(ThreadContext context, RubyString uri) {
    throw context.getRuntime().newArgumentError("unsupported");
  }

  @JRubyMethod(name="empty?")
  public RubyBoolean isEmpty(ThreadContext context) throws Exception {
	  
    boolean r = getJavaObject().getTextContent() == null || getJavaObject().getTextContent().isEmpty();
    if( r )
    	r = getChild(context).isNil();
    return UtilJ.toBool(context, r );
  }

  @JRubyMethod(name={"entity_ref?"})
  public RubyBoolean isEntryRef(ThreadContext context)
  {
    boolean r = ((Node)getJavaObject()).getNodeType() == 5;
    return r ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"cdata?"})
  public RubyBoolean isCdata(ThreadContext context)
  {
    boolean r = ((Node)getJavaObject()).getNodeType() == 4;
    return r ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"child="})
  public void setChild(ThreadContext context, IRubyObject pChild) {
    NodeJ child = (NodeJ)pChild;
    Node old = ((Node)getJavaObject()).getFirstChild();
    Node n = ((Node)getJavaObject()).getOwnerDocument().adoptNode((Node)child.getJavaObject());
    if (old == null)
      ((Node)getJavaObject()).appendChild(n);
    else
      ((Node)getJavaObject()).insertBefore(n, old);
  }

  @JRubyMethod(name={"first"}, alias={"child"})
  public IRubyObject getChild(ThreadContext context) {
    NodeList list = getJavaObject().getChildNodes();
    for( int i=0; i<list.getLength(); i++ ) {
    	NodeJ node;
    	switch( list.item(i).getNodeType() ) {
    	case Node.ELEMENT_NODE:
    	    node = newInstance(context); 
    	    node.setDocPresent( isDocPresent() );
    	    node.setJavaObject( list.item(i) );
    	    return node;
    	}
    }
    return context.getRuntime().getNil();
  }

  @JRubyMethod(name={"child?"}, alias={"first", "children?"})
  public RubyBoolean hasChild(ThreadContext context) {
    boolean r = ((Node)getJavaObject()).getFirstChild() != null;
    return r ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"last"})
  public NodeJ getLast(ThreadContext context) {
    NodeJ node = newInstance(context);
    node.setDocPresent( isDocPresent() );
    node.setJavaObject(((Node)getJavaObject()).getLastChild());
    return node;
  }
  @JRubyMethod(name={"last?"})
  public RubyBoolean hastLast(ThreadContext context) {
    return UtilJ.toBool(context, ((Node)getJavaObject()).getLastChild() != null);
  }
  @JRubyMethod(name={"children"})
  public RubyArray getChildren(ThreadContext context) {
    NodeList list = ((Node)getJavaObject()).getChildNodes();
    List array = new ArrayList(list.getLength());
    for (int i = 0; i < list.getLength(); i++) {
      NodeJ node = newInstance(context);
      node.setDocPresent( isDocPresent() );
      node.setJavaObject(list.item(i));
      array.add(node);
    }
    return context.getRuntime().newArray(array);
  }
  @JRubyMethod(name={"clone"}, alias={"copy", "dup"})
  public NodeJ clone(ThreadContext context, IRubyObject pDeep) {
    RubyBoolean deep = (RubyBoolean)pDeep;
    NodeJ node = newInstance(context);
    node.setDocPresent( isDocPresent() );
    node.setJavaObject(((Node)getJavaObject()).cloneNode(deep.isNil() ? false : deep.isTrue()));

    return node;
  }

  @JRubyMethod(name={"comment?"})
  public RubyBoolean isComment(ThreadContext context) {
    boolean r = ((Node)getJavaObject()).getNodeType() == 8;
    return r ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"content"})
  public RubyString getContent(ThreadContext context)
  {
    return context.getRuntime().newString(((Node)getJavaObject()).getTextContent());
  }
  
  @JRubyMethod(name={"content="})
  public void setContent(ThreadContext context, IRubyObject pContent) {
    RubyString content = (RubyString)pContent;
    ((Node)getJavaObject()).setTextContent(content.asJavaString());
  }

  @JRubyMethod(name={"content_stripped"})
  public RubyString getContentStripped(ThreadContext context) {
    return context.getRuntime().newString(((Node)getJavaObject()).getTextContent().trim());
  }

  @JRubyMethod(name="context", rest = true )
  public IRubyObject getContext(ThreadContext context, IRubyObject[] args ) {

	  XPathContextJ result = XPathContextJ.newInstance( context, getDoc(context) );	  
	  result.setNode(context, this);

	  for( IRubyObject arg : args ) {
		  if( arg instanceof RubyString ) {
			  IRubyObject[] array = { arg };
			  result.registerNamespaces( context, array );
		  } else  {
			  
			  throw context.getRuntime().newArgumentError("unsupported");
		  }
	  }
	  return result;
  }
  
  @JRubyMethod(name={"debug"})
  public RubyBoolean debug(ThreadContext context) {
    return context.getRuntime().getFalse();
  }
  @JRubyMethod(name={"doc"})
  public DocumentJ getDoc(ThreadContext context) {
    DocumentJ doc = DocumentJ.newInstance(context);
    doc.setJavaObject(((Node)getJavaObject()).getOwnerDocument());
    return doc;
  }
  @JRubyMethod(name={"document?"})
  public RubyBoolean isDocument(ThreadContext context) {
    return context.getRuntime().getFalse();
  }
  @JRubyMethod(name={"docbook_doc?"})
  public RubyBoolean isDocbookDoc(ThreadContext context) {
    return context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"doctype?"})
  public RubyBoolean isDoctype(ThreadContext context) {
    boolean r = ((Node)getJavaObject()).getNodeType() == 10;
    return r ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"dtd?"})
  public RubyBoolean isDtd(ThreadContext context)
  {
    return context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"element?"})
  public RubyBoolean isElement(ThreadContext context) {
    boolean r = ((Node)getJavaObject()).getNodeType() == 1;
    return r ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"element_decl?"})
  public RubyBoolean isElementDecl(ThreadContext context)
  {
    return context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"each"}, alias={"each_child"})
  public void each(ThreadContext context, Block block) {
    for (NodeJ node : childrenAsList(context))
    {
      block.yield(context, node);
      if (block.isEscaped())
        break;
    }
  }

  @JRubyMethod(name={"each_attr"})
  public void eachAttr(ThreadContext context, Block block) {
    for (NodeJ node : attributesAsList(context))
    {
      block.yield(context, node);
      if (block.isEscaped())
        break;
    }
  }

  @JRubyMethod(name={"each_element"})
  public void iterateOverElements(ThreadContext context, Block block) {
    for (NodeJ node : elementsAsList(context))
    {
      block.yield(context, node);
      if (block.isEscaped())
        break;
    }
  }

  @JRubyMethod(name={"find"}, required=1, optional=1)
  public XPathObjectJ find(ThreadContext context, IRubyObject[] args) {
    RubyString expression = (RubyString)args[0];

    if (args.length > 1);
    return XPathObjectJ.newInstance(context, expression, this, new IRubyObject[0]);
  }

  @JRubyMethod(name={"find_first"}, required=1, optional=1)
  public IRubyObject findFirst(ThreadContext context, IRubyObject[] args) throws Exception
  {
    RubyString expression = (RubyString)args[0];

    if (args.length > 1);
    return XPathObjectJ.newInstance(context, expression, this, new IRubyObject[0]).getFirst(context);
  }

  @JRubyMethod(name={"fragment?"})
  public RubyBoolean isFragment(ThreadContext context) {
    return isNodeType(context, 11);
  }

  @JRubyMethod(name={"text?"})
  public RubyBoolean isText(ThreadContext context) {
    return isNodeType(context, 3);
  }

  @JRubyMethod(name={"html_doc?"})
  public RubyBoolean isHtmlDoc(ThreadContext context) {
    return context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"to_s"}, optional=1)
  public RubyString toString(ThreadContext context, IRubyObject[] args)
    throws Exception
  {
    RubyHash hash;
    if (args.length != 0) {
      hash = (RubyHash)args[0];
    }

    String string = UtilJ.toString( getJavaObject(), outputEscaping );
    return context.getRuntime().newString(string);
  }

  @JRubyMethod(name={"inner_xml"})
  public RubyString getInnerXml(ThreadContext context, IRubyObject pHash) throws Exception {
    RubyHash hash = (RubyHash)pHash;

    return context.getRuntime().newString(UtilJ.toString( getJavaObject(), outputEscaping ) );
  }

  @JRubyMethod(name={"lang"})
  public IRubyObject getLang(ThreadContext context) {
    Node attr = ((Node)getJavaObject()).getAttributes().getNamedItem("xml:lang");

    if (attr == null) {
      return context.getRuntime().getNil();
    }
    return context.getRuntime().newString(attr.getNodeValue());
  }
  @JRubyMethod(name={"lang="})
  public void setLang(ThreadContext context, IRubyObject pString) {
    RubyString string = (RubyString)pString;

    Node attr = ((Node)getJavaObject()).getAttributes().getNamedItem("xml:lang");

    if (attr == null) {
      attr = ((Node)getJavaObject()).getOwnerDocument().createAttribute("xml:lang");

      ((Node)getJavaObject()).getAttributes().setNamedItem(attr);
    }
    attr.setNodeValue(string.asJavaString());
  }

  @JRubyMethod(name={"namespace?"})
  public RubyBoolean isNamespace(ThreadContext context) {
    return UtilJ.toBool(context, false);
  }

  @JRubyMethod(name={"namespacess="})
  public NamespacesJ getNamespaces(ThreadContext context) {
    return NamespacesJ.newInstance(context, this);
  }

  @JRubyMethod(name={"next"})
  public IRubyObject getNext(ThreadContext context) {
    
    Node n = getJavaObject().getNextSibling();
    while( n != null && n.getNodeType() != Node.ELEMENT_NODE ) {
    	n = n.getNextSibling();
    }
    
    if( n == null || n.getNodeType() != Node.ELEMENT_NODE ) {
    	return context.getRuntime().getNil();
    }
    
    NodeJ node = newInstance(context);
    node.setDocPresent( isDocPresent() );
    node.setJavaObject( n );
    return node;
  }
  @JRubyMethod(name={"next="})
  public void setNext(ThreadContext context, IRubyObject pNode) {
    NodeJ node = (NodeJ)pNode;
    context.getRuntime().newArgumentError("unsupported");
  }

  @JRubyMethod(name={"next?"})
  public RubyBoolean hasNext(ThreadContext context) {
    return UtilJ.toBool(context, ((Node)getJavaObject()).getNextSibling() != null);
  }

  @JRubyMethod(name={"type"})
  public RubyFixnum getType(ThreadContext context) {
    return context.getRuntime().newFixnum(((Node)getJavaObject()).getNodeType());
  }
  @JRubyMethod(name={"sibling="})
  public void addSibling(ThreadContext context, IRubyObject pNode) {
    NodeJ node = (NodeJ)pNode;

    throw context.getRuntime().newArgumentError("unsupported");
  }

  @JRubyMethod(name={"remove!"})
  public void remove(ThreadContext context) {
    if (((Node)getJavaObject()).getParentNode() != null)
      ((Node)getJavaObject()).getParentNode().removeChild((Node)getJavaObject());
  }

  @JRubyMethod(name={"node_type"})
  public RubyFixnum getNodeType(ThreadContext context) {
    return context.getRuntime().newFixnum(((Node)getJavaObject()).getNodeType());
  }

  @JRubyMethod(name={"node_type_name"})
  public RubyString getTypeName(ThreadContext context) {
    switch (((Node)getJavaObject()).getNodeType())
    {
    case 2:
      return context.getRuntime().newString("attribute");
    case 9:
      return context.getRuntime().newString("document_xml");
    case 1:
      return context.getRuntime().newString("element");
    case 3:
      return context.getRuntime().newString("text");
    case 8:
      return context.getRuntime().newString("comment");
    case 4:
      return context.getRuntime().newString("cdata");
    case 5:
    case 6:
    case 7: } return context.getRuntime().newString("what ever");
  }

  @JRubyMethod(name={"find?"})
  public RubyBoolean hasFirst(ThreadContext context)
  {
    return UtilJ.toBool(context, ((Node)getJavaObject()).getFirstChild() != null);
  }

  @JRubyMethod(name={"notation?"})
  public RubyBoolean isNotation(ThreadContext context) {
    return isNodeType(context, 12);
  }
  @JRubyMethod(name={"output_escaping="})
  public void setOutputEscaping(ThreadContext context, IRubyObject pValue) {
    this.outputEscaping = ((RubyBoolean)pValue).isTrue();
  }

  @JRubyMethod(name={"output_escaping?"})
  public RubyBoolean isOutputEscaping(ThreadContext context)
  {
    return context.getRuntime().newBoolean( outputEscaping );
  }

  @JRubyMethod(name={"parent?"})
  public RubyBoolean hasParent(ThreadContext context) {
    return UtilJ.toBool(context, ((Node)getJavaObject()).getParentNode() != null);
  }

  @JRubyMethod(name={"parent"})
  public IRubyObject getParent(ThreadContext context) {
    if (((Node)getJavaObject()).getParentNode() == null) {
      return context.getRuntime().getNil();
    }
    NodeJ node = newInstance(context);
    node.setDocPresent( isDocPresent() );
    node.setJavaObject(((Node)getJavaObject()).getParentNode());
    return node;
  }

  @JRubyMethod(name={"path"})
  public IRubyObject getPath(ThreadContext context) {
    throw context.getRuntime().newArgumentError("unsupported");
  }

  @JRubyMethod(name={"pi?"})
  public RubyBoolean isPi(ThreadContext context) {
    return isNodeType(context, 7);
  }

  @JRubyMethod(name={"pointer"})
  public XPointerJ getPointer(ThreadContext context)
  {
    return null;
  }

  @JRubyMethod(name={"xinclude_start?"})
  public RubyBoolean isXincludeStart(ThreadContext context)
  {
    return context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"xinclude_end?"})
  public RubyBoolean isXincludeEnd(ThreadContext context)
  {
    return context.getRuntime().getFalse();
  }
  @JRubyMethod(name={"xlink?"})
  public RubyBoolean isXlink(ThreadContext context) throws Exception {
    System.out.println(toString(context, new IRubyObject[0]));
    System.out.println(((Node)getJavaObject()).getTextContent());

    return context.getRuntime().getFalse();
  }

  @JRubyMethod(name={"xlink_type"})
  public IRubyObject getXlinkType(ThreadContext context)
  {
    return context.getRuntime().getNil();
  }

  @JRubyMethod(name={"xlink_type_name"})
  public IRubyObject getXlinkTypeName(ThreadContext context)
  {
    return context.getRuntime().getNil();
  }

  @JRubyMethod(name={"prev"})
  public IRubyObject getPrev(ThreadContext context) {
    if (((Node)getJavaObject()).getPreviousSibling() == null) {
      return context.getRuntime().getNil();
    }
    NodeJ node = newInstance(context);
    node.setDocPresent( isDocPresent() );
    node.setJavaObject(((Node)getJavaObject()).getPreviousSibling());
    return node;
  }

  @JRubyMethod(name={"prev?"})
  public RubyBoolean hasPrev(ThreadContext context) {
    return UtilJ.toBool(context, ((Node)getJavaObject()).getPreviousSibling() != null);
  }
  @JRubyMethod(name={"prev="})
  public void setPrev(ThreadContext context, IRubyObject pNode) {
    NodeJ node = (NodeJ)pNode;
    throw context.getRuntime().newArgumentError("unsupported");
  }

  @JRubyMethod(name={"line_num"})
  public RubyFixnum getLineNumber(ThreadContext context) {
    return context.getRuntime().newFixnum(-1);
  }

  
  @JRubyMethod(name="space_preserve=")
  public void setSpacePreserve(ThreadContext context, IRubyObject pValue) {
	  // TODO
  }

  @JRubyMethod(name="space_preserve")
  public IRubyObject getSpacePreserve(ThreadContext context) {
	  // TODO
	  return context.getRuntime().newFixnum( SPACE_DEFAULT );
  }

  @JRubyMethod(name="name")
  public RubyString getName(ThreadContext context) {
	String name = getJavaObject().getNodeName();
    return context.getRuntime().newString( name );
  }

  @JRubyMethod(name={"name="})
  public void setName(ThreadContext context, RubyString name) {
    throw context.getRuntime().newArgumentError("unsupported");
  }

  @JRubyMethod(name={"value"})
  public IRubyObject getValue(ThreadContext context) {
    return context.getRuntime().newString(((Node)getJavaObject()).getNodeValue());
  }

  private RubyBoolean isNodeType(ThreadContext context, int type)
  {
    return UtilJ.toBool(context, ((Node)getJavaObject()).getNodeType() == type);
  }

  private List<NodeJ> childrenAsList(ThreadContext context)
  {
    NodeList list = ((Node)getJavaObject()).getChildNodes();
    List array = new ArrayList(list.getLength());
    for (int i = 0; i < list.getLength(); i++) {
      NodeJ node = newInstance(context);
      node.setDocPresent( isDocPresent() );
      node.setJavaObject(list.item(i));
      array.add(node);
    }
    return array;
  }

  private List<NodeJ> attributesAsList(ThreadContext context)
  {
    NamedNodeMap list = ((Node)getJavaObject()).getAttributes();
    List array = new ArrayList(list.getLength());
    for (int i = 0; i < list.getLength(); i++) {
      NodeJ node = newInstance(context);
      node.setDocPresent( isDocPresent() );
      node.setJavaObject(list.item(i));
      array.add(node);
    }
    return array;
  }

  private List<NodeJ> elementsAsList(ThreadContext context)
  {
    NodeList list = ((Node)getJavaObject()).getChildNodes();
    List array = new ArrayList(list.getLength());
    for (int i = 0; i < list.getLength(); i++) {
      Node obj = list.item(i);
      if (obj.getNodeType() != 1)
        continue;
      NodeJ node = newInstance(context);
      node.setDocPresent( isDocPresent() );
      node.setJavaObject(obj);
      array.add(node);
    }
    return array;
  }

public void setDocPresent(boolean b) {
	this.docPresent = b;
}
	@Override
	public String toString() {
		return UtilJ.toString( getJavaObject(), true );
	}

}