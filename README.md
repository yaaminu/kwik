# Kwik

Kwik, a simple KV store written in [clojure](https://clojure.org). It supports three data structures strings, lists and maps.
Kwik speaks http so it's very easy to adopt. All you need to interact with kwik is an http client. The client issues a command to the server and it gets a response back in json. 

## Running Kwik

To run kwik, you need to have the [lein tool](https://leiningen.org/) installed on your system. In the project directory run:

`lein run [port]`

By default kwik listens on port 8000 but you can supply a custom port.

## The KWIK protocol

Assuming the server is running locally on port 8000,
`curl http://localhost:8000/v1/[command]/[arg1]?args=[extra arguments]`

where
 **command**: Any supported command, command names are case insensitive.
 **arg1**: The first argument to the command, often times it's the key and is 		    case sensitive
 **extra-arguments**: Any other arguments expected by the command. 

There's no limitation on the length of keys and values beyond the length permited by the http specs.

**Errors**
All commands return a kwik error on failure and a 4xx http status code. 
A kwik error has two fields, an error code and a message

Valid error codes are:
1. **100** - key does not exist 
2. **101** - command does not support the type of value held by **key**
3. **102** - Incorrect number of arguments 
4. **103** - specified command is unknown
5. **104** - attempt to retrieve a value from an empty list
6. **300** - An unknwon error occured



##USAGE 

**Kwik command grouped by their respective data structures**

## strings
1. GET 
  - Returs the string value mapped to a given key. 
  - example: `curl http://localhost:8000/v1/get/key` 
2. SET 
  - Sets the specified value to a given key
  - returns "OK" on success 
  - example: `curl http://localhost:8000/v1/set/key?args=value`


## Lists
1. LGET [key]
   - Returns the list value stored inside a given key
   - example: `curl http://localhost:8000/v1/lget/key`

2. LSET [key "value1,value2,value3"]
  - Sets a a list of comma-separated values to a given key as list
  - returns the length of the list stored at the given key.
  - example: `curl http://localhost:8000/v1/lset/key?args=value1,value2,value3` 

3. LAPPEND [Key "value1,value2,value3"]
  - Adds the a list of comma-separated values to a given key.
  - returns the new length of the list stored at the given key.
  - example: `curl http://localhost:8000/v1/lappend/key?args=value1,value2,value3` 

4. LPOP [Key]
  - Removes the last entry from a list and returns it.
  - example: `curl http://localhost:8000/v1/lpop/key` 

## Maps
1. MSET [KEY map-key map-value]
   - Stores map-value into a map at map-key stored at KEY
   - example: `curl http://localhost:8000/v1/mset/KEY?args=map-key,map-value`
2. MGET [KEY map-key]
  - Returns the map entry stored at map-key of a map which is stored at KEY
  - example: `curl http://localhost:8000/v1/mget/KEY?args=map-key`

3. MDEL [KEY map-key]
  - Removes a map entry stored at map-key of a map stored at KEY
  - example: `curl http://localhost:8000/v1/mdel/KEY?args=map-key`


## Data structure unspecific commands

1. KEYS [regex]
   - Returns all keys stored on the server that matches the supplied pattern
   - example `curl http://localhost:8000/v1/keys/.?foo$`, it's your duty to url-encode 
     the pattern before making the request.
2. DEL [KEY]
   - Deletes key and the associated value
   - Always returns "OK" even if key does not exist
   - example `curl http://localhost:8000/v1/del/KEY`



#testing

Run `lein test` in the root directory of the project