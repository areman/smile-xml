package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.anno.JRubyConstant;
import org.jruby.anno.JRubyModule;

import smile.xml.util.UtilJ;

@JRubyModule(name = "LibXML::XML::Parser::Options")
public class ParserOptionsJ {

	@JRubyConstant
	public static final String RECOVER = null;
	@JRubyConstant
	public static final String NOENT = null;
	@JRubyConstant
	public static final String DTDLOAD = null;
	@JRubyConstant
	public static final String DTDATTR = null;
	@JRubyConstant
	public static final String DTDVALID = null;
	@JRubyConstant
	public static final String NOERROR = null;
	@JRubyConstant
	public static final String NOWARNING = null;
	@JRubyConstant
	public static final String PEDANTIC = null;
	@JRubyConstant
	public static final String NOBLANKS = null;
	@JRubyConstant
	public static final String SAX1 = null;
	@JRubyConstant
	public static final String XINCLUDE = null;
	@JRubyConstant
	public static final String NONET = null;
	@JRubyConstant
	public static final String NODICT = null;
	@JRubyConstant
	public static final String NSCLEAN = null;
	@JRubyConstant
	public static final String NOCDATA = null;
	@JRubyConstant
	public static final String NOXINCNODE = null;
	@JRubyConstant
	public static final String COMPACT = null;
	@JRubyConstant
	public static final String PARSE_OLD10 = null;
	@JRubyConstant
	public static final String NOBASEFIX = null;
	@JRubyConstant
	public static final String HUGE = null;

	public static RubyModule define(Ruby runtime) {
		return UtilJ.defineModule(runtime, ParserOptionsJ.class);
	}

}
