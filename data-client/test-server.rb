#!/usr/bin/env ruby

require 'sinatra'
require 'sinatra/json'

after do
  headers "Access-Control-Allow-Origin" => "*",
          "Access-Control-Allow-Headers" => "content-type"
end

options '/' do
  
end

post '/' do
  data = request.body.read
  puts "Received #{data}"
  "OK"
end
