require_relative '../lib/modules/annotation'
require_relative '../lib/modules/invariant_module'

class Class
  include Annotation
  include Invariant_module

  attr_accessor :new_method

  def attr_accessor(*vars)
    attributes.concat vars
    super(*vars)
  end

  def attributes
    @attributes ||= []
  end

  def init_new_method
    if new_method.nil?
      @new_method = true
    end
  end

  def method_added(name)
    init_new_method
    if self.new_method && !attributes.include?(name)# Sin esto entra en loop con metodos nuevos y getters
      self.new_method = false
      old_method = instance_method name
      parameters = old_method.parameters.map &:last
      add_precondition name if self.new_precondition
      add_post_condition name if self.new_post_condition

      define_method(name) do |*args, &block|
        self.class.parse_parameters(args, parameters)
        self.class.call_preconditions(name, self)
        self.class.call_procs_before self
        ret = old_method.bind(self).call *args, &block
        self.class.call_procs_after self
        self.class.call_post_conditions(name, ret, self)
        ret
      end
    end
    self.new_method = true
  end

end