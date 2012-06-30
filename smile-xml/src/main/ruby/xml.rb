require 'java'
require File.join( File.dirname( __FILE__), 'smile-xml.jar' )

ruby = JRuby.runtime
Java::smile.xml.SmileXML.define(ruby);
