require_relative '../lib/models/guerrero'

describe Guerrero do
  let(:highlander) { Guerrero.new 100, 50}
  let(:alf) { Guerrero.new 10, 10}

  describe 'highlander vs alf' do

    it 'Should raise error with health points with less than 0' do
      expect { alf.health_points = -50 }.to raise_error InvariantError
    end

    it 'Should raise error with health points with less than 0' do
      expect { highlander.attack alf }.to raise_error InvariantError
    end

    it 'Should raise error with force with less than 0' do
      expect { highlander.force_decrease 60 }.to raise_error InvariantError
    end

    it 'Should raise error with force over 100' do
      expect { alf.force_increase 500}.to raise_error InvariantError
    end

    it 'Should raise error with negative health points' do
      expect { Guerrero.new -500, 100}.to raise_error InvariantError
    end

    it 'Should attack normally' do
      alf.attack highlander
      expect(highlander.health_points).to eq 90
    end
  end
end