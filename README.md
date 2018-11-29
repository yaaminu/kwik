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

##Design considerations

- Every kwik response is json compatible. The response could 
  either be a json object or a json primitive.
- The protocol is a minimal semantic built on top http making it 
easy to use. 
- All commands take at least one argument which is specified as an extra path to the command. eg /v1/GET/arg1[?args=extra-aguments]. Any extra argument is specified in the arg query. The extra arguments are fed to the command handlers in the argV vector.
- The kwik database itself is a map but the values are wrapped in a value object and stored with the type information. This way, commands can detect unsupported data types and err gracefully.
- In code, all command handers return a tuple indicating a success and an error. No exception is deliberately thrown, this makes the code easy to reason about 
- The kwik server returns http 4xx error code when a command errs. 
- Finally to add a new command, all one has to do is to define a handler function and add it as an entry to the command-table. Kwik server will pick it up when a client issues that command

##Limitations
- Since the protocol runs on http, which is text based, values like `nil` cannot be represented so when a mapping does not exist, an error is returned so clients can handle it gracefully
- kwik is in-memory so a server restart will lead to data loss.
- All extra arguments passed to kwik is comma delimited, this means a command cannot process any argument that contains a comma.
- All arguments passed to command must be url-encoded by clients if they contain contain-url unsafe characters. 
- Due to the fact that clojure data structures are immutable, any mutation will lead to a new copy. For a large dataset, this can be prohibitively expensive.
   

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
1. MSET [KEY "key1 value1" "key2 value2" ...]
   - Takes a list of space separated values, transform each value to key-val pairs and puts them into a map at KEY. It's important to ensure that each value is space separated or you'll get ERR_MALFORMED_ARGUMENTS error.
  -example: `curl http://localhost:8000/v1/mset/KEY?args=key1%20va1key2%20val2`

2. MGET [KEY map-key]
  - Returns the map entry stored at map-key of a map which is stored at KEY
  - example: `curl http://localhost:8000/v1/mget/KEY?args=map-key`
3. MGETALL [KEY]
  - Returns all entries of the map stored at KEY
  - example `curl http://localhost:8000/v1/mgetall/KEY`
4. MDEL [KEY map-key]
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