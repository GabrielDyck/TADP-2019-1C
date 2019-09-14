module Before_and_after_each_module

  def procs_before
    @procs_before ||= []
  end

  def procs_after
    @procs_after ||= []
  end

  def before_and_after_each_call(proc_before, proc_after)
    #No encontre forma de hacerlo sin arrays
    self.init_new_method
    procs_before << proc_before
    procs_after << proc_after
    #Esta parte es para los setters de las variables de instancia
    self.instance_methods(false).each do |variable|
      old_method = instance_method variable
      define_method(variable.to_s) do |*args, &block|
        ret = old_method.bind(self).call *args, &block
        self.class.call_procs self, self.class.procs_after
        ret
      end if variable.to_s.include? "="
    end

  end

  def call_procs_before(object)
    # Si se redefine el initialize, el before no debe pasar porque los atributos estan sin inicializar
    call_procs object, procs_before unless procs_before.nil? || name.equal?(:initialize)
  end

  def call_procs_after(object)
    call_procs object, procs_after unless procs_after.nil?
  end

  def call_procs(object, procs)
    procs.each do |proc|
      proc.call(object) unless proc.nil?
    end if procs
  end
end
