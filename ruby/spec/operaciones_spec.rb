require_relative '../lib/models/operaciones'

describe Operaciones do
  let(:operacion) { Operaciones.new }

  describe 'dividir' do

    it 'Should raise error when divisor is 0' do
      expect { operacion.dividir 4, 0 }.to raise_error RuntimeError
    end

    it 'Should work normally' do
      expect(operacion.dividir 4, 2).to eq 2
    end

  end

  describe 'restar' do

    it 'Should finish without error' do
      expect( operacion.restar 4, 0 ).to eql 4
    end

    it 'Should finish without error with new method with the same name as parameter'do
      class Operaciones
        def minuendo
          10
        end
      end
      expect(operacion.restar 4, 2).to eq 2
      expect(operacion.minuendo).to eq 10
    end

  end

  describe 'restar_positivo' do

    it 'Should raise error when result is not positive' do
      expect { operacion.restar_positivo 5,10}.to raise_error RuntimeError
    end
  end

  describe 'annotations' do
    it 'Should raise error with more than one precondition' do
      expect{
        class Operaciones
          pre{puts "hola"}
          pre{puts "rompe"}
          def un_metodo
            1
          end
        end
      }.to raise_error UniquePreCondition
    end

    it 'Should raise error with more than one post_condition' do
      expect{
        class Operaciones
          post{|ret| puts "hola"}
          post{|ret| puts "rompe"}
          def un_metodo
            1
          end
        end
      }.to raise_error UniquePostCondition
    end

    it 'Should raise error with more than one precondition and only a post_condition' do
      expect{
        class Operaciones
          pre{puts "hola"}
          post{|ret| puts "post"}
          pre{puts "rompe"}
          def un_metodo
            1
          end
        end
      }.to raise_error UniquePreCondition
    end
  end
end