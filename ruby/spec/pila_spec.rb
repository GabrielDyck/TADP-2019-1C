require_relative '../lib/models/pila'

describe Pila do
  let(:pila) { Pila.new 3}

  describe 'Invariant tests' do

    it 'Should raise error with negative initial capacity' do
      expect { Pila.new -5}.to raise_error InvariantError
    end

    it 'Should raise error when changing capacity to a negative value' do
      expect { pila.capacity = -5 }.to raise_error InvariantError
    end
  end

  describe 'Precondition tests' do

    it 'Should be full' do
      pila.push 1
      pila.push 2
      pila.push 3
      expect {pila.push 4}.to raise_error RuntimeError
    end

    it 'Top should be empty' do
      expect { pila.top }.to raise_error RuntimeError
    end

    it 'Pop should be empty' do
      expect { pila.pop }.to raise_error RuntimeError
    end
  end
end