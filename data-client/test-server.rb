#!/usr/bin/env ruby

require 'sinatra'
require 'sinatra/json'

post '/upload' do
  data = request.body.read
  puts "Received #{data}"
  "OK"
end
