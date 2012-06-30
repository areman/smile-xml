package smile.xml;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;

public class BaseJ<T> extends RubyObject
{
  private static final long serialVersionUID = 4634403693609027673L;
  private T javaObject;

  public BaseJ(Ruby ruby, RubyClass clazz)
  {
    super(ruby, clazz);
  }

  public T getJavaObject() {
    return this.javaObject;
  }

  public void setJavaObject(T javaObject) {
    this.javaObject = javaObject;
  }
}