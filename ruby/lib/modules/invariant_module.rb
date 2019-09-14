require_relative '../../lib/modules/before_and_after_each_module'

module Invariant_module

  include Before_and_after_each_module

  def invariant(&proc_invariant)
    #Solo debe controlar el estado despues de la ejecucion de un mensaje
    before_and_after_each_call nil, proc {|object|
      unless object.instance_eval &proc_invariant
        raise InvariantError.new "Invalid state"
      end
    }
  end
end
