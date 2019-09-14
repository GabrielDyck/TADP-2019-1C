class Pila
  attr_accessor :current_node, :capacity

  invariant {capacity >= 0}

  post {empty?}
  def initialize(capacity)
    @capacity = capacity
    @current_node = nil
  end

  pre {!full?}
  post {height > 0}
  def push(element)
    @current_node = Node.new(element, current_node)
  end

  pre {!empty?}
  def pop
    element = top
    @current_node = @current_node.next_node
    element
  end

  pre {!empty?}
  def top
    current_node.element
  end

  def height
    empty? ? 0 : current_node.size
  end

  def empty?
    current_node.nil?
  end

  def full?
    height == capacity
  end

  Node = Struct.new(:element, :next_node) do
    def size
      next_node.nil? ? 1 : 1 + next_node.size
    end
  end
end
