#!/usr/bin/env ruby
# Read CSV-data from STDIN and feed using POST to REST service @ ENV['REST_URL']

require 'rest-client'
require 'csv'

REST_URL = ENV['REST_URL'] or raise "No REST_URL specified"

def read_data(io)
  CSV.new(io, :headers => true, :header_converters => :symbol).to_a.map { |r| r.to_hash }
end

if __FILE__ == $0
  read_data(STDIN).each do |e|
    RestClient.post(REST_URL, {:latitude => e[:lat],
                               :longitude => e[:long],
                               :type => e[:type],
                               :value => e[:value]}.to_json,
                    :content_type => :json, :accept => :json)
  end
end
