(ns kwik.errors)

(def ERR_MAPPING_NOT_FOUND {"code" 100 "message" "No mapping exist for specified key"})
(def ERR_TYPE_MISMATCH {"code" 101 "message" "Command specified against wrong type"})
(def ERR_WRONG_ARITY {"code" 102 "message" "Incorrect number of arguments specified"})
(def ERR_COMMAND_NOT_FOUND {"code" 103 "message" "Command not found"})
(def ERR_LIST_EMPTY {"code" 104 "message" "Attempt to pop value off an empty list"})
(def ERR_INVALID_PATTERN {"code" 105 "message" "Invalid pattern"})
(def ERR_MALFORMED_ARGUMENTS {"code" 106 "message" "Command received malformed arguments"})
(def ERR_UNKNOWN {"code" 300 "message" "An unknown error occurred"})


