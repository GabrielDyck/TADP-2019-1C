class InvariantError < StandardError
  def initialize(msg = "This is a custom exception", exception_type = "Custom exception")
    @exception_type = exception_type
    super(msg)
  end
end

class UniquePreCondition < StandardError

end

class UniquePostCondition < StandardError

end