Dir.chdir( 'dist' )

Gem::Specification.new do |gem|
  
  gem.platform = 'jruby'
  gem.name     = 'smile-xml'
  gem.version  = '1.0.2'
  gem.date     = '2012-07-06'

  gem.summary     = "LibXML API for JRuby"
  gem.description = ""

  gem.authors  = [ 'Andre Kullmann' ]
  gem.email    = 'andre [dot] kullmann [at] googlemail [dot] com'
  gem.homepage = 'https://github.com/areman/smile-xml'

  gem.files = Dir[ '**/*' ]
    
end