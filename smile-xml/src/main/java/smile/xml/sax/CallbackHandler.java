package smile.xml.sax;

import java.util.HashMap;
import java.util.Map;

import org.jruby.RubyHash;
import org.jruby.RubyString;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

import smile.xml.ErrorJ;

public class CallbackHandler extends DefaultHandler2 {

	private final IRubyObject callback;
	private final ThreadContext context;
	private boolean cdata = false;

	public CallbackHandler( ThreadContext context, IRubyObject callback ) {
		this.context = context;
		this.callback = callback;
	}

	private void call( String method, IRubyObject...args ) {
		callback.callMethod( context, method, args );
	}
	
	private RubyString toString( String string ) {
		return context.getRuntime().newString( string );
	}

	private RubyHash toHash( Map<?,?> map ) {
		return RubyHash.newHash( context.getRuntime(), map, context.getRuntime().getNil() );
	}

	@Override
	public void startDocument() throws SAXException {
		call( "on_start_document" );
	}

	@Override
	public void endDocument() throws SAXException {
		call( "on_end_document" );
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		call( "on_error", ErrorJ.newInstance( context, e.getMessage()) );
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			
		Map<IRubyObject, IRubyObject> map = new HashMap<IRubyObject,IRubyObject>();
		for (int i = 0; i < atts.getLength(); i++)
			map.put( toString( atts.getLocalName(i) ), toString( atts.getValue(i) ) );
			
			call( "on_start_element", toString(localName), toHash(map) );
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		call( "on_end_element", toString( localName ) );
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		RubyString string = toString( String.valueOf(ch, start, length) );
		if (this.cdata) {
			call( "on_cdata_block", string );
		} else {
			call( "on_characters", string );
		}
	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {			
		call( "on_processing_instruction", toString(target), toString(data));
	}

	@Override
	public void startCDATA() {
		this.cdata = true;
	}

	@Override
	public void endCDATA() {
		this.cdata = false;
	}

	@Override
	public void comment(char[] ch, int start, int length) throws SAXException {
		call( "on_comment", toString( String.valueOf(ch, start, length) ) );
	}

}


