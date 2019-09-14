require_relative '../../lib/custom_exceptions'

module Annotation

  attr_accessor :new_precondition, :new_post_condition

  def post_conditions
    @post_conditions ||= Hash.new
  end

  def preconditions
    @pre_conditions ||= Hash.new
  end

  def parameters_temp
    @parameters ||= Hash.new
  end

  def add_precondition(name)
    self.preconditions[name] = self.new_precondition
    self.new_precondition = nil
  end

  def add_post_condition(name)
    self.post_conditions[name] = self.new_post_condition
    self.new_post_condition = nil
  end

  def pre(&block)
    raise UniquePreCondition.new if self.new_precondition
    self.new_precondition = Proc.new { |object|
      unless object.instance_eval &block
        raise RuntimeError.new 'Failed to meet preconditions.'
      end
    }
  end

  def post(&block)
    raise UniquePostCondition.new if self.new_post_condition
    self.new_post_condition = Proc.new { |object, ret|
      unless object.instance_exec ret, &block
        raise RuntimeError.new 'Failed to meet post conditions.'
      end
    }
  end

  def parse_parameters(args, parameters)
    parameters.each do |param|
      i = parameters.find_index param
      self.parameters_temp[param] = args[i]
    end if parameters
  end

  def call_preconditions(name, object)
    preconditions[name].call object if preconditions[name]
  end

  def call_post_conditions(name, ret, object)
    post_conditions[name].call object, ret if post_conditions[name]
  end

end