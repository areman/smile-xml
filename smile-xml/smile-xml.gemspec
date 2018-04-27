Dir.chdir( 'dist' )

Gem::Specification.new do |gem|
  
  gem.platform = 'java'
  gem.name     = 'smile-xml'
  gem.version  = '1.0.4'
  gem.date     = '2018-04-27'

  gem.summary     = "LibXMLRuby compatibility layer for jruby"
  gem.description = ""

  gem.authors  = [ 'Andre Kullmann' ]
  gem.email    = 'andre [dot] kullmann [at] googlemail [dot] com'
  gem.homepage = 'https://github.com/areman/smile-xml'

  gem.files = Dir[ '**/*' ]
    
end
