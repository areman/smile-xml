# encoding: UTF-8

require './test_helper'

require 'test/unit'

# attributes is deprecated - use attributes instead.
# Tests for backwards compatibility

class Testattributes < Test::Unit::TestCase
  def setup()
    xp = XML::Parser.string('<ruby_array uga="booga" foo="bar"><fixnum>one</fixnum><fixnum>two</fixnum></ruby_array>')
    @doc = xp.parse
  end

  def teardown()
    @doc = nil
  end

  def test_traversal
    attributes = @doc.root.attributes
    
    assert_instance_of(XML::Attributes, attributes)
    f = attributes.first
    l = f.next
    assert( f.name.eql?( 'uga' ) || f.name.eql?( 'foo' ) )
    assert( l.name.eql?( 'uga' ) || l.name.eql?( 'foo' ) )

    n = f.next
    assert_instance_of(XML::Attr, n)
    assert( n.name.eql?( 'uga' ) || n.name.eql?( 'foo' ) )

  end
  
  def test_no_attributes
    attributes = @doc.root.child.attributes
    assert_instance_of(XML::Attributes, attributes)
    assert_equal(0, attributes.length)
  end
end
