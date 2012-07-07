require 'java'
require 'rubygems'
require 'test/unit'
#require 'xml'

ruby = JRuby.runtime
Java::smile.xml.SmileXML.define(ruby);

#XML = LibXML

#class SmileXmlTest < Test::Unit::TestCase
  
#end

Dir.chdir( File.dirname( __FILE__ ) )

require File.dirname( __FILE__ ) + '/tc_node'
  
require File.dirname( __FILE__ ) + '/tc_sax_parser'

require File.dirname( __FILE__ ) + '/tc_document'
  
require File.dirname( __FILE__ ) + '/tc_xpath_expression'

require File.dirname( __FILE__ ) + '/tc_document_write'

require File.dirname( __FILE__ ) + '/tc_attr'

require File.dirname( __FILE__ ) + '/tc_attributes'

require File.dirname( __FILE__ ) + '/tc_node_cdata'
  
require File.dirname( __FILE__ ) + '/tc_node_comment'

require File.dirname( __FILE__ ) + '/tc_node_copy'

require File.dirname( __FILE__ ) + '/tc_node_text'

require File.dirname( __FILE__ ) + '/tc_node_edit'


puts "TODO #{__FILE__} #{__LINE__}"
#require File.dirname( __FILE__ ) + '/tc_node_xlink'

require File.dirname( __FILE__ ) + '/tc_parser'
#
#require File.dirname( __FILE__ ) + '/tc_properties'

require File.dirname( __FILE__ ) + '/tc_schema'

puts "TODO #{__FILE__} #{__LINE__}"
#require File.dirname( __FILE__ ) + '/tc_traversal'

puts "TODO #{__FILE__} #{__LINE__}"
#require File.dirname( __FILE__ ) + '/tc_xml'

puts "TODO #{__FILE__} #{__LINE__}"
#require File.dirname( __FILE__ ) + '/tc_xpointer'

require File.dirname( __FILE__ ) + '/tc_xpath'

#puts "TODO #{__FILE__} #{__LINE__}"
##require File.dirname( __FILE__ ) + '/tc_error'

