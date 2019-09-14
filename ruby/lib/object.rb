class Object

  def method_missing(sym, *args, &block)
    if self.class.parameters_temp.include? sym
      self.class.parameters_temp[sym]
    else
      super sym, *args, &block
    end
  end

end
