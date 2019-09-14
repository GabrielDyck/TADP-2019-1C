require_relative '../lib/models/mi_clase'

describe MiClase do
  let(:mi_clase) { MiClase.new }

  describe "mensaje_2" do
    it 'Should print before_and_after procs' do
      expect(STDOUT).to receive(:puts).with("Entré a un mensaje")
      expect(STDOUT).to receive(:puts).with("mensaje_2")
      expect(STDOUT).to receive(:puts).with("Salí de un mensaje")
      mi_clase.mensaje_2
    end

    it 'Should return 3' do
      expect( mi_clase.mensaje_2 ).to eq 3
    end

  end

  describe "mensaje nuevo" do

    class MiClase
      def mensaje_3
        puts "mensaje_3"
        123
      end
    end

    it 'Should print before_and_after procs' do
      expect(STDOUT).to receive(:puts).with("Entré a un mensaje")
      expect(STDOUT).to receive(:puts).with("mensaje_3")
      expect(STDOUT).to receive(:puts).with("Salí de un mensaje")
      mi_clase.mensaje_3
    end

    it 'Should return 123' do
      expect( mi_clase.mensaje_3 ).to eq 123
    end
  end

end