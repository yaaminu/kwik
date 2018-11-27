(ns kwik.errors)

(def ERR_MAPPING_NOT_FOUND {"code" 100 "message" "No mapping exist for specified key"})
(def ERR_TYPE_MISMATCH {"code" 101 "message" "Command specified against wrong type"})
(def ERR_WRONG_ARITY {"code" 102 "message" "Incorrect number of arguments specified"})
(def ERR_COMMAND_NOT_FOUND {"code" 103 "message" "Command not found"})


