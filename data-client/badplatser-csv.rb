#!/usr/bin/env ruby
#
# Convert the CSV-data from havochvatten.se into a CSV format suitable for the csv-to-rest.rb script

require 'csv'

if __FILE__ == $0
  v = CSV.generate do |out|
    out << ["lat", "long", "value", "type"]
    CSV.new(STDIN, :headers => true, :header_converters => :symbol, :converters => :all).to_a.map { |r| r.to_hash }.each do |r|
      out << [r[:latitud], r[:longitud], r[:vattentemperatur], "WATERTEMP"]
    end
  end
  puts v
end
