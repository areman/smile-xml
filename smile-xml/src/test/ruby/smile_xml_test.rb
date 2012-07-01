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