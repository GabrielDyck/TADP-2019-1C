class Guerrero

  attr_accessor :health_points, :force

  invariant { health_points > 0 }
  invariant { force > 0 && force < 100 }

  def initialize(health_points, force)
    @health_points = health_points
    @force = force
  end

  def attack(another_warrior)
    another_warrior.health_points -= force
  end

  def force_increase(value)
    self.force += value
  end

  def force_decrease(value)
    self.force -= value
  end

end